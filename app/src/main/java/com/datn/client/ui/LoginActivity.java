package com.datn.client.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.datn.client.MainActivity;
import com.datn.client.R;
import com.datn.client.databinding.ActivityLoginBinding;
import com.datn.client.databinding.FragmentHomeBinding;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        Objects.requireNonNull(getSupportActionBar()).hide();

        final Button register = binding.btnRegister;
        final Button main = binding.btnMain;
        register.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        main.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, MainActivity.class)));

    }
}