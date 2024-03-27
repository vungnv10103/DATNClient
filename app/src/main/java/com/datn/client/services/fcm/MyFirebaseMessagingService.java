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
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.datn.client.MainActivity;
import com.datn.client.R;
import com.datn.client.ui.auth.LoginActivity;
import com.datn.client.ui.checkout.CheckoutPresenter;
import com.datn.client.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.URL;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if (!message.getData().isEmpty()) {
            String title = message.getData().get("title");
            String body = message.getData().get("body");
            String imageURL = message.getData().get("imageURL");
            String mType = message.getData().get("type");
            Log.w("onMessageReceived", title + "-" + body + "-" + imageURL);
            sendNotification(title, body, imageURL);
//            sendNotification(title, body);

            if (mType != null) {
                if (Integer.parseInt(mType) == CheckoutPresenter.PAYMENT_METHOD.E_BANKING.getValue()) {
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

    private void sendNotification(String title, String messageBody, String imageURL) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Stech", "Stech",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Get the layouts to use in the custom notification
        RemoteViews notificationLayout = createNotificationView(R.layout.layout_notification_small, title, messageBody);
        RemoteViews notificationLayoutExpanded = createNotificationView(R.layout.layout_notification_large, title, messageBody);

//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, "Stech")
//                        .setSmallIcon(R.drawable.logo_no)
//                        .setCustomContentView(notificationLayout)
//                        .setCustomBigContentView(notificationLayoutExpanded)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent);
//
//        notificationManager.notify(0, notificationBuilder.build());
        // Apply the layouts to the notification.
//        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_app_gradient);
        Bitmap bitmapIcon = null;
        if (!imageURL.trim().isEmpty()) {
            try {
                URL url = new URL(imageURL);
                bitmapIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        Notification customNotification = new NotificationCompat.Builder(this, "Stech")
                .addAction(new NotificationCompat.Action(NotificationCompat.Action.SEMANTIC_ACTION_NONE, "Go", pendingIntent))
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

        notificationManager.notify(666, customNotification);
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

    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Stech")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.logo_app_black)
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
