package com.datn.client.ui.product;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.datn.client.R;
import com.datn.client.databinding.ActivityDetailProductBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageResponse;
import com.datn.client.models.Notification;
import com.datn.client.models.OverlayMessage;
import com.datn.client.models.Product;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.ui.components.MyOverlayMsgDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.ManagerUser;
import com.datn.client.utils.MyFormat;
import com.datn.client.utils.PreferenceManager;
import com.datn.client.utils.TYPE_BUY;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class DetailProductActivity extends AppCompatActivity implements IProductView {
    private ActivityDetailProductBinding binding;

    private ProductPresenter productPresenter;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private AddToCartBS bottomSheetAddToCart;

    private LinearLayout layoutDetailProduct;
    private CircularProgressIndicator progressLoadingDetail, progressLoadingVideo;
    private PlayerView playerView;
    private ExoPlayer player;
    private GifImageView imgChat;
    private Button btnBuyNow, btnAddToCart;
    private TextView tvName, tvPrice;
    private LinearLayout layoutSeeAllFeedback;

    private Customer mCustomer;
    private String mToken;
    private String mProductID;

    public static Product mProduct;

    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_product), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).hide();
        getOnBackPressedDispatcher().addCallback(this, callback);

        initUI();
        initEventClick();
        preferenceManager = new PreferenceManager(this, Constants.KEY_PREFERENCE_ACC);
        mProductID = getIntent().getStringExtra("productID");
        if (mProductID == null) {
            showToast(getString(R.string.product_selection_error));
            finishAffinity();
        }
        mCustomer = ManagerUser.gI().checkCustomer(this);
        mToken = ManagerUser.gI().checkToken(this);
        if (mCustomer == null || mToken == null) {
            reLogin();
        }
        initService();
    }

    @Override
    protected void onStart() {
        super.onStart();

        productPresenter.getDetailProduct(mProductID);
    }

    private void displayVideo() {
        progressLoadingVideo.setVisibility(View.GONE);
        playerView.setVisibility(View.VISIBLE);
//        player.play();
    }

    private void setPlayer() {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(mProduct.getVideo())
                .setMimeType(MimeTypes.APPLICATION_MP4)
                .build();
        // MediaItem mediaItem = MediaItem.fromUri(mProduct.getVideo());
        player.setMediaItem(mediaItem);
        player.prepare();
        handlePlayer();
        displayVideo();
    }

    @SuppressLint("SetTextI18n")
    private void displayProduct() {
        tvName.setText(mProduct.getName());
        tvPrice.setText("Giá: " + MyFormat.formatCurrency(mProduct.getPrice()));
        setPlayer();
    }


    private void onProductLoaded() {
        displayProduct();
        progressLoadingDetail.setVisibility(View.GONE);
        layoutDetailProduct.setVisibility(View.VISIBLE);
    }


    @Override
    public void onLoadProduct(@NonNull List<Product> productList) {
        mProduct = productList.get(0);
        onProductLoaded();
    }

    @Override
    public void onListNotification(List<Notification> notificationList) {

    }

    @Override
    public void onListOverlayMessage(List<OverlayMessage> overlayMessages) {
        MyOverlayMsgDialog.gI().showOverlayMsgDialog(this, overlayMessages, productPresenter);
    }

    @Override
    public void onThrowMessage(MessageResponse message) {
        if (message != null) {
            MyDialog.gI().startDlgOK(this, message.getContent());
        }
    }

    @Override
    public void onThrowLog(String key, String message) {
        Log.w(key, "onThrowLog: " + message);
    }

    @Override
    public void onFinish() {
        reLogin();
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initService() {
        apiService = RetrofitConnection.getApiService();
        productPresenter = new ProductPresenter(DetailProductActivity.this, this, apiService, mToken, mCustomer.get_id());
    }

    private void initEventClick() {
        imgChat.setOnClickListener(v -> {

        });
        btnBuyNow.setOnClickListener(v -> {
            bottomSheetAddToCart = new AddToCartBS(apiService, mToken, mCustomer.get_id(), TYPE_BUY.BUY_NOW.getValue());
            bottomSheetAddToCart.show(getSupportFragmentManager(), AddToCartBS.TAG);
        });
        btnAddToCart.setOnClickListener(v -> {
            bottomSheetAddToCart = new AddToCartBS(apiService, mToken, mCustomer.get_id(), TYPE_BUY.ADD_TO_CART.getValue());
            bottomSheetAddToCart.show(getSupportFragmentManager(), AddToCartBS.TAG);
        });
        layoutSeeAllFeedback.setOnClickListener(v -> {

        });
    }

    private void initUI() {
        layoutDetailProduct = binding.layoutDetailProduct;
        progressLoadingDetail = binding.progressbarLoading;
        progressLoadingVideo = binding.progressbarLoadingVideo;
        playerView = binding.playerView;
        tvName = binding.tvName;
        tvPrice = binding.tvPrice;
        imgChat = binding.imgChat;
        btnBuyNow = binding.btnBuyNow;
        btnAddToCart = binding.btnAddToCart;
        layoutSeeAllFeedback = binding.layoutViewAllFeedback;
    }

    private void handlePlayer() {
        player.addListener(new Player.Listener() {
            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                Player.Listener.super.onIsLoadingChanged(isLoading);
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                @Nullable Throwable cause = error.getCause();
                if (cause instanceof HttpDataSource.HttpDataSourceException) {
                    // An HTTP error occurred.
                    HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                    // It's possible to find out more about the error both by casting and by querying
                    // the cause.
                    if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                        // Cast to InvalidResponseCodeException and retrieve the response code, message
                        // and headers.
                        MyDialog.gI().startDlgOK(DetailProductActivity.this, httpError.getMessage());
                    } else {
                        MyDialog.gI().startDlgOK(DetailProductActivity.this, httpError.getMessage());
                    }
                }
            }
        });
    }

    private void reLogin() {
        showToast(getString(R.string.please_log_in_again));
        preferenceManager.clear();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
        productPresenter.onCancelAPI();
    }

    public static Product getProduct() {
        return mProduct;
    }
}
