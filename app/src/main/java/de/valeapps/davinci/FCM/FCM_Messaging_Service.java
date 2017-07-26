package de.valeapps.davinci.FCM;


import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import de.valeapps.davinci.R;

public class FCM_Messaging_Service extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification().getSound().equals("default")) {
            displayNotifcationSound(remoteMessage.getData().get("title"), remoteMessage.getData().get("content"));
        } else {
            displayNotifcation(remoteMessage.getData().get("title"), remoteMessage.getData().get("content"));
        }

        Log.d("DaVinci", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("test", "Message data payload: " + remoteMessage.getData());
        }
    }


    private void displayNotifcationSound(String title, String contentText) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLights(Color.CYAN, 3000, 3000)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(14, notification);
    }

    private void displayNotifcation(String title, String contentText) {
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(14, notification);
    }
}
