package com.datn.client.ui.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.adapter.ConversationAdapter;
import com.datn.client.adapter.MessageAdapter;
import com.datn.client.databinding.ActivityChatBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageDetailResponse;
import com.datn.client.models.MessageModel;
import com.datn.client.models.Notification;
import com.datn.client.models.OverlayMessage;
import com.datn.client.models.UserModel;
import com.datn.client.models._BaseModel;
import com.datn.client.response.Demo;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.ui.components.MyOverlayMsgDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.ManagerUser;
import com.datn.client.utils.MyFormat;
import com.datn.client.utils.MyPermission;
import com.datn.client.utils.PreferenceManager;
import com.datn.client.utils.TYPE_MESSAGE;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ChatActivity extends AppCompatActivity implements IChatView {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private ActivityChatBinding binding;

    private ChatPresenter chatPresenter;
    private PreferenceManager preferenceManager;
    private Customer mCustomer;
    private String mToken;
    private String mConversationID;
    private UserModel mUserFocus = null;

    private List<MessageModel> mDataMessages;
    private MessageAdapter messageAdapter;
    private Socket mSocket;
    private boolean isShowKeyBoard = false;
    private boolean isShowEmoji = false;
    private boolean isSendImage = false;
    private boolean isSendVideo = false;
    private Uri imgUri = null;
    private Uri videoUri = null;

    private int messageType = TYPE_MESSAGE.TEXT.getValue();

    @SuppressLint({"ResourceAsColor", "PrivateResource"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        View decor = getWindow().getDecorView();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorSurfaceContainer, typedValue, true);
        int color = ContextCompat.getColor(this, typedValue.resourceId);
        window.setStatusBarColor(color);
        boolean isNightMode = Constants.isNightMode;
        if (isNightMode) {
            decor.setSystemUiVisibility(0);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }


        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chat), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).hide();

        mConversationID = getIntent().getStringExtra("conversationID");
        if (mConversationID == null) {
            showToast(getString(R.string.an_error_occurred));
            finish();
            return;
        }
        mUserFocus = ConversationAdapter.dataUsersFocus.get(mConversationID);

        initUI();
        checkRequire();
        initService();
        initSocket();
    }


    @Override
    protected void onStart() {
        super.onStart();

        setLoading(true);
        if (mUserFocus == null) {
            return;
        }
        setInfoUser();
        chatPresenter.getDataMessage(mConversationID);
        initEventClick();
    }


    private void setInfoUser() {
        Glide.with(this)
                .load(mUserFocus.getAvatar())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.imgAvt);
        binding.tvName.setText(mUserFocus.getFull_name());
    }

    private void setLoading(boolean isLoading) {
        binding.progressbarChat.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.rcvMessage.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
    }

    private void displayMessage() {
//        mConversation.sort(Comparator.comparing(Demo::getCreated_at).reversed());
        messageAdapter = new MessageAdapter(ChatActivity.this, mDataMessages, mUserFocus.getAvatar(),
                new IAction() {
                    @Override
                    public void onClick(_BaseModel conversation) {

                    }

                    @Override
                    public void onLongClick(_BaseModel conversation) {
                        MyDialog.gI().startDlgOK(ChatActivity.this, conversation.get_id());
                    }

                    @Override
                    public void onItemClick(_BaseModel conversation) {
                        MyDialog.gI().startDlgOK(ChatActivity.this, conversation.get_id());
                    }
                });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        binding.rcvMessage.setLayoutManager(linearLayoutManager);
        binding.rcvMessage.setAdapter(messageAdapter);
        binding.rcvMessage.smoothScrollToPosition(mDataMessages.size() - 1);
        setLoading(false);
    }

    @Override
    public void onDataMessage(List<MessageModel> dataMessage) {
        if (dataMessage != null) {
            this.mDataMessages = dataMessage;
            displayMessage();
        } else {
            showToast("no data");
        }
    }

    @Override
    public void onNewMessage(MessageModel newMessage) {
        System.out.println(newMessage);
        mDataMessages.add(newMessage);
        binding.rcvMessage.smoothScrollToPosition(mDataMessages.size() - 1);
        binding.textInputEditMsg.setText("");
        messageAdapter.notifyItemInserted(mDataMessages.size() - 1);
    }

    @Override
    public void onListConversation(List<Demo> dataConversation) {
        System.out.println(dataConversation);
    }

    @Override
    public void onListNotification(List<Notification> notificationList) {
        Log.d(TAG, "onListNotification: " + notificationList);
    }

    @Override
    public void onListOverlayMessage(List<OverlayMessage> overlayMessages) {
        MyOverlayMsgDialog.gI().showOverlayMsgDialog(ChatActivity.this, overlayMessages, chatPresenter);

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
                    MyDialog.gI().startDlgOK(ChatActivity.this, message.getContent());
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
        binding.toolbarChat.inflateMenu(R.menu.chat_action_menu);
        binding.chat.getViewTreeObserver().
                addOnGlobalLayoutListener(() -> {
                    int heightDiff = binding.chat.getRootView().getHeight() - binding.chat.getHeight();
                    if (heightDiff > MyFormat.dpToPx(ChatActivity.this, 200)) {
                        binding.rcvMessage.smoothScrollToPosition(mDataMessages.size() - 1);
                        isShowKeyBoard = true;
                        isShowEmoji = false;
                        binding.emojiPicker.setVisibility(View.GONE);
                    } else {
                        isShowKeyBoard = false;
                        if (isShowEmoji) {
                            binding.emojiPicker.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }

    private void doToggleEmoji() {
        isShowEmoji = !isShowEmoji;
        binding.textInputEditMsg.requestFocus();
        if (isShowEmoji) {
            if (isShowKeyBoard) {
                isShowKeyBoard = false;
                hiddenKeyboard();
            }
            binding.emojiPicker.setVisibility(View.VISIBLE);
            setAnimationOptionMsg(true);
//            binding.textInputLayoutMsg.setEndIconDrawable(com.google.android.material.R.drawable.mtrl_ic_cancel);
//            binding.textInputLayoutMsg.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
        } else {
            binding.emojiPicker.setVisibility(View.GONE);
//            binding.textInputLayoutMsg.setEndIconMode(R.drawable.ic_emoji_24);
//            binding.textInputLayoutMsg.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEventClick() {
        binding.toolbarChat.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        binding.toolbarChat.setOnMenuItemClickListener(item -> {
            int itemID = item.getItemId();
            if (itemID == R.id.item_call_default) {
                featureUpdating("Call");
                return true;
            } else if (itemID == R.id.item_call_video) {
                featureUpdating("Video call");
                return true;
            } else if (itemID == R.id.item_more_info) {
                featureUpdating("More info");
                return true;
            }
            return false;
        });
        binding.textInputLayoutMsg.setEndIconOnClickListener(v -> doToggleEmoji());
        binding.btnEnableOption.setOnClickListener(v -> setAnimationOptionMsg(false));
        binding.textInputEditMsg.setOnTouchListener((v, event) -> {
            setAnimationOptionMsg(true);
            return false;
        });
        binding.btnSend.setOnClickListener(v -> doSendMsg());
        binding.textInputEditMsg.addTextChangedListener(textWatcherInputMsg);
        binding.emojiPicker.setOnEmojiPickedListener(emojiViewItem -> {
            String newMessage = Objects.requireNonNull(binding.textInputEditMsg.getText()).toString().trim() + emojiViewItem.getEmoji();
            binding.textInputEditMsg.setText(newMessage);
            binding.textInputEditMsg.setSelection(binding.textInputEditMsg.length());
        });

//        binding.btnOption.setOnClickListener(v -> featureUpdating("Option"));
        binding.btnOption.setOnClickListener(v -> doOpenVideo());
        binding.btnCamera.setOnClickListener(v -> requestPermission());
        binding.btnGallery.setOnClickListener(v -> doOpenGallery());
    }

    private void featureUpdating(Object featureName) {
        MyDialog.gI().startDlgOK(this, featureName + " is updating...");
    }

    private void doOpenCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        resultLauncher.launch(intent);
    }

    private void doOpenVideo() {
        isSendVideo = true;
        isSendImage = false;
        resultLauncherContent.launch("video/*");
    }

    private void doOpenGallery() {
        isSendVideo = false;
        isSendImage = true;
        resultLauncherContent.launch("image/*");
    }


    private void doSendMsg() {
        String message = Objects.requireNonNull(binding.textInputEditMsg.getText()).toString().trim();
        if (message.isEmpty()) {
            return;
        }
        sendMessage(message);
    }

    private void sendMessage(String message) {
        if (imgUri != null) {
            File file = new File(Objects.requireNonNull(imgUri.getPath()));
            chatPresenter.createMessage(mConversationID, message, messageType, file, null);
        } else if (videoUri != null) {
            File file = new File(Objects.requireNonNull(videoUri.getPath()));
            chatPresenter.createMessage(mConversationID, message, messageType, null, file);
        } else {
            chatPresenter.createMessage(mConversationID, message, messageType, null, null);
        }
    }

    private void hiddenKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void setAnimationOptionMsg(boolean isTypeMessage) {
        binding.btnOption.setVisibility(isTypeMessage ? View.GONE : View.VISIBLE);
        binding.btnEnableOption.setVisibility(isTypeMessage ? View.VISIBLE : View.GONE);
        binding.btnCamera.setVisibility(isTypeMessage ? View.GONE : View.VISIBLE);
        binding.btnGallery.setVisibility(isTypeMessage ? View.GONE : View.VISIBLE);
    }

    private void initSocket() {
        try {
            mSocket = IO.socket(Constants.URL_API);
        } catch (URISyntaxException e) {
            Log.w(TAG, "initSocket: " + e.getMessage());
            return;
        }
//        mSocket.on(Socket.EVENT_CONNECT, onConnect);
//        mSocket.on("user-chat", onUserChat);

        mSocket.connect();
    }

    private void initService() {
        ApiService apiService = RetrofitConnection.getApiService();
        chatPresenter = new ChatPresenter(ChatActivity.this, this, apiService, mToken, mCustomer.get_id());
    }

    private void checkRequire() {
        preferenceManager = new PreferenceManager(ChatActivity.this, Constants.KEY_PREFERENCE_ACC);
        mCustomer = ManagerUser.gI().getCustomerLogin(this);
        mToken = ManagerUser.gI().checkToken(this);
        if (mCustomer == null || mToken == null) {
            reLogin();
        }
    }


    private void showToast(@NonNull Object message) {
        Toast.makeText(ChatActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
    }

    private void showLogW(String key, Object message) {
        MyDialog.gI().startDlgOK(ChatActivity.this, message);
        Log.w(TAG, key + ": " + message);
    }

    private void reLogin() {
        showToast(getString(R.string.please_log_in_again));
        preferenceManager.clear();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

    TextWatcher textWatcherInputMsg = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            setAnimationOptionMsg(true);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    ActivityResultLauncher<String> resultLauncherContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                // Handle the returned Uri
                if (isSendImage) {
                    this.imgUri = uri;
                } else if (isSendVideo) {
                    this.videoUri = uri;
                }
            });
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    if (bundle != null) {
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        if (bitmap != null) {
                            messageType = TYPE_MESSAGE.IMAGE.getValue();
                            MyDialog.gI().startDlgOK(ChatActivity.this, bitmap.toString());
                        }
                    }

                }
            }
    );

    private void requestPermission() {
        runOnUiThread(() -> {
            if (!MyPermission.gI().checkSelfPermission(ChatActivity.this)) {
                MyPermission.gI().requestPermission(ChatActivity.this);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    doOpenCamera();
                }
            }
        });
    }

    private void showPermissionDeniedDialog() {
        MyDialog.gI().startDlgOKWithAction(ChatActivity.this, null,
                getString(R.string.ask_access_denied), getString(R.string.go_to_setting),
                (dialog, which) -> openAppSettings());
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        ActivityCompat.startActivityForResult(this, intent, MyPermission.REQUEST_CODE_APP_SETTINGS, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MyPermission.REQUEST_CODE_OPEN_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    doOpenCamera();
                }
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    showPermissionDeniedDialog();
                } else {
                    MyDialog.gI().startDlgOK(ChatActivity.this, "CAMERA PERMISSION DENIED");
                }
            }
        } else if (requestCode == MyPermission.REQUEST_CODE_APP_SETTINGS) {
            requestPermission();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        binding = null;
        chatPresenter.onCancelAPI();
//        mSocket.off(Socket.EVENT_CONNECT, onConnect);
//        mSocket.off("user-chat", onUserChat);
        mSocket.disconnect();
        mSocket.close();
    }
}