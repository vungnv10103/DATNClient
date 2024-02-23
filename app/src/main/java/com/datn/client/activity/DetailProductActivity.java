package com.datn.client.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import com.datn.client.R;
import com.datn.client.databinding.ActivityDetailProductBinding;
import com.datn.client.ui.LoginActivity;

import java.util.Objects;

public class DetailProductActivity extends AppCompatActivity {
    private static final String TAG = DetailProductActivity.class.getSimpleName();
    private ActivityDetailProductBinding binding;

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

    }

    private void initUI() {
    }
}