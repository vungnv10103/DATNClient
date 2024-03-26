package com.datn.client.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.datn.client.R;
import com.datn.client.databinding.ActivitySettingBinding;
import com.datn.client.ui.components.MyDialog;

import java.util.Objects;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding binding;
    private SwitchCompat switchDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.setting), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).hide();

        initUI();

    }

    @Override
    protected void onStart() {
        super.onStart();

        initEventClick();
    }

    private void initEventClick() {
        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            }
        });
    }

    private void initUI() {
        switchDarkMode = binding.switchDarkMode;
    }

//    @Override
//    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        // Checks whether a keyboard is available
//        if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
//            MyDialog.gI().startDlgOK(this, "Keyboard available");
//        } else if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_NO) {
//            MyDialog.gI().startDlgOK(this, "No keyboard");
//        }
//
//        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//        switch (currentNightMode) {
//            case Configuration.UI_MODE_NIGHT_NO:
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                MyDialog.gI().startDlgOK(this, "Night mode is not active, we're using the light theme");
//                break;
//            case Configuration.UI_MODE_NIGHT_YES:
//                MyDialog.gI().startDlgOK(this, "Night mode is active, we're using dark theme");
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                break;
//        }
//    }
}