package org.glucosio.android.receivers;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.glucosio.android.tools.GlucosioAlarmManager;

public class GlucosioBroadcastReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            // Set alarms at boot
            GlucosioAlarmManager alarmManager = new GlucosioAlarmManager(context);
            alarmManager.setAlarms();

            Toast.makeText(context, "Setting alarms...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Test!", Toast.LENGTH_SHORT).show();
        }
    }
}