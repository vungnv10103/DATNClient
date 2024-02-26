package com.datn.client.ui.product;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.datn.client.databinding.ActivityDetailProductBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.Product;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.MyDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.Currency;
import com.datn.client.utils.PreferenceManager;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class DetailProductActivity extends AppCompatActivity implements IProductView {
    private static final String TAG = DetailProductActivity.class.getSimpleName();
    private ActivityDetailProductBinding binding;

    private ProductPresenter productPresenter;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    public static AddToCartModalBS modalAddToCart;

    private LinearLayout layoutDetailProduct;
    private SpinKitView spinKitDetail, spinKitVideo;
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
        binding = ActivityDetailProductBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getOnBackPressedDispatcher().addCallback(this, callback);

        initUI();
        initEventClick();
        preferenceManager = new PreferenceManager(this, Constants.KEY_PREFERENCE_ACC);
        checkLogin();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initService();
        productPresenter.getDetailProduct(mProductID);
    }

    private void displayVideo() {
        spinKitVideo.setVisibility(View.GONE);
        playerView.setVisibility(View.VISIBLE);
        player.play();
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

    private void displayProduct() {
        tvName.setText(mProduct.getName());
        tvPrice.setText(Currency.formatCurrency(mProduct.getPrice()));
        setPlayer();
    }


    private void onProductLoaded() {
        displayProduct();
        spinKitDetail.setVisibility(View.GONE);
        layoutDetailProduct.setVisibility(View.VISIBLE);
    }


    @Override
    public void onLoadProduct(@NonNull List<Product> productList) {
        mProduct = productList.get(0);
        onProductLoaded();
    }


    @Override
    public void onThrowMessage(String message) {
        MyDialog.gI().startDlgOK(this, message);
    }

    private Customer getLogin() {
        Gson gson = new Gson();
        String json = preferenceManager.getString("user");
        return gson.fromJson(json, Customer.class);
    }

    private void checkLogin() {
        mCustomer = getLogin();
        if (mCustomer == null) {
            showToast("Có lỗi xảy ra, vui lòng đăng nhập lại.");
            finishAffinity();
            return;
        }
        mToken = preferenceManager.getString("token");
        if (mToken == null || mToken.isEmpty()) {
            showToast("Có lỗi xảy ra, vui lòng đăng nhập lại.");
            finishAffinity();
            return;
        }
        mProductID = getIntent().getStringExtra("productID");
        if (mProductID == null) {
            showToast("Lỗi chọn sản phẩm.");
            finishAffinity();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initService() {
        apiService = RetrofitConnection.getApiService();
        productPresenter = new ProductPresenter(this, apiService, mToken, mCustomer.get_id());
    }

    private void initEventClick() {
        imgChat.setOnClickListener(v -> {
            showToast("message");
        });
        btnBuyNow.setOnClickListener(v -> {
        });
        btnAddToCart.setOnClickListener(v -> {
            modalAddToCart = new AddToCartModalBS(apiService, mToken, mCustomer.get_id());
            modalAddToCart.show(getSupportFragmentManager(), AddToCartModalBS.TAG);
        });
    }

    private void initUI() {
        layoutDetailProduct = binding.layoutDetailProduct;
        spinKitDetail = binding.spinKit;
        spinKitVideo = binding.spinKitVideo;
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

    @Override
    protected void onStop() {
        super.onStop();
        player.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }

    public static Product getProduct() {
        return mProduct;
    }


}