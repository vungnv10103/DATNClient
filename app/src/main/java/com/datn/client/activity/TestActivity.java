package com.datn.client.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.datn.client.R;
import com.datn.client.databinding.ActivityTestBinding;
import com.datn.client.helper.MyNavigationBar;
import com.datn.client.ui.components.MyDialog;
import com.google.android.material.textfield.TextInputEditText;

public class TestActivity extends AppCompatActivity {
    private final static String TAG = TestActivity.class.getSimpleName();
    private ActivityTestBinding binding;
    int height;
    int width;
    TextView btnDemo;

    ActionMode.Callback callbackActionMode = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.bottom_action_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, @NonNull MenuItem item) {
            int itemID = item.getItemId();
            if (itemID == R.id.item_selected_all) {
                MyDialog.gI().startDlgOK(TestActivity.this, item.getTitle() + "");
                return true;
            } else if (itemID == R.id.item_delete) {
                MyDialog.gI().startDlgOK(TestActivity.this, item.getTitle() + "");
                return true;
            } else if (itemID == R.id.item_seen_all) {
                MyDialog.gI().startDlgOK(TestActivity.this, item.getTitle() + "");
                return true;
            }
            MyDialog.gI().startDlgOK(TestActivity.this, item.getTitle() + "123");
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };

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
        ActionMode actionMode = startSupportActionMode(callbackActionMode);
        if (actionMode != null) {
            actionMode.setTitle("1 selected");
        }
        showToast("onCreate");
        // All the necessary UI elements should be initialized

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels + MyNavigationBar.gI().getNavigationBarHeight(TestActivity.this);
        width = displayMetrics.widthPixels;

        initUI();


    }

    @Override
    protected void onStart() {
        super.onStart();
        showToast("onStart");
        // Code that maintains the UI, start async tasks (get data from API or database), register listeners
        initEventClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showToast("onResume");
        // Starts animations
//        MyOverlayMsgDialog.gI().showOverlayMsgDialog(this, MyOverlayMsgDialog.gI().getDefaultOverlayMessage(this), null);

    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof TextInputEditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

//    private void demo() {
//        binding.chat.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//            Rect r = new Rect();
//            binding.chat.getWindowVisibleDisplayFrame(r);
//            int screenHeight = binding.chat.getRootView().getHeight();
//            showLogW("demo", screenHeight);
//            int keyboardHeight = screenHeight - (r.bottom);
//            MyDialog.gI().startDlgOK(ChatActivity.this, keyboardHeight);
//
//        });
//    }

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

    private void showToast(@NonNull Object message) {
        Log.d(TAG, message.toString());
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEventClick() {
        btnDemo.setOnTouchListener(touchListener);
    }

    private void initUI() {
        btnDemo = binding.btnDemo;
    }
}