package de.valeapps.davinci;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import de.valeapps.davinci.substitutetable.UpdateSubstituteTableService;


public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent startServiceIntent = new Intent(context, UpdateSubstituteTableService.class);

            PendingIntent startServicePendingIntent = PendingIntent.getService(context, 0, startServiceIntent, 0);

            Calendar calendar = Calendar.getInstance();

            calendar.setTimeInMillis(System.currentTimeMillis());

            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), MainActivity.UPDATE_TIME_TABLE_IN_MILLISECONDS, startServicePendingIntent);
        }
    }
}
