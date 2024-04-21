package com.datn.client.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.datn.client.R;
import com.datn.client.activity.SettingActivity;
import com.datn.client.databinding.FragmentDashboardBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageDetailResponse;
import com.datn.client.models.Notification;
import com.datn.client.models.OverlayMessage;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.chat.ConversationActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.ui.components.MyOverlayMsgDialog;
import com.datn.client.ui.order.OrderActivity;
import com.datn.client.utils.Constants;
import com.datn.client.utils.ManagerUser;
import com.datn.client.utils.PreferenceManager;

import java.util.List;

public class DashboardFragment extends Fragment implements IDashboardView {

    private FragmentDashboardBinding binding;

    private DashboardPresenter dashboardPresenter;

    private PreferenceManager preferenceManager;

    private Button btnLogout;
    private Customer mCustomer;
    private String mToken;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        initUI();
        preferenceManager = new PreferenceManager(requireActivity(), Constants.KEY_PREFERENCE_ACC);
        mCustomer = ManagerUser.gI().getCustomerLogin(requireActivity());
        mToken = ManagerUser.gI().checkToken(requireActivity());
        if (mCustomer == null || mToken == null) {
            reLogin();
        }
        setDataUser();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEventClick();
        initService();
    }

    private void doLogout() {
        dashboardPresenter.logout();
    }

    @Override
    public void onThrowMessage(MessageDetailResponse message) {
        if (message != null) {
            MyDialog.gI().startDlgOK(requireActivity(), message.getContent());
        }
    }

    @Override
    public void onListNotification(List<Notification> notificationList) {

    }

    @Override
    public void onListOverlayMessage(List<OverlayMessage> overlayMessages) {
        MyOverlayMsgDialog.gI().showOverlayMsgDialog(requireActivity(), overlayMessages, dashboardPresenter);
    }
    @Override
    public void onThrowNotification(String notification) {
        MyDialog.gI().startDlgOK(requireActivity(), notification);
    }

    @Override
    public void onThrowLog(String key, String message) {
        Log.w(key, "onThrowLog: " + message);
    }

    @Override
    public void onFinish() {
        MyDialog.gI().startDlgOK(requireActivity(), "onFinish");
    }

    @Override
    public void onLogout() {
        showToast("Đăng xuất thành công");
        preferenceManager.clear();
        startActivity(new Intent(requireActivity(), LoginActivity.class));
        requireActivity().finishAffinity();
    }

    private void initService() {
        ApiService apiService = RetrofitConnection.getApiService();
        dashboardPresenter = new DashboardPresenter(requireActivity(), this, apiService, mToken, mCustomer.get_id());
    }

    private void setDataUser() {
        binding.tvEmail.setText(mCustomer.getEmail());
        binding.tvName.setText(mCustomer.getFull_name());
        Glide.with(requireActivity())
                .load(mCustomer.getAvatar())
                .error(R.drawable.logo_app_gradient)
                .into(binding.imgAvatarUser);
    }

    private void showToast(@NonNull Object message) {
        Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
    }


    private void reLogin() {
        showToast(getString(R.string.please_log_in_again));
        preferenceManager.clear();
        startActivity(new Intent(requireActivity(), LoginActivity.class));
        requireActivity().finishAffinity();
    }


    private void initEventClick() {
        btnLogout.setOnClickListener(v -> doLogout());
        binding.layoutSetting.setOnClickListener(v -> startActivity(new Intent(requireActivity(), SettingActivity.class)));
        binding.layoutOrder.setOnClickListener(v -> startActivity(new Intent(requireActivity(), OrderActivity.class)));
        binding.layoutMessage.setOnClickListener(v -> startActivity(new Intent(requireActivity(), ConversationActivity.class)));
    }


    private void initUI() {
        btnLogout = binding.btnLogout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        dashboardPresenter.onCancelAPI();
    }
}
