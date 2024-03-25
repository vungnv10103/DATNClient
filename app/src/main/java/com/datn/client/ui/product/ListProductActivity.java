package com.datn.client.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.client.R;
import com.datn.client.adapter.ProductAdapter;
import com.datn.client.databinding.ActivityListProductBinding;
import com.datn.client.models.Customer;
import com.datn.client.models.MessageResponse;
import com.datn.client.models.Notification;
import com.datn.client.models.OverlayMessage;
import com.datn.client.models.Product;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.ui.components.MyOverlayMsgDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

public class ListProductActivity extends AppCompatActivity implements IProductView {
    private ActivityListProductBinding binding;
    private ProductPresenter productPresenter;
    private PreferenceManager preferenceManager;

    private Customer mCustomer;
    private String mToken;

    private RecyclerView rcvListProduct;

    private String mCategoryID;
    private List<Product> mProductList;

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
        binding = ActivityListProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.list_product), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).hide();
        getOnBackPressedDispatcher().addCallback(this, callback);

        initUI();

        preferenceManager = new PreferenceManager(this, Constants.KEY_PREFERENCE_ACC);
        checkLogin();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initService();
        productPresenter.getProductByCateID(mCategoryID);
    }

    private void initService() {
        ApiService apiService = RetrofitConnection.getApiService();
        productPresenter = new ProductPresenter(ListProductActivity.this, this, apiService, mToken, mCustomer.get_id());
    }

    private Customer getLogin() {
        Gson gson = new Gson();
        String json = preferenceManager.getString("user");
        return gson.fromJson(json, Customer.class);
    }

    private void checkLogin() {
        mCustomer = getLogin();
        if (mCustomer == null) {
            switchToLogin();
            return;
        }
        mToken = preferenceManager.getString("token");
        if (mToken == null || mToken.isEmpty()) {
            switchToLogin();
            return;
        }
        mCategoryID = getIntent().getStringExtra("categoryID");
        if (mCategoryID == null) {
            showToast(getString(R.string.category_selection_error));
            finishAffinity();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initUI() {
        rcvListProduct = binding.rcvListProduct;
    }

    @Override
    public void onLoadProduct(List<Product> productList) {
        this.mProductList = productList;
        displayProduct();
    }

    private void displayProduct() {
        if (mProductList.isEmpty()) {
            showToast("No product");
            return;
        }
        runOnUiThread(() -> {
            ProductAdapter productAdapter = new ProductAdapter(this, mProductList,
                    R.layout.item_product, product -> {
                Intent intent = new Intent(this, DetailProductActivity.class);
                intent.putExtra("productID", product.get_id());
                startActivity(intent);
            });
            rcvListProduct.setLayoutManager(new GridLayoutManager(this, 2));
            rcvListProduct.setAdapter(productAdapter);
        });
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
        MyDialog.gI().startDlgOK(this, message.getContent());
    }

    @Override
    public void onThrowLog(String key, String message) {
        Log.w(key, "onThrowLog: " + message);
    }

    @Override
    public void onFinish() {
        MyDialog.gI().startDlgOK(this, "onFinish");
    }

    private void switchToLogin() {
        showToast(getString(R.string.please_log_in_again));
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        productPresenter.cancelAPI();
    }


}