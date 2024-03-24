package com.datn.client.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.datn.client.MainActivity;
import com.datn.client.R;
import com.datn.client.databinding.ActivityWelcomeBinding;
import com.datn.client.models.MessageResponse;
import com.datn.client.response._BaseResponse;
import com.datn.client.services.ApiService;
import com.datn.client.services.RetrofitConnection;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PreferenceManager;

import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private ActivityWelcomeBinding binding;
    private ApiService apiService;
    private PreferenceManager preferenceManager;

    private ToggleButton toggleLanguage;

    private Chronometer chronometer;

    private static final int LIMIT__SECOND = 10;
    private long time;
    private boolean isServerStarted = false;

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


        initService();
        initEventClick();
        pingServer();


//        startCountDown(2000, 1000);
//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(Constants.isNightMode ? getColor(R.color.big_stone) : getColor(R.color.tutu));
    }

    private void pingServer() {
        try {
            binding.tvMsg.setText("");
            binding.tvMsg.setVisibility(View.INVISIBLE);
            Call<_BaseResponse> pingServer = apiService.pingServer();
            pingServer.enqueue(new Callback<_BaseResponse>() {
                @Override
                public void onResponse(@NonNull Call<_BaseResponse> call, @NonNull Response<_BaseResponse> response) {
                    runOnUiThread(() -> {
                        if (response.body() != null) {
                            isServerStarted = true;
                            int statusCode = response.body().getStatusCode();
                            MessageResponse message = response.body().getMessage();
                            if (statusCode == 200) {
                                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                                finishAffinity();
                            } else if (statusCode == 400) {
                                binding.tvMsg.setText(message.getContent());
                                binding.tvMsg.setVisibility(View.VISIBLE);
//                                MyDialog.gI().startDlgOK(WelcomeActivity.this, getString(R.string.error) + statusCode, String.valueOf(time));
                            }
                        } else {
                            isServerStarted = false;
                            binding.tvMsg.setText(response.toString());
                            binding.tvMsg.setVisibility(View.VISIBLE);
//                            MyDialog.gI().startDlgOK(WelcomeActivity.this, getString(R.string.error) + response.code(), response.message());
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<_BaseResponse> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        binding.tvMsg.setText(t.getMessage());
                        binding.tvMsg.setVisibility(View.VISIBLE);
//                        MyDialog.gI().startDlgOK(WelcomeActivity.this, "onFailure", String.valueOf(time));
                    });
                }
            });
        } catch (Exception e) {
            binding.tvMsg.setText(e.getMessage());
            binding.tvMsg.setVisibility(View.VISIBLE);
//            MyDialog.gI().startDlgOK(WelcomeActivity.this, "Exception", String.valueOf(time));
        }
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

    private void initEventClick() {
        toggleLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean isEnLanguage = buttonView.isChecked();
            if (isEnLanguage) {
                binding.tvLanguage.setText(getString(R.string.language_en));
            } else {
                binding.tvLanguage.setText(getString(R.string.language_vi));
            }
        });

        chronometer.start();
        chronometer.setOnChronometerTickListener(chronometer -> {
            String rawText = chronometer.getText().toString().trim();
            int minute = Integer.parseInt(rawText.split(":")[0]);
            int second = Integer.parseInt(rawText.split(":")[1]);
            time++;
            if (minute == 0 & second == LIMIT__SECOND) {
                chronometer.stop();
            }
        });

        binding.imgApp.setOnLongClickListener(v -> {
            pingServer();
            return true;
        });
    }

    private void initService() {
        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(this, Constants.KEY_PREFERENCE_ACC);
    }

    private void initUI() {
        toggleLanguage = binding.toggleLanguage;
        chronometer = binding.chronometer;
    }
}