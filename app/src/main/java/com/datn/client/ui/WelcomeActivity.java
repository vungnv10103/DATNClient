package com.datn.client.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.datn.client.R;
import com.datn.client.databinding.ActivityWelcomeBinding;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.utils.Constants;

import java.util.Locale;
import java.util.Objects;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private ActivityWelcomeBinding binding;

    private ToggleButton toggleLanguage;

    private Chronometer chronometer;

    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            new AlertDialog.Builder(WelcomeActivity.this)
                    .setTitle(TAG.split("Activity")[0])
                    .setMessage(getString(R.string.are_you_sure_exit))
//                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setIcon(R.drawable.logo_app_gradient)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> finish())
                    .setNegativeButton(android.R.string.no, null).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.welcome), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).hide();
        getOnBackPressedDispatcher().addCallback(this, callback);

        Constants.language = getLocate();
        Constants.isNightMode = isNightMode();
        initUI();
        binding.imgApp.setImageResource(Constants.isNightMode ? R.drawable.logo_app_white_no_bg : R.drawable.logo_app_gradient);
    }

    @Override
    protected void onStart() {
        super.onStart();


        toggleLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean isEnLanguage = buttonView.isChecked();
                if (isEnLanguage) {
                    binding.tvLanguage.setText(getString(R.string.language_en));
                } else {
                    binding.tvLanguage.setText(getString(R.string.language_vi));
                }
            }
        });

//        chronometer.start();
        chronometer.setOnChronometerTickListener(chronometer -> {
            String rawText = chronometer.getText().toString().trim();
            int minute = Integer.parseInt(rawText.split(":")[0]);
            int second = Integer.parseInt(rawText.split(":")[1]);
            System.out.println(minute);
            System.out.println(second);
            if (minute == 0 & second == 10) {
                chronometer.stop();
            }
        });

        binding.imgApp.setOnLongClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            finishAffinity();
            return true;
        });
        startCountDown(2000, 1000);
//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(Constants.isNightMode ? getColor(R.color.big_stone) : getColor(R.color.tutu));
    }

    @NonNull
    private String getLocate() {
        return Locale.getDefault().getLanguage();
    }

    private boolean isNightMode() {
        return getResources().getBoolean(R.bool.isNight);
    }

    private void startCountDown(long count, long step) {
        new CountDownTimer(count, step) {

            public void onTick(long millisUntilFinished) {
                System.out.println(millisUntilFinished / 1000);
            }

            public void onFinish() {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                finishAffinity();
            }

        }.start();
    }

    private void initUI() {
        toggleLanguage = binding.toggleLanguage;
        chronometer = binding.chronometer;
    }
}