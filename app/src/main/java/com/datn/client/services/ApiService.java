package com.datn.client.services;

import com.datn.client.models.Cart;
import com.datn.client.models.CartBuyNow;
import com.datn.client.models.Customer;
import com.datn.client.response.BannerResponse;
import com.datn.client.response.CategoryResponse;
import com.datn.client.response.ConversationResponse;
import com.datn.client.response.CreateOrderResponse;
import com.datn.client.response.CustomerResponse;
import com.datn.client.response.EBankingResponse;
import com.datn.client.response.MessageResponse;
import com.datn.client.response.NewMessageResponse;
import com.datn.client.response.NotificationResponse;
import com.datn.client.response.OrderResponse;
import com.datn.client.response.OverlayMessageResponse;
import com.datn.client.response.PaymentMethodResponse;
import com.datn.client.response.ProductCartResponse;
import com.datn.client.response.ProductResponse;
import com.datn.client.response._BaseResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @GET("/v1/api/global/ping")
    Call<_BaseResponse> pingServer();

    @POST("/v1/api/customer/register")
    Call<CustomerResponse> registerCustomer(@Body Customer customer);

    @POST("/v1/api/customer/login")
    Call<CustomerResponse> loginCustomer(@Body Customer customer);

    @POST("/v1/api/customer/login/check")
    Call<_BaseResponse> checkLogin(@Header("Authorization") String token, @Body Customer customer);

    @POST("/v1/api/customer/login/verify")
    Call<CustomerResponse> verify(@Body Customer customer);

    @POST("/v1/api/customer/add/fcm")
    Call<_BaseResponse> addFCM(@Header("Authorization") String token, @Body Customer customer);

    @FormUrlEncoded
    @POST("/v1/api/customer/logout")
    Call<_BaseResponse> logout(@Header("Authorization") String token, @Field("customerID") String customerID);


    @FormUrlEncoded
    @POST("/v1/api/banner/get")
    Call<BannerResponse> getBanner(@Header("Authorization") String token, @Field("customerID") String customerID);

    @FormUrlEncoded
    @POST("/v1/api/category/get")
    Call<CategoryResponse> getCategory(@Header("Authorization") String token, @Field("customerID") String customerID);

    @FormUrlEncoded
    @POST("/v1/api/product/get")
    Call<ProductResponse> getSellingProduct(@Header("Authorization") String token, @Field("customerID") String customerID);

    @FormUrlEncoded
    @POST("/v1/api/product/get/category")
    Call<ProductResponse> getProductByCateID(@Header("Authorization") String token,
                                             @Field("customerID") String customerID,
                                             @Field("categoryID") String categoryID);

    @FormUrlEncoded
    @POST("/v1/api/product/detail")
    Call<ProductResponse> getDetailProduct(@Header("Authorization") String token,
                                           @Field("customerID") String customerID,
                                           @Field("productID") String productID);

    @FormUrlEncoded
    @POST("/v1/api/product/search")
    Call<ProductResponse> searchProduct(@Header("Authorization") String token,
                                        @Field("customerID") String customerID,
                                        @Field("keyword") String keyword);

    @FormUrlEncoded
    @POST("/v1/api/cart/get/customer")
    Call<ProductCartResponse> getCart(@Header("Authorization") String token, @Field("customerID") String customerID);

    @POST("/v1/api/cart/add")
    Call<_BaseResponse> addToCart(@Header("Authorization") String token, @Body Cart cart);

    @FormUrlEncoded
    @POST("/v1/api/cart/update/quantity")
    Call<ProductCartResponse> updateQuantity(@Header("Authorization") String token,
                                             @Field("customerID") String customerID,
                                             @Field("type") String type,
                                             @Field("quantity") int quantity,
                                             @Field("cartID") String cartID);

    @FormUrlEncoded
    @POST("/v1/api/cart/update/status")
    Call<ProductCartResponse> updateStatus(@Header("Authorization") String token,
                                           @Field("customerID") String customerID,
                                           @Field("status") int status,
                                           @Field("cartID") String cartID);

    @FormUrlEncoded
    @POST("/v1/api/cart/update/status-all")
    Call<ProductCartResponse> updateStatusAll(@Header("Authorization") String token,
                                              @Field("customerID") String customerID,
                                              @Field("isSelected") boolean isSelected);

    @POST("/v1/api/cart/buynow")
    Call<ProductCartResponse> buyNow(@Header("Authorization") String token, @Body Cart cart);

    @FormUrlEncoded
    @POST("/v1/api/cart/buynow-cart")
    Call<ProductCartResponse> buyNowCart(@Header("Authorization") String token,
                                         @Field("customerID") String customerID,
                                         @Field("cartID") String cartID);


    @FormUrlEncoded
    @POST("/v1/api/checkout/get/product")
    Call<ProductCartResponse> getProductCheckout(@Header("Authorization") String token,
                                                 @Field("customerID") String customerID);

    @FormUrlEncoded
    @POST("/v1/api/checkout/get/payment-method")
    Call<PaymentMethodResponse> getPaymentMethod(@Header("Authorization") String token,
                                                 @Field("customerID") String customerID);

    @FormUrlEncoded
    @POST("/v1/api/order/create/delivery")
    Call<_BaseResponse> createOrderDelivery(@Header("Authorization") String token,
                                            @Field("customerID") String customerID);

    @FormUrlEncoded
    @POST("/v1/api/order/get/amount-zalopay")
    Call<CreateOrderResponse> getAmountZaloPay(@Header("Authorization") String token,
                                               @Field("customerID") String customerID,
                                               @Field("type") int type);

    @POST("/v1/api/order/get/amount-zalopay-now")
    Call<CreateOrderResponse> getAmountZaloPayNow(@Header("Authorization") String token,
                                                  @Body CartBuyNow cartBuyNow);

    @FormUrlEncoded
    @POST("/v1/api/order/create/zalopay")
    Call<_BaseResponse> createOrderZaloPay(@Header("Authorization") String token,
                                           @Field("customerID") String customerID);

    @POST("/v1/api/order/create/zalopay-now")
    Call<_BaseResponse> createOrderZaloPayNow(@Header("Authorization") String token,
                                              @Body CartBuyNow cartBuyNow);


    @FormUrlEncoded
    @POST("/v1/api/order/create_payment_url")
    Call<EBankingResponse> createPaymentURL(@Header("Authorization") String token,
                                            @Field("customerID") String customerID,
                                            @Field("bankCode") String bankCode,
                                            @Field("language") String language);

    @FormUrlEncoded
    @POST("/v1/api/order/create_payment_url_now")
    Call<EBankingResponse> createPaymentURLNow(@Header("Authorization") String token,
                                               @Field("customerID") String customerID,
                                               @Field("productID") String productID,
                                               @Field("quantityProduct") int quantityProduct,
                                               @Field("bankCode") String bankCode,
                                               @Field("language") String language);

    @FormUrlEncoded
    @POST("/v1/api/order/get")
    Call<OrderResponse> getOrdersByStatus(@Header("Authorization") String token,
                                          @Field("customerID") String customerID,
                                          @Field("status") int status);

    @FormUrlEncoded
    @POST("/v1/api/overlay/message/get")
    Call<OverlayMessageResponse> getOverlayMessage(@Header("Authorization") String token,
                                                   @Field("customerID") String customerID);

    @FormUrlEncoded
    @POST("/v1/api/overlay/message/update")
    Call<_BaseResponse> updateStatusOverlayMessage(@Header("Authorization") String token,
                                                   @Field("customerID") String customerID,
                                                   @Field("overlayMessageID") String overlayMessageID);

    @FormUrlEncoded
    @POST("/v1/api/notification/get")
    Call<NotificationResponse> getNotification(@Header("Authorization") String token,
                                               @Field("customerID") String customerID);

    @FormUrlEncoded
    @POST("/v1/api/notification/update")
    Call<NotificationResponse> updateStatusNotification(@Header("Authorization") String token,
                                                        @Field("customerID") String customerID,
                                                        @Field("notificationID") String notificationID,
                                                        @Field("status") int status);

    @FormUrlEncoded
    @POST("/v1/api/conversation/get")
    Call<ConversationResponse> getDataConversation(@Header("Authorization") String token,
                                                   @Field("memberID") String memberID);

    @FormUrlEncoded
    @POST("/v1/api/message/get")
    Call<MessageResponse> getDataMessage(@Header("Authorization") String token,
                                         @Field("conversationID") String conversationID,
                                         @Field("userID") String userID);

    @Multipart
    @POST("/v1/api/message/create")
    Call<NewMessageResponse> createMessage(@Header("Authorization") String token,
                                           @Part("conversationID") RequestBody conversationID,
                                           @Part("senderID") RequestBody senderID,
                                           @Part("message") RequestBody message,
                                           @Part("messageType") RequestBody messageType,
                                           @Part MultipartBody.Part images,
                                           @Part MultipartBody.Part video
    );

}
