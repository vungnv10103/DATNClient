package com.datn.client.services.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.datn.client.R;
import com.datn.client.response.ConversationDisplay;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.chat.ChatActivity;
import com.datn.client.ui.components.MyDialog;
import com.datn.client.utils.Constants;
import com.datn.client.utils.PAYMENT_METHOD;
import com.datn.client.utils.TYPE_NOTIFICATION;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        MyDialog.gI().startDlgOK(getApplicationContext(), token);
    }

    public static final String KEY_TEXT_REPLY = "key_text_reply";
    private static final String KEY_LIKE_REPLY = "key_like_reply";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if (!message.getData().isEmpty()) {
            String title = message.getData().get("title");
            String body = message.getData().get("body");
            String imageURL = message.getData().get("imageURL");
            String mType = message.getData().get("type");
//            Log.w("onMessageReceived", title + "-" + body + "-" + imageURL);
            sendNotification(new ConversationDisplay(), mType, title, body, imageURL);

            if (mType != null && !mType.isEmpty()) { // send notification in web view e-banking
                if (Integer.parseInt(mType) == PAYMENT_METHOD.E_BANKING.getValue()) {
                    // Gửi Broadcast với nội dung thông báo
                    Intent intent = new Intent("com.datn.client.NOTIFICATION_RECEIVED");
                    intent.putExtra("title", title);
                    intent.putExtra("body", body);
                    intent.putExtra("imageURL", imageURL);
                    intent.putExtra("mType", mType);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }
            }
        }
    }

    private void sendNotification(ConversationDisplay conversationDisplay, String typeNotification, String title, String messageBody, String imageURL) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Stech", "Stech",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }
        // Get the layouts to use in the custom notification
        RemoteViews notificationLayout = createNotificationView(R.layout.layout_notification_small, title, messageBody);
        RemoteViews notificationLayoutExpanded = createNotificationView(R.layout.layout_notification_large, title, messageBody);


        Notification customNotification;
        if (Integer.parseInt(typeNotification) == TYPE_NOTIFICATION.DEFAULT.getValue()) {
            customNotification = createNotificationDefault(notificationLayout, notificationLayoutExpanded, imageURL);
        } else {
            customNotification = createNotificationMessage(conversationDisplay, notificationLayout, notificationLayoutExpanded);
        }
        notificationManager.notify(666, customNotification);

    }

    @NonNull
    @Contract("_ -> new")
    private Intent getMessageReplyIntent(String conversationID) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("conversationID", conversationID);
        return intent;
    }

    @NonNull
    private Notification createNotificationMessage(
            @NonNull ConversationDisplay conversationDisplay,
            RemoteViews notificationLayout,
            RemoteViews notificationLayoutExpanded) {
        String replyLabel = getString(R.string.reply);
        String likeLabel = getString(R.string.like);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();
        RemoteInput remoteLike = new RemoteInput.Builder(KEY_LIKE_REPLY)
                .setLabel(likeLabel)
                .build();
        PendingIntent likePendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                conversationDisplay.getMessage_type(),
                getMessageReplyIntent(conversationDisplay.getConversation_id()),
                PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                conversationDisplay.getMessage_type(),
                getMessageReplyIntent(conversationDisplay.getConversation_id()),
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action actionLike = new NotificationCompat.Action.Builder(R.drawable.ic_camera_24,
                (CharSequence) getString(R.string.like), likePendingIntent)
                .addRemoteInput(remoteLike)
                .build();
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_send_24,
                (CharSequence) getString(R.string.reply), replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build();
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return new NotificationCompat.Builder(this, "Stech")
                .addAction(actionLike)
                .addAction(action)
                .setSmallIcon(R.drawable.logo_app_black)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayoutExpanded)
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .build();

    }

    @NonNull
    private Notification createNotificationDefault(RemoteViews notificationLayout,
                                                   RemoteViews notificationLayoutExpanded,
                                                   String imageURL) {
        Bitmap bitmapIcon = getBitmapImageNotification(imageURL);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return new NotificationCompat.Builder(this, "Stech")
                .setSmallIcon(R.drawable.logo_app_black)
                .setLargeIcon(bitmapIcon)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayoutExpanded)
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .build();

    }

    private Bitmap getBitmapImageNotification(@NonNull String imageURL) {
        Bitmap bitmapIcon = null;
        if (!imageURL.trim().isEmpty()) {
            try {
                URL url = new URL(imageURL);
                bitmapIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return bitmapIcon;
    }


    @NonNull
    @SuppressLint("RemoteViewLayout")
    private RemoteViews createNotificationView(int layout, String title, String content) {
        boolean isNightMode = Constants.isNightMode;
        int idTitle = R.id.tv_title_notification;
        int idContent = R.id.tv_content_notification;
        RemoteViews remoteViews = new RemoteViews(getPackageName(), layout);
        if (isNightMode) {
            remoteViews.setTextColor(idTitle, ContextCompat.getColor(this, R.color.white));
            remoteViews.setTextColor(idContent, ContextCompat.getColor(this, R.color.white));
        }
        remoteViews.setTextViewText(idTitle, title);
        remoteViews.setTextViewText(idContent, content);
        return remoteViews;
    }


    @Nullable
    public CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
