package com.datn.client.ui.components;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.datn.client.services.ApiService;
import com.datn.client.ui.BasePresenter;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;
import java.util.Objects;

public class MyOverlayMsgDialog {
    private static MyOverlayMsgDialog instance;

    public static MyOverlayMsgDialog gI() {
        if (instance == null) {
            instance = new MyOverlayMsgDialog();
        }
        return instance;
    }

    public void showOverlayMsgDialog(Context context, @NonNull List<OverlayMessage> overlayMessages, BasePresenter basePresenter) {
        if (overlayMessages.isEmpty()) {
            return;
        }
        OverlayMessage overlayMessage = overlayMessages.get(0);
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
            Glide.with(context)
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
            Glide.with(context).clear(imgMessage);
            progressLoading.setVisibility(View.GONE);
            imgMessage.setVisibility(View.VISIBLE);
            tvStatusImg.setVisibility(View.VISIBLE);
            imgMessage.setImageResource(R.drawable.lover_taylor);
        }
        tvTitleImg.setText(overlayMessage.getTitle_image());
        tvContentImg.setText(overlayMessage.getContent_image());
        tvTitle.setText(overlayMessage.getTitle());
        tvContent.setText(overlayMessage.getContent());
        btnAction.setText(overlayMessage.getText_action());


        final AlertDialog dialog = builder.create();
        //btnAction.setOnClickListener(overlayMessage.getAction());
        btnAction.setOnClickListener(v -> {
            dialog.dismiss();
        });
        closeButton.setOnClickListener(v -> {
            // TODO update status overlay message
            basePresenter.updateStatusOverlayMessage(overlayMessage.get_id());
            dialog.dismiss();
        });

        dialog.setView(view);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.show();
    }
}
