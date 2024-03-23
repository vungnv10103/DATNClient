package com.datn.client.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.datn.client.R;
import com.datn.client.databinding.ActivityTestBinding;
import com.datn.client.helper.MyNavigationBar;
import com.datn.client.ui.components.MyOverlayMsgDialog;

public class TestActivity extends AppCompatActivity {
    private final static String TAG = TestActivity.class.getSimpleName();
    private ActivityTestBinding binding;

    TextView _view;
    ViewGroup _root;
    private int _xDelta;
    private int _yDelta;
    int height;
    int width;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.test), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        showToast("onCreate");
        // All the necessary UI elements should be initialized

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels + MyNavigationBar.gI().getNavigationBarHeight(TestActivity.this);
        width = displayMetrics.widthPixels;

        binding.btnDemo.setOnTouchListener(touchListener);

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

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View view, @NonNull MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = X - _xDelta;
                layoutParams.topMargin = Y - _yDelta;
                layoutParams.rightMargin = -250;
                layoutParams.bottomMargin = -250;
                view.setLayoutParams(layoutParams);
                break;
        }
        _root.invalidate();
        return true;
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        private int lastX = 0;
        private int lastY = 0;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, @NonNull MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    int left = v.getLeft() + dx;
                    int top = v.getTop() + dy;
                    int right = v.getRight() + dx;
                    int bottom = v.getBottom() + dy;
                    if (left < 0) {
                        left = 0;
                        right = left + v.getWidth();
                    }
                    if (right > width) {
                        right = width;
                        left = right - v.getWidth();
                    }
                    if (top < 0) {
                        top = 0;
                        bottom = top + v.getHeight();
                    }
                    if (bottom > height) {
                        bottom = height;
                        top = bottom - v.getHeight();
                    }
                    v.layout(left, top, right, bottom);
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    break;
                }
            }
            return true;
        }
    };

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