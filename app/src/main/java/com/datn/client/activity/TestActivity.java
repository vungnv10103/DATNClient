package com.datn.client.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.datn.client.R;
import com.datn.client.ui.components.MyOverlayMsgDialog;

public class TestActivity extends AppCompatActivity {
    private final static String TAG = TestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        showToast("onCreate");
        // All the necessary UI elements should be initialized
    }

    @Override
    protected void onStart() {
        super.onStart();
        showToast("onStart");
        // Code that maintains the UI, start async tasks (get data from API or database), register listeners
    }

    @Override
    protected void onResume() {
        super.onResume();
        showToast("onResume");
        // Starts animations
        MyOverlayMsgDialog.gI().showOverlayMsgDialog(this, MyOverlayMsgDialog.gI().getDefaultOverlayMessage(this), null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        showToast("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        showToast("onStop");
        // Unregisters listeners and resources allocated in onStart()
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        showToast("onRestart");
        // Cursor objects should be re-queried
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showToast("onDestroy");
    }

    private void showToast(String message) {
        Log.d(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}