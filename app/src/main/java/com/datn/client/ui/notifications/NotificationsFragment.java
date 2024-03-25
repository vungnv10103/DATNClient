package com.datn.client.ui.notifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.client.R;
import com.datn.client.adapter.NotificationAdapter;
import com.datn.client.databinding.FragmentNotificationsBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageResponse;
import com.datn.client.models.Notification;
import com.datn.client.models.OverlayMessage;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.ui.components.MyOverlayMsgDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.MyFormat;
import com.datn.client.utils.PreferenceManager;
import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;

import com.datn.client.ui.BasePresenter.STATUS_NOTIFICATION;

import java.util.List;

public class NotificationsFragment extends Fragment implements INotificationView {
    private static final String TAG = NotificationsFragment.class.getSimpleName();

    private FragmentNotificationsBinding binding;

    private NotificationPresenter notificationPresenter;
    private PreferenceManager preferenceManager;

    private CircularProgressIndicator progressBarLoading;
    private RecyclerView rcvNotification;

    private Customer mCustomer;
    private String mToken;

    private NotificationAdapter notificationAdapter;
    private List<Notification> mNotificationList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        initUI();
        preferenceManager = new PreferenceManager(requireActivity(), Constants.KEY_PREFERENCE_ACC);
        checkLogin();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEventClick();
        initService();

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
                notificationAdapter = new NotificationAdapter(getActivity(), mNotificationList,
                        notification -> notificationPresenter.updateStatusNotification(notification.get_id(), STATUS_NOTIFICATION.SEEN.getValue()));
                LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                rcvNotification.setLayoutManager(llm);
                rcvNotification.setAdapter(notificationAdapter);
                MaterialDividerItemDecoration divider = new MaterialDividerItemDecoration(requireActivity(), MaterialDividerItemDecoration.VERTICAL);
                rcvNotification.addItemDecoration(divider);
                float dipStart = 72f; // width image + padding view
                float dipEnd = 8f;
                divider.setDividerInsetStart(MyFormat.convertDPtoPx(requireActivity(), dipStart));
                divider.setDividerInsetEnd(MyFormat.convertDPtoPx(requireActivity(), dipEnd));
                divider.setLastItemDecorated(false);
            } else {
                notificationAdapter.updateList(mNotificationList);
            }
            setLoading(false);
        });
    }

    private void onNotificationLoaded() {
        displayNotification();
        progressBarLoading.setVisibility(View.GONE);
        rcvNotification.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListNotification(List<Notification> notificationList) {
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

    }

    private void setLoading(boolean isLoading) {
        progressBarLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        rcvNotification.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void initUI() {
        progressBarLoading = binding.progressbarLoading;
        rcvNotification = binding.rcvNotification;
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}