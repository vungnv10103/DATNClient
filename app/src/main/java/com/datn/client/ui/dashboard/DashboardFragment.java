package com.datn.client.ui.dashboard;

import android.content.Context;
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
import com.datn.client.activity.TestActivity;
import com.datn.client.databinding.FragmentDashboardBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageResponse;
import com.datn.client.models.Notification;
import com.datn.client.models.OverlayMessage;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.ui.components.MyOverlayMsgDialog;
import com.datn.client.ui.order.OrderActivity;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.google.gson.Gson;

import java.util.ArrayList;
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
        checkLogin();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEventClick();
        initService();
//        MyDialog.gI().startDlgOK(requireActivity(), getString(R.string.app_name), getString(R.string.app_name), null, null);
    }

    private void doLogout() {
        dashboardPresenter.logout();
    }

    @Override
    public void onThrowMessage(MessageResponse message) {
        MyDialog.gI().startDlgOK(requireActivity(), message.getContent());
    }

    @Override
    public void onListNotification(List<Notification> notificationList) {

    }

    @Override
    public void onListOverlayMessage(List<OverlayMessage> overlayMessages) {
        MyOverlayMsgDialog.gI().showOverlayMsgDialog(requireActivity(), overlayMessages, dashboardPresenter);
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
        switchToLogin();
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
        setDataUser();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void switchToLogin() {
        preferenceManager.putBoolean("isRemember", false);
        preferenceManager.putString("token", "");

        startActivity(new Intent(requireActivity(), LoginActivity.class));
        requireActivity().finishAffinity();
    }

    private void reLogin() {
        showToast(getString(R.string.please_log_in_again));
        requireActivity().finishAffinity();
    }


    private void initEventClick() {
        binding.layoutSetting.setOnClickListener(v -> startActivity(new Intent(requireActivity(), SettingActivity.class)));
        binding.layoutOrder.setOnClickListener(v -> startActivity(new Intent(requireActivity(), OrderActivity.class)));
    }


    private void initUI() {
        btnLogout = binding.btnLogout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
