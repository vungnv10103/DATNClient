package com.datn.client.ui.notifications;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.BackEventCompat;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.adapter.NotificationAdapter;
import com.datn.client.databinding.FragmentNotificationsBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageResponse;
import com.datn.client.models.Notification;
import com.datn.client.models.OverlayMessage;
import com.datn.client.models._BaseModel;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.BasePresenter.STATUS_NOTIFICATION;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.ui.components.MyOverlayMsgDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.MyFormat;
import com.datn.client.utils.PreferenceManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NotificationsFragment extends Fragment implements INotificationView {
    private static final String TAG = NotificationsFragment.class.getSimpleName();

    private FragmentNotificationsBinding binding;

    private NotificationPresenter notificationPresenter;
    private PreferenceManager preferenceManager;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private MaterialToolbar topAppBar;
    private CircularProgressIndicator progressBarLoading;
    private RecyclerView rcvNotification;
    private TextView tvQuantitySelected;
    private CheckBox cbSelectedAllNotification;

    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private Fragment currentFragment;

    private Customer mCustomer;
    private String mToken;

    private NotificationAdapter notificationAdapter;
    private List<Notification> mNotificationList;
    private final List<String> mListNotificationID = new ArrayList<>();
    private boolean isLayoutActionShow = false;
    private boolean isSelectedAll = false;

    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (isLayoutActionShow) {
                resetAction();
            } else {
                if (currentFragment instanceof NotificationsFragment) {
                    navController.popBackStack();
                }
            }
        }

        @Override
        public void handleOnBackCancelled() {
            super.handleOnBackCancelled();
            MyDialog.gI().startDlgOK(requireActivity(), "handleOnBackCancelled");
        }

        @Override
        public void handleOnBackProgressed(@NonNull BackEventCompat backEvent) {
            super.handleOnBackProgressed(backEvent);
            MyDialog.gI().startDlgOK(requireActivity(), "handleOnBackProgressed");
        }

        @Override
        public void handleOnBackStarted(@NonNull BackEventCompat backEvent) {
            super.handleOnBackStarted(backEvent);
            MyDialog.gI().startDlgOK(requireActivity(), "handleOnBackStarted");
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        initUI();

        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), callback);
        preferenceManager = new PreferenceManager(requireActivity(), Constants.KEY_PREFERENCE_ACC);
        checkLogin();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initService();
        initEventClick();

    }

    @Override
    public void onStart() {
        super.onStart();

        setLoading(true);
        notificationPresenter.getNotification();
    }

    private void displayNotification() {
        requireActivity().runOnUiThread(() -> {
            if (getContext() != null && getContext() instanceof Activity && !((Activity) getContext()).isFinishing()) {
                if (mNotificationList.isEmpty()) {
                    if (notificationAdapter != null) {
                        System.out.println("displayNotification");
                        notificationAdapter.updateList(mNotificationList);
                    }
                    showToast("No notification");
                    return;
                }
            }
            if (notificationAdapter == null) {
                notificationAdapter = new NotificationAdapter(getActivity(), mNotificationList, new IAction() {
                    @Override
                    public void onClick(_BaseModel notification) {
                        MyDialog.gI().startDlgOKWithAction(requireActivity(), "Do you want update this!", notification.get_id(),
                                (dialog, which) -> notificationPresenter.updateStatusNotification(notification.get_id(), STATUS_NOTIFICATION.SEEN.getValue()), (dialog, which) -> dialog.dismiss());
                    }

                    @Override
                    public void onLongClick(_BaseModel notification) {
                        if (NotificationAdapter.isShowSelected) {
                            isLayoutActionShow = true;
                            bottomNavigationView.setVisibility(View.GONE);
                            binding.layoutActionTop.setVisibility(View.VISIBLE);
                            tvQuantitySelected.setText(String.format(getString(R.string.selected) + 0));
                            binding.layoutActionBottom.setVisibility(View.VISIBLE);
                            scalePaddingRcvNotification();
                            setColorActionBottom(false);
                        } else {
                            isLayoutActionShow = false;
                            binding.layoutActionTop.setVisibility(View.GONE);
                            binding.layoutActionBottom.setVisibility(View.GONE);
                            bottomNavigationView.setVisibility(View.VISIBLE);
                            scalePaddingRcvNotification();
                        }
                    }

                    @Override
                    public void onItemClick(_BaseModel notification) {
                        if (NotificationAdapter.isShowSelected) {
                            if (NotificationAdapter.isChecked) {
                                mListNotificationID.add(notification.get_id());
                                NotificationAdapter.isChecked = false;
                            } else {
                                mListNotificationID.remove(notification.get_id());
                            }
                            cbSelectedAllNotification.setChecked(mListNotificationID.size() == mNotificationList.size());
                            tvQuantitySelected.setText(String.format(getString(R.string.selected) + mListNotificationID.size()));
                            setColorActionBottom(!mListNotificationID.isEmpty());
                        }
                    }
                });
                LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                rcvNotification.setLayoutManager(llm);
                rcvNotification.setAdapter(notificationAdapter);
                MaterialDividerItemDecoration divider = new MaterialDividerItemDecoration(requireActivity(), MaterialDividerItemDecoration.VERTICAL);
                rcvNotification.addItemDecoration(divider);
                float dipStart = 72f + 8f; // width image + padding view
                float dipEnd = 8f;
                divider.setDividerInsetStart(MyFormat.convertDPtoPx(requireActivity(), dipStart));
                divider.setDividerInsetEnd(MyFormat.convertDPtoPx(requireActivity(), dipEnd));
                divider.setLastItemDecorated(false);
            } else {
                notificationAdapter.updateList(mNotificationList);
            }
            mySwipeRefreshLayout.setRefreshing(false);
            setLoading(false);
        });
    }

    private void onNotificationLoaded() {
        displayNotification();
        progressBarLoading.setVisibility(View.GONE);
        rcvNotification.setVisibility(View.VISIBLE);
    }

    private void resetAction() {
        isLayoutActionShow = false;
        NotificationAdapter.isShowSelected = false;
        NotificationAdapter.isChecked = false;
        if (notificationAdapter != null) {
            notificationAdapter.notifyItemRangeChanged(0, notificationAdapter.getItemCount());
        }
        binding.layoutActionTop.setVisibility(View.GONE);
        binding.layoutActionBottom.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.VISIBLE);
        scalePaddingRcvNotification();
    }

    @Override
    public void onListNotification(List<Notification> notificationList) {
        Collections.reverse(notificationList);
        this.mNotificationList = notificationList;
        onNotificationLoaded();
    }

    @Override
    public void onListOverlayMessage(List<OverlayMessage> overlayMessages) {
        MyOverlayMsgDialog.gI().showOverlayMsgDialog(requireActivity(), overlayMessages, notificationPresenter);
    }

    @Override
    public void onThrowMessage(@NonNull MessageResponse message) {
        switch (message.getCode()) {
            case "overlay/update-status-success":
            case "notification/update-status-success":
                showLogW(message.getTitle(), message.getContent());
                break;
            default:
                MyDialog.gI().startDlgOK(requireActivity(), message.getContent());
                break;
        }
    }

    @Override
    public void onThrowLog(String key, String message) {
        showLogW(key, message);
    }

    @Override
    public void onFinish() {
        switchToLogin();
    }

    private void initService() {
        ApiService apiService = RetrofitConnection.getApiService();
        notificationPresenter = new NotificationPresenter(requireActivity(), this, apiService, mToken, mCustomer.get_id());
    }

    private void initEventClick() {
        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            notificationAdapter = null;
            notificationPresenter.getNotification();
        });
        binding.btnClose.setOnClickListener(v -> resetAction());
        topAppBar.setOnMenuItemClickListener(item -> {
            int itemID = item.getItemId();
            if (itemID == R.id.item_selected_all) {
                isSelectedAll = !isSelectedAll;
                doDeleteAll();
                return true;
            } else if (itemID == R.id.item_delete) {
                MyDialog.gI().startDlgOK(requireActivity(), item.getTitle() + "");
                return true;
            } else if (itemID == R.id.item_seen_all) {
                MyDialog.gI().startDlgOK(requireActivity(), item.getTitle() + "");
                return true;
            }
            MyDialog.gI().startDlgOK(requireActivity(), "Option: " + item.getTitle());
            return false;
        });
        cbSelectedAllNotification.setOnClickListener(v -> {
            isSelectedAll = cbSelectedAllNotification.isChecked();
            doSelectedAll();
        });

        binding.layoutSeenAll.setOnClickListener(v -> doSeenAll());
        binding.btnSeen.setOnClickListener(v -> doSeenAll());

        binding.layoutDelete.setOnClickListener(v -> doDeleteAll());
        binding.btnDelete.setOnClickListener(v -> doDeleteAll());
    }

    private void doSeenAll() {
        if (NotificationAdapter.isShowSelected) {
            if (mListNotificationID.isEmpty()) {
                return;
            }
            MyDialog.gI().startDlgOK(requireActivity(), mListNotificationID.size() + "");
        }
    }

    private void doDeleteAll() {
        if (NotificationAdapter.isShowSelected) {
            if (mListNotificationID.isEmpty()) {
                return;
            }
            MyDialog.gI().startDlgOK(requireActivity(), mListNotificationID.size() + "");
        }
    }

    private int getViewHeight(@NonNull View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }


    private void setMenuAction() {
        topAppBar.inflateMenu(R.menu.bottom_action_menu);
    }

    private void doSelectedAll() {
        mListNotificationID.clear();
        for (int i = 0; i < mNotificationList.size(); i++) {
            Notification notification = mNotificationList.get(i);
            notification.setChecked(isSelectedAll);
            notificationAdapter.notifyItemChanged(i);
            if (isSelectedAll) {
                mListNotificationID.add(notification.get_id());
            } else {
                mListNotificationID.remove(notification.get_id());
            }
        }
        tvQuantitySelected.setText(String.format(getString(R.string.selected) + mListNotificationID.size()));
        setColorActionBottom(!mListNotificationID.isEmpty());
    }

    private void setLoading(boolean isLoading) {
        progressBarLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        rcvNotification.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void setColorActionBottom(boolean isEnable) {
        ColorStateList colorGray = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_500));
        int primaryColor = getColorPrimary();
        ColorStateList colorStatePrimary = getColorStateList(primaryColor);
        if (isEnable) {
            binding.btnSeen.setIconTint(colorStatePrimary);
            binding.tvSeen.setTextColor(primaryColor);
            binding.btnDelete.setIconTint(colorStatePrimary);
            binding.tvDelete.setTextColor(primaryColor);
            binding.btnMore.setIconTint(colorStatePrimary);
            binding.tvMore.setTextColor(primaryColor);
        } else {
            binding.btnSeen.setIconTint(colorGray);
            binding.tvSeen.setTextColor(colorGray);
            binding.btnDelete.setIconTint(colorGray);
            binding.tvDelete.setTextColor(colorGray);
            binding.btnMore.setIconTint(colorGray);
            binding.tvMore.setTextColor(colorGray);
        }
    }

    @NonNull
    private static ColorStateList getColorStateList(int primaryColor) {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed}  // pressed
        };

        int[] colors = new int[]{
                primaryColor,
                Color.RED,
                Color.GREEN,
                Color.BLUE
        };
        return new ColorStateList(states, colors);
    }


    private int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = requireActivity().getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        @SuppressLint("Recycle") TypedArray arr =
                requireActivity().obtainStyledAttributes(typedValue.data, new int[]{
                        android.R.attr.textColorPrimary});
        return arr.getColor(0, -1);
    }

    private void scalePaddingRcvNotification() {
        int padding = 16 * 2;
        int heightBottomAction = getViewHeight(binding.layoutActionBottom) + padding;
        int bottom;
        if (isLayoutActionShow) {
            bottom = heightBottomAction + padding;
        } else {
            bottom = padding;
        }
        rcvNotification.setPadding(0, 0, 0, bottom);
    }

    private void initUI() {
        mySwipeRefreshLayout = binding.swipeRefreshNotification;
        //        mySwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.GREEN);
        topAppBar = binding.topAppBar;
        progressBarLoading = binding.progressbarLoading;
        rcvNotification = binding.rcvNotification;
        scalePaddingRcvNotification();
        bottomNavigationView = requireActivity().findViewById(R.id.nav_view);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        if (navHostFragment != null) {
            currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
        }
        tvQuantitySelected = binding.tvQuantity;
        cbSelectedAllNotification = binding.cbSelectedAll;
    }

    public void switchToLogin() {
        preferenceManager.clear();
        startActivity(new Intent(requireActivity(), LoginActivity.class));
        requireActivity().finishAffinity();
    }


    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showLogW(String key, String message) {
        Log.w(TAG, key + ": " + message);
    }

    private void reLogin() {
        showToast(getString(R.string.please_log_in_again));
        requireActivity().finishAffinity();
    }

    private Customer getLogin() {
        Gson gson = new Gson();
        String json = preferenceManager.getString("user");
        return gson.fromJson(json, Customer.class);
    }

    private void checkLogin() {
        mCustomer = getLogin();
        if (mCustomer == null) {
            reLogin();
            return;
        }
        mToken = preferenceManager.getString("token");
        if (mToken == null || mToken.isEmpty()) {
            reLogin();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        resetAction();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        notificationPresenter.onCancelAPI();
    }
}