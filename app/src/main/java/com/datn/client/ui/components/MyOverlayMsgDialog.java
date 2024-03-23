package com.datn.client.ui.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.datn.client.R;
import com.datn.client.models.OverlayMessage;
import com.datn.client.ui.BasePresenter;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyOverlayMsgDialog {
    private BasePresenter basePresenter;
    private OverlayMessage mOverlayMessage;
    private static MyOverlayMsgDialog instance;
    private AlertDialog mDialog;
    private static final int MEASUREMENT_ERROR = 100;

    public static MyOverlayMsgDialog gI() {
        if (instance == null) {
            instance = new MyOverlayMsgDialog();
        }
        return instance;
    }

    public void showOverlayMsgDialog(Activity context, @NonNull List<OverlayMessage> overlayMessages, BasePresenter basePresenter) {
        if (overlayMessages.isEmpty()) {
            return;
        }
        this.basePresenter = basePresenter;
        OverlayMessage overlayMessage = overlayMessages.get(0);
        this.mOverlayMessage = overlayMessage;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // init view
        View view = inflater.inflate(R.layout.layout_overlay_msg, null);
        Button closeButton = view.findViewById(R.id.btn_close);
        TextView tvNotification = view.findViewById(R.id.tv_notification);
        CircularProgressIndicator progressLoading = view.findViewById(R.id.progressbar_image);
        progressLoading.setVisibility(View.VISIBLE);
        ShapeableImageView imgMessage = view.findViewById(R.id.img_msg);
        imgMessage.setVisibility(View.INVISIBLE);
        TextView tvStatusImg = view.findViewById(R.id.tv_status_img);
        tvStatusImg.setVisibility(View.GONE);
        TextView tvTitleImg = view.findViewById(R.id.tv_title_img);
        TextView tvContentImg = view.findViewById(R.id.tv_content_img);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvContent = view.findViewById(R.id.tv_content);
        Button btnAction = view.findViewById(R.id.btn_action);

        tvNotification.setText(overlayMessage.getNotification());
        if (!overlayMessage.getImage().isEmpty()) {
            Glide.with(context.getBaseContext())
                    .load(overlayMessage.getImage())
                    .error(R.drawable.lover_taylor)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                            MyDialog.gI().startDlgOK(context, e != null ? e.getMessage() : "Error load image");
                            progressLoading.setVisibility(View.GONE);
                            tvStatusImg.setVisibility(View.VISIBLE);
                            imgMessage.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                            progressLoading.setVisibility(View.GONE);
                            imgMessage.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .into(imgMessage);
        } else {
            Glide.with(context.getBaseContext()).clear(imgMessage);
            progressLoading.setVisibility(View.GONE);
            imgMessage.setVisibility(View.VISIBLE);
            tvStatusImg.setVisibility(View.VISIBLE);
            imgMessage.setImageResource(R.drawable.lover_taylor);
        }
        imgMessage.setTag("image overlay");
//        imgMessage.setOnLongClickListener(onLongClickListenerImage);
//        imgMessage.setOnDragListener(onDragListenerImage);
        tvTitleImg.setText(overlayMessage.getTitle_image());
        tvContentImg.setText(overlayMessage.getContent_image());
        tvTitle.setText(overlayMessage.getTitle());
        tvContent.setText(overlayMessage.getContent());
        btnAction.setText(overlayMessage.getText_action());



        View layoutOverlay = view.findViewById(R.id.layout_overlay);
//        layoutOverlay.setOnLongClickListener(onLongClickListener);
//        layoutOverlay.setOnDragListener(onDragListener);
        layoutOverlay.setOnTouchListener(onTouchListener);

        mDialog = builder.create();

        //btnAction.setOnClickListener(overlayMessage.getAction());
        btnAction.setOnClickListener(v -> mDialog.dismiss());
        closeButton.setOnClickListener(v -> {
            // TODO update status overlay message
            dismissOverlay(overlayMessage.get_id());
        });

        mDialog.setView(view);
        Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    public String getStringResource(@NonNull Context context, int id) {
        return context.getString(id);
    }

    private void dismissOverlay(String idOverlay) {
        if (basePresenter != null) {
            basePresenter.updateStatusOverlayMessage(idOverlay);
        }
        mDialog.dismiss();
    }

    public List<OverlayMessage> getDefaultOverlayMessage(Context context) {
        String notification = getStringResource(context, R.string.new_release);
        String urlImage = "https://stech-993p.onrender.com/images/lover_taylor.png";
        String titleImage = getStringResource(context, R.string.lover_taylor);
        String contentImage = getStringResource(context, R.string.content_image);
        String title = getStringResource(context, R.string.lover);
        String content = getStringResource(context, R.string.content);
        String textAction = getStringResource(context, R.string.stream_now);
        List<OverlayMessage> list = new ArrayList<>();
        list.add(new OverlayMessage("", -1, notification, urlImage, titleImage,
                contentImage, title, content, textAction, v1 -> {
            String id = Math.random() + "";
            switch (id) {
                case "0":
                    MyDialog.gI().startDlgOK(context, getStringResource(context, R.string.app_name), id, null, null);
                    break;
                case "1":
                    MyDialog.gI().startDlgOK(context, getStringResource(context, R.string.app_name), id, null, null);
                default:
                    MyDialog.gI().startDlgOK(context, getStringResource(context, R.string.app_name), id, null, null);
                    break;
            }
        }));
        return list;
    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        private int lastX;
        private int lastY;
        private int LIMIT_LEFT = 0;
        private int LIMIT_RIGHT = 0;
        private int LIMIT_BOTTOM = 0;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, @NonNull MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    Context context = v.getContext();
                    int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
//                    int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
                    LIMIT_LEFT = (screenWidth / 2) * -1 + MEASUREMENT_ERROR / 10;
                    LIMIT_RIGHT = (int) Math.floor((double) screenWidth * 1.5) - MEASUREMENT_ERROR + 10;
                    LIMIT_BOTTOM = 2550;
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    int left = v.getLeft() + dx;
                    int top = v.getTop() + dy;
                    int right = v.getRight() + dx;
                    int bottom = v.getBottom() + dy;

                    if (top < 0) {
                        top = 0;
                        bottom = top + v.getHeight();
                    }
                    Log.w("ACTION_MOVE", left + "+" + right);
                    Log.w("ACTION_MOVE", bottom + "");
                    if (left <= LIMIT_LEFT || bottom >= LIMIT_BOTTOM) {
                        dismissOverlay(mOverlayMessage.get_id());
                    }
                    if (right >= LIMIT_RIGHT || bottom >= LIMIT_BOTTOM) {
                        dismissOverlay(mOverlayMessage.get_id());
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

    View.OnTouchListener onTouchListenerDefault = new View.OnTouchListener() {
        private int lastX = 0;
        private int lastY = 0;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, @NonNull MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    Log.w("ACTION_DOWN:(lastX-lastY)", lastX + "-" + lastY);
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    Log.w("ACTION_MOVE:(dx-dy)", dx + "-" + dy);
                    int left = v.getLeft() + dx;
                    int top = v.getTop() + dy;
                    int right = v.getRight() + dx;
                    int bottom = v.getBottom() + dy;
                    Log.w("ACTION_MOVE:(left-top-right-bottom)", left + "-" + top + "-" + right + "-" + bottom);
                    v.layout(left, top, right, bottom);
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    Log.w("ACTION_MOVE:(lastX-lastY)", lastX + "-" + lastY);
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    Log.w("ACTION_UP:(dx-dy)", dx + "-" + dy);
                    break;
                }
            }
            return true;
        }
    };

    View.OnLongClickListener onLongClickListener = v -> {
        ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
        ClipData dragData = new ClipData((CharSequence) v.getTag(),
                new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                item);
        MyDragShadowBuilder myShadow = new MyDragShadowBuilder(v);
        v.startDragAndDrop(dragData, myShadow, null, 0);
        return true;
    };
    View.OnDragListener onDragListener = (v, event) -> {
        int dragEvent = event.getAction();
        switch (dragEvent) {
            case DragEvent.ACTION_DRAG_STARTED:
                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
//                    Log.d("ACTION_DRAG_STARTED1", event.toString());
                    return true;
                }
                Log.d("ACTION_DRAG_STARTED2", event.toString());
                return false;
            case DragEvent.ACTION_DRAG_ENTERED:
//                Log.d("ACTION_DRAG_ENTERED", event.toString());
                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
//                Log.d("ACTION_DRAG_LOCATION", event.toString());
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
//                Log.d("ACTION_DRAG_EXITED", event.toString());
                return true;
            case DragEvent.ACTION_DROP:
                Log.d("ACTION_DROP", event.toString());
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                if (!event.getResult()) {
                    mDialog.dismiss();
                }
                Log.d("ACTION_DROP", event.toString());
                return true;

            default:
                Log.e("DragDrop Example", "Unknown action type received by View.OnDragListener.");
                break;
        }
        return false;
    };

    View.OnLongClickListener onLongClickListenerImage = v -> {
        // https://developer.android.com/develop/ui/views/touch-and-input/drag-drop
        ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
        ClipData dragData = new ClipData((CharSequence) v.getTag(),
                new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                item);

        // Instantiate the drag shadow builder.
        MyDragShadowBuilder myShadow = new MyDragShadowBuilder(v);

        // Start the drag.
        v.startDragAndDrop(dragData,   // The data to be dragged.
                myShadow,              // The drag shadow builder.
                null,                  // No need to use local data.
                0                      // Flags. Not currently used, set to 0.
        );

        // Indicate that the long-click is handled.
        return true;
    };

    View.OnDragListener onDragListenerImage = (v, event) -> {
        // https://developer.android.com/develop/ui/views/touch-and-input/drag-drop
        // Handle each of the expected events.
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // Determine whether this View can accept the dragged data.
                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    // As an example, apply a blue color tint to the View to indicate that it can accept data.
                    ((ImageView) v).setColorFilter(Color.BLUE);
                    // Invalidate the view to force a redraw in the new tint.
                    v.invalidate();
                    Log.d("ACTION_DRAG_STARTED1", event.toString());
                    // Return true to indicate that the View can accept the dragged data.
                    return true;
                }
                // Return false to indicate that, during the current drag and drop
                // operation, this View doesn't receive events again until
                // ACTION_DRAG_ENDED is sent.
                Log.d("ACTION_DRAG_STARTED2", event.toString());
                return false;
            case DragEvent.ACTION_DRAG_ENTERED:
                // Apply a green tint to the View.
                ((ImageView) v).setColorFilter(Color.GREEN);
                // Invalidate the view to force a redraw in the new tint.
                v.invalidate();
                // Return true. The value is ignored.
                Log.d("ACTION_DRAG_ENTERED", event.toString());
//                    if (((View) event.getLocalState()).getId() == R.id.btn_action) {
//                        Toast.makeText(context, "OKE", Toast.LENGTH_SHORT).show();
//                    }

                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
                // Ignore the event.
                Log.d("ACTION_DRAG_LOCATION", event.toString());
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                // Reset the color tint to blue.
                ((ImageView) v).setColorFilter(Color.RED);
                // Invalidate the view to force a redraw in the new tint.
                v.invalidate();
                // Return true. The value is ignored.
                Log.d("ACTION_DRAG_EXITED", event.toString());
                return true;
            case DragEvent.ACTION_DROP:
                // Get the item containing the dragged data.
                ClipData.Item item = event.getClipData().getItemAt(0);
                // Get the text data from the item.
                CharSequence dragData = item.getText();
                // Display a message containing the dragged data.
                Log.w("ACTION_DROP", dragData.toString());
//                Toast.makeText(mContext, "Dragged data is " + dragData, Toast.LENGTH_LONG).show();
                // Turn off color tints.
                ((ImageView) v).clearColorFilter();
                // Invalidate the view to force a redraw.
                v.invalidate();
                // Return true. DragEvent.getResult() returns true.
                Log.d("ACTION_DROP", event.toString());
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                // Turn off color tinting.
                ((ImageView) v).clearColorFilter();
                // Invalidate the view to force a redraw.
                v.invalidate();
                // Do a getResult() and displays what happens.
                if (event.getResult()) {
                    Log.w("ACTION_DRAG_ENDED", "The drop was handled.");
                } else {
                    Log.w("ACTION_DRAG_ENDED", "The drop didn't work.");
                }
                // Return true. The value is ignored.
                Log.d("ACTION_DROP", event.toString());
                return true;

            // An unknown action type is received.
            default:
                Log.e("DragDrop Example", "Unknown action type received by View.OnDragListener.");
                break;
        }
        return false;
    };
}
