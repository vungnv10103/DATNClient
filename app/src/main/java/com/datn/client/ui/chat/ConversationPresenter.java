package com.datn.client.ui.chat;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.datn.client.models.MessageResponse;
import com.datn.client.models.OrdersDetail;
import com.datn.client.response.ConversationResponse;
import com.datn.client.response.Demo;
import com.datn.client.services.ApiService;
import com.datn.client.ui.BasePresenter;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationPresenter extends BasePresenter {
    private final FragmentActivity context;

    private final IChatView iChatView;
    private final ApiService apiService;
    private final String token;
    private final String customerID;

    private Call<ConversationResponse> getDataConversation;

    public ConversationPresenter(FragmentActivity context, IChatView iChatView, ApiService apiService, String token, String customerID) {
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
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                List<Demo> dataConversation = response.body().getConversations();
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
                            iChatView.onThrowLog("getDataConversation: onResponse", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ConversationResponse> call, @NonNull Throwable t) {
                        iChatView.onThrowLog("getDataConversation: onFailure", t.getMessage());
                    }
                });
            } catch (Exception e) {
                iChatView.onThrowLog("getDataConversation", e.getMessage());
            }
        });
    }
}
