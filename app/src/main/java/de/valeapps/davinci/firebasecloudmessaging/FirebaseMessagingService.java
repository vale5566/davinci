package de.valeapps.davinci.firebasecloudmessaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.google.firebase.messaging.RemoteMessage;

import de.valeapps.davinci.R;
import de.valeapps.davinci.substitutetable.SubstituteTableActivity;
import de.valeapps.davinci.timetable.UpdateTimeTableServiceManually;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if(remoteMessage.getNotification() != null) {
            String messageText = remoteMessage.getNotification().getBody();
            assert messageText != null;
            if(messageText.equals("Stundenplan")) {
                startService(new Intent(getApplicationContext(), UpdateTimeTableServiceManually.class));
            } else {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (sp.getBoolean("notifcation", true)) {
                    sendNotification(messageText);
                }
            }
        }
    }
    private void sendNotification(String messageBody) {
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(getApplicationContext(), SubstituteTableActivity.class), 0);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setStyle(new Notification.BigTextStyle()
                .bigText(messageBody)
                .setBigContentTitle("Da Vinci"))
                .setSmallIcon(R.drawable.ic_school_black_24dp)
                .setAutoCancel(true)
                .setContentIntent(pi);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(0, builder.build());
    }
}
