package de.valeapps.davinci;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Utils.scheduleSubstituteTableJob(context);
            Utils.scheduleTimeTableJob(context);
        }
    }
}
