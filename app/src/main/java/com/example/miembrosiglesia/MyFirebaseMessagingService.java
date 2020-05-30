package com.example.miembrosiglesia;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String LOGTAG = "android-fcm";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData() != null) {

            showNotification(remoteMessage.getData());
        }
    }

    public void showNotification(Map<String, String> data) {
        String title = data.get("Titulo").toString();
        String body = data.get("Detalle").toString();
        String NOTIFICATION_CHANNEL_ID = getString(R.string.default_notification_channel_id);
        //
        Intent fullScreenIntent = new Intent(this, Bienvenido.class);
        fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_logo);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_logo)
                        .setLargeIcon(icon)
                        .setVibrate(new long[]{0, 1000, 500, 1000})
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setAutoCancel(true)
                        .setContentIntent(fullScreenPendingIntent)
                        // Use a full-screen intent only for the highest-priority alerts where you
                        // have an associated activity that you would like to launch after the user
                        // interacts with the notification. Also, if your app targets Android 10
                        // or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
                        // order for the platform to invoke this notification.
                        //.setFullScreenIntent(fullScreenPendingIntent, true)
                        .setWhen(System.currentTimeMillis())
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                        //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(icon))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Notification",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("Descripcion");
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Random ramdom = new Random();
        int idNoti = ramdom.nextInt(8000);
        notificationManager.notify(idNoti /* ID of notification */, notificationBuilder.build());
    }
}



