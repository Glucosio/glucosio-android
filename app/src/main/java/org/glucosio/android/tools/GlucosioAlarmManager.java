package org.glucosio.android.tools;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.Reminder;
import org.glucosio.android.receivers.GlucosioBroadcastReceiver;

import java.util.Calendar;
import java.util.List;

public class GlucosioAlarmManager {
    List<Reminder> reminders;
    Context context;
    AlarmManager alarmMgr;
    DatabaseHandler db;

    public GlucosioAlarmManager(Context context) {
        this.context = context;
        this.db = new DatabaseHandler(context);
        this.alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarms() {
        reminders = db.getReminders();

        // if there are some active alarms, start receiver to set them at boot
        enableBootReceiver(db.areRemindersActive());

        // Set an alarm for each date
        for (int i = 0; i < reminders.size(); i++) {
            Reminder reminder = reminders.get(i);

            Intent intent = new Intent(context, GlucosioBroadcastReceiver.class);
            intent.putExtra("metric", reminder.getMetric());

            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, (int) reminder.getId(), intent, 0);

            if (reminder.isActive()) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(reminder.getAlarmTime());
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, alarmIntent);

                Toast.makeText(context, "Setting alarm" + reminder.getId(), Toast.LENGTH_LONG).show();
            } else {
                alarmMgr.cancel(alarmIntent);
            }
        }
    }

    private void enableBootReceiver(boolean value) {
        ComponentName receiver = new ComponentName(context, GlucosioBroadcastReceiver.class);
        PackageManager pm = context.getPackageManager();

        int componentState = value ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

        pm.setComponentEnabledSetting(receiver,
                componentState,
                PackageManager.DONT_KILL_APP);
    }
}
