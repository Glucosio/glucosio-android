package org.glucosio.android.receivers;

import android.content.Context;
import android.content.Intent;

import org.glucosio.android.tools.GlucosioAlarmManager;
import org.glucosio.android.tools.GlucosioNotificationManager;

public class GlucosioBroadcastReceiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            setAlarms(context);
        } else {
            if (intent.getBooleanExtra("glucosio_reminder", false)) {
                GlucosioNotificationManager notificationManager = new GlucosioNotificationManager(context);
                notificationManager.sendReminderNotification();
            } else {
                setAlarms(context);
            }
        }
    }

    private void setAlarms(Context context) {
        GlucosioAlarmManager alarmManager = new GlucosioAlarmManager(context);
        alarmManager.setAlarms();
    }
}