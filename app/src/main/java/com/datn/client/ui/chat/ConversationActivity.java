package com.datn.client.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.adapter.ConversationAdapter;
import com.datn.client.databinding.ActivityConversationBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageResponse;
import com.datn.client.models.Notification;
import com.datn.client.models.OverlayMessage;
import com.datn.client.models._BaseModel;
import com.datn.client.response.Demo;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.ui.components.MyOverlayMsgDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.ManagerUser;
import com.datn.client.utils.PreferenceManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ConversationActivity extends AppCompatActivity implements IChatView {
    private static final String TAG = ConversationActivity.class.getSimpleName();
    private ActivityConversationBinding binding;

    private ConversationPresenter conversationPresenter;

    private PreferenceManager preferenceManager;


    private Customer mCustomer;
    private String mToken;

    private List<Demo> mConversation;
    private ConversationAdapter conversationAdapter;
    private RecyclerView rcvConversation;
    private CircularProgressIndicator progressLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.conversation), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).hide();


        initUI();
        checkRequire();
        initService();


    }

    @Override
    protected void onStart() {
        super.onStart();

        setLoading(true);
        conversationPresenter.getDataConversation();
    }

    private void setLoading(boolean isLoading) {
        progressLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        rcvConversation.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
    }

    private void displayConversation() {
        mConversation.sort(Comparator.comparing(Demo::getCreated_at).reversed());
        conversationAdapter = new ConversationAdapter(ConversationActivity.this, mConversation, new IAction() {
            @Override
            public void onClick(_BaseModel conversation) {
                MyDialog.gI().startDlgOK(ConversationActivity.this, conversation.get_id());
            }

            @Override
            public void onLongClick(_BaseModel conversation) {
                MyDialog.gI().startDlgOK(ConversationActivity.this, conversation.get_id());
            }

            @Override
            public void onItemClick(_BaseModel conversation) {
                MyDialog.gI().startDlgOK(ConversationActivity.this, conversation.get_id());
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ConversationActivity.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        rcvConversation.setLayoutManager(linearLayoutManager);
        rcvConversation.setAdapter(conversationAdapter);
        setLoading(false);
    }

    @Override
    public void onListConversation(List<Demo> dataConversation) {
        if (dataConversation != null) {
            this.mConversation = dataConversation;
            displayConversation();
        } else {
            showToast("no data");
        }
    }

    @Override
    public void onListNotification(List<Notification> notificationList) {
        Log.d(TAG, "onListNotification: " + notificationList);
    }

    @Override
    public void onListOverlayMessage(List<OverlayMessage> overlayMessages) {
        MyOverlayMsgDialog.gI().showOverlayMsgDialog(ConversationActivity.this, overlayMessages, conversationPresenter);
    }

    @Override
    public void onThrowMessage(MessageResponse message) {
        if (message != null) {
            switch (message.getCode()) {
                case "overlay/update-status-success":
                case "notification/update-status-success":
                    showLogW(message.getTitle(), message.getContent());
                    break;
                default:
                    MyDialog.gI().startDlgOK(ConversationActivity.this, message.getContent());
                    break;
            }
        }
    }

    @Override
    public void onThrowLog(String key, String message) {
        showLogW(key, message);
        setLoading(false);
    }

    @Override
    public void onFinish() {
        reLogin();
    }


    private void initUI() {
        progressLoading = binding.progressbarConversation;
        rcvConversation = binding.rcvConversation;
    }

    private void initService() {
        ApiService apiService = RetrofitConnection.getApiService();
        conversationPresenter = new ConversationPresenter(ConversationActivity.this, this, apiService, mToken, mCustomer.get_id());
    }

    private void checkRequire() {
        preferenceManager = new PreferenceManager(ConversationActivity.this, Constants.KEY_PREFERENCE_ACC);
        mCustomer = ManagerUser.gI().checkCustomer(this);
        mToken = ManagerUser.gI().checkToken(this);
        if (mCustomer == null || mToken == null) {
            reLogin();
        }
    }


    private void showToast(String message) {
        Toast.makeText(ConversationActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLogW(String key, String message) {
        MyDialog.gI().startDlgOK(ConversationActivity.this, message);
        Log.w(TAG, key + ": " + message);
    }

    private void reLogin() {
        showToast(getString(R.string.please_log_in_again));
        preferenceManager.clear();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        conversationPresenter.onCancelAPI();
    }
}