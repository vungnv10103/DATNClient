package com.datn.client.ui.chat;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.datn.client.BasePresenter;
import com.datn.client.models.MessageDetailResponse;
import com.datn.client.models.MessageModel;
import com.datn.client.response.ConversationResponse;
import com.datn.client.response.ConversationDisplay;
import com.datn.client.response.MessageResponse;
import com.datn.client.response.NewMessageResponse;
import com.datn.client.services.ApiService;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatPresenter extends BasePresenter {
    private final FragmentActivity context;

    private final IChatView iChatView;
    private final ApiService apiService;
    private final String token;
    private final String customerID;

    private Call<ConversationResponse> getDataConversation;
    private Call<MessageResponse> getDataMessage;
    private Call<NewMessageResponse> createMessage;

    public ChatPresenter(FragmentActivity context, IChatView iChatView, ApiService apiService, String token, String customerID) {
        super(context, iChatView, apiService, token, customerID);
        this.context = context;
        this.iChatView = iChatView;
        this.apiService = apiService;
        this.token = token;
        this.customerID = customerID;
    }

    @Override
    public void onCancelAPI() {
        super.onCancelAPI();

        if (getDataConversation != null) {
            getDataConversation.cancel();
        }
        if (getDataMessage != null) {
            getDataMessage.cancel();
        }
        if (createMessage != null) {
            createMessage.cancel();
        }
    }

    public void getDataConversation() {
        context.runOnUiThread(() -> {
            try {
                getDataConversation = apiService.getDataConversation(token, customerID);
                getDataConversation.enqueue(new Callback<ConversationResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ConversationResponse> call, @NonNull Response<ConversationResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageDetailResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                List<ConversationDisplay> dataConversation = response.body().getConversations();
                                if (dataConversation != null) {
                                    iChatView.onListConversation(dataConversation);
                                }
                            } else if (statusCode == 400) {
                                if (code.equals("auth/wrong-token")) {
                                    iChatView.onFinish();
                                } else {
                                    iChatView.onThrowLog("getDataConversation400", code);
                                    iChatView.onThrowMessage(message);
                                }
                            }
                        } else {
                            iChatView.onThrowNotification("getDataConversation: onResponse: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ConversationResponse> call, @NonNull Throwable t) {
                        iChatView.onThrowNotification("getDataConversation: onFailure: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                iChatView.onThrowNotification("getDataConversation: " + e.getMessage());
            }
        });
    }

    public void getDataMessage(String conversationID) {
        context.runOnUiThread(() -> {
            try {
                getDataMessage = apiService.getDataMessage(token, conversationID, customerID);
                getDataMessage.enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageDetailResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                List<MessageModel> dataMessage = response.body().getMessages();
                                if (dataMessage != null) {
                                    iChatView.onDataMessage(dataMessage);
                                }
                            } else if (statusCode == 400) {
                                if (code.equals("auth/wrong-token")) {
                                    iChatView.onFinish();
                                } else {
                                    iChatView.onThrowLog("getDataMessage" + statusCode, code);
                                    iChatView.onThrowMessage(message);
                                }
                            }
                        } else {
                            iChatView.onThrowNotification("getDataMessage: onResponse: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                        iChatView.onThrowNotification("getDataMessage: onFailure: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                iChatView.onThrowNotification("getDataMessage: " + e.getMessage());
            }
        });
    }

    public void createMessage(String conversationID, String message, int messageType, File fileImg, File fileVideo) {
        context.runOnUiThread(() -> {
            try {
                RequestBody rbConversationID = RequestBody.create(conversationID, MediaType.parse("text/plain"));
                RequestBody rbSenderID = RequestBody.create(customerID, MediaType.parse("text/plain"));
                RequestBody rbMessage = RequestBody.create(message, MediaType.parse("text/plain"));
                RequestBody rbMessageType = RequestBody.create(String.valueOf(messageType), MediaType.parse("text/plain"));
                if (fileImg != null) {
                    RequestBody imageRequestBody = RequestBody.create(fileImg, MediaType.parse("image/*"));
                    MultipartBody.Part imagePart = MultipartBody.Part.createFormData("images", fileImg.getName(), imageRequestBody);
                    createMessage = apiService.createMessage(token, rbConversationID, rbSenderID, rbMessage, rbMessageType, imagePart, null);
                } else if (fileVideo != null) {
                    RequestBody videoRequestBody = RequestBody.create(fileVideo, MediaType.parse("video/*"));
                    MultipartBody.Part videoPart = MultipartBody.Part.createFormData("video", fileVideo.getName(), videoRequestBody);
                    createMessage = apiService.createMessage(token, rbConversationID, rbSenderID, rbMessage, rbMessageType, null, videoPart);
                } else {
                    createMessage = apiService.createMessage(token, rbConversationID, rbSenderID, rbMessage, rbMessageType, null, null);
                }
                createMessage.enqueue(new Callback<NewMessageResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<NewMessageResponse> call, @NonNull Response<NewMessageResponse> response) {
                        if (response.body() != null) {
                            int statusCode = response.body().getStatusCode();
                            String code = response.body().getCode();
                            MessageDetailResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                MessageModel newMessage = response.body().getNewMessage();
                                if (newMessage != null) {
                                    iChatView.onNewMessage(newMessage);
                                }
                            } else if (statusCode == 400) {
                                if (code.equals("auth/wrong-token")) {
                                    iChatView.onFinish();
                                } else {
                                    iChatView.onThrowLog("createMessage" + statusCode, code);
                                    iChatView.onThrowMessage(message);
                                }
                            }
                        } else {
                            iChatView.onThrowNotification("createMessage: onResponse: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<NewMessageResponse> call, @NonNull Throwable t) {
                        iChatView.onThrowNotification("createMessage: onFailure: " + t.getMessage());
                        iChatView.onThrowLog("createMessage: onFailure", t.getMessage());
                    }
                });
            } catch (Exception e) {
                iChatView.onThrowNotification("createMessage: " + e.getMessage());
            }
        });
    }
}
