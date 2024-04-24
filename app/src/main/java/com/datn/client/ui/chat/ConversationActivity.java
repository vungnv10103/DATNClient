package com.datn.client.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.adapter.ConversationAdapter;
import com.datn.client.databinding.ActivityConversationBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageDetailResponse;
import com.datn.client.models.MessageModel;
import com.datn.client.models.Notification;
import com.datn.client.models.OverlayMessage;
import com.datn.client.models._BaseModel;
import com.datn.client.response.ConversationDisplay;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.services.SocketManager;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.ui.components.MyOverlayMsgDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.ManagerUser;
import com.datn.client.utils.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ConversationActivity extends AppCompatActivity implements IChatView {
    private static final String TAG = ConversationActivity.class.getSimpleName();
    private ActivityConversationBinding binding;

    private ChatPresenter chatPresenter;
    private PreferenceManager preferenceManager;


    private Customer mCustomer;
    private String mToken;

    private List<ConversationDisplay> mConversation;
    private ConversationAdapter mConversationAdapter;
    private Socket mSocket;


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
        initSocket();

    }

    @Override
    protected void onStart() {
        super.onStart();

        setLoading(true);
        chatPresenter.getDataConversation();
    }

    private void setLoading(boolean isLoading) {
        binding.progressbarConversation.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.rcvConversation.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
    }

    private void displayConversation() {
        mConversation.sort(Comparator.comparing(ConversationDisplay::getCreated_at).reversed());
        if (mConversationAdapter == null) {
            mConversationAdapter = new ConversationAdapter(ConversationActivity.this, mConversation, new IAction() {
                @Override
                public void onClick(_BaseModel conversation) {
                    Intent intent = new Intent(ConversationActivity.this, ChatActivity.class);
                    intent.putExtra("conversationID", conversation.get_id());
                    startActivity(intent);
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
            binding.rcvConversation.setLayoutManager(linearLayoutManager);
            binding.rcvConversation.setAdapter(mConversationAdapter);
        } else {
            mConversationAdapter.updateList(mConversation);
        }
        setLoading(false);

    }

    @Override
    public void onListConversation(List<ConversationDisplay> dataConversation) {
        if (dataConversation != null) {
            this.mConversation = dataConversation;
            displayConversation();
        } else {
            showToast("no data");
        }
    }

    @Override
    public void onDataMessage(List<MessageModel> dataMessage) {
        System.out.println(dataMessage);
    }

    @Override
    public void onNewMessage(MessageModel newMessage) {
        // TODO notify new message
        System.out.println(newMessage);
    }

    @Override
    public void onListNotification(List<Notification> notificationList) {
        Log.d(TAG, "onListNotification: " + notificationList);
    }

    @Override
    public void onListOverlayMessage(List<OverlayMessage> overlayMessages) {
        MyOverlayMsgDialog.gI().showOverlayMsgDialog(ConversationActivity.this, overlayMessages, chatPresenter);
    }

    @Override
    public void onThrowMessage(MessageDetailResponse message) {
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
    public void onThrowNotification(String notification) {
        MyDialog.gI().startDlgOK(this, notification);
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

    }

    private int getPositionConversation(String conversationID) {
        for (int i = 0; i < mConversation.size(); i++) {
            if (mConversation.get(i).getConversation_id().equals(conversationID)) {
                return i;
            }
        }
        return -1;
    }

    private final Emitter.Listener onConnect = args -> runOnUiThread(() -> Log.d(TAG, "run: " + R.string.app_name));


    private final Emitter.Listener onUserChat = args -> runOnUiThread(() -> {
        // new message
        JSONObject data = (JSONObject) args[0];
        try {
            String conversationID = data.getString("conversation_id");
//            String senderID = data.getString("sender_id");
//            int messageType = data.getInt("message_type");
//            String message = data.getString("message");
//            String created_at = data.getString("created_at");
            int position = getPositionConversation(conversationID);
            System.out.println("position change: " + position);
            chatPresenter.getDataConversation();
        } catch (JSONException e) {
            MyDialog.gI().startDlgOK(this, e);
        }
    });

    private void initSocket() {
        mSocket = SocketManager.getInstance(this).getSocket();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on("user-chat", onUserChat);
        SocketManager.getInstance(this).connect();
    }

    private void initService() {
        ApiService apiService = RetrofitConnection.getApiService();
        chatPresenter = new ChatPresenter(ConversationActivity.this, this, apiService, mToken, mCustomer.get_id());
    }

    private void checkRequire() {
        preferenceManager = new PreferenceManager(ConversationActivity.this, Constants.KEY_PREFERENCE_ACC);
        mCustomer = ManagerUser.gI().getCustomerLogin(this);
        mToken = ManagerUser.gI().checkToken(this);
        if (mCustomer == null || mToken == null) {
            reLogin();
        }
    }


    private void showToast(@NonNull Object message) {
        Toast.makeText(ConversationActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
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
        chatPresenter.onCancelAPI();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off("user-chat", onUserChat);
        SocketManager.getInstance(this).disconnect();
        SocketManager.getInstance(this).close();
    }
}