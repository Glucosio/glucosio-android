package org.glucosio.android.presenter;

import android.widget.ListAdapter;

import org.glucosio.android.R;
import org.glucosio.android.activity.RemindersActivity;
import org.glucosio.android.adapter.RemindersAdapter;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.Reminder;

import java.util.Calendar;
import java.util.Date;

public class RemindersPresenter {

    private RemindersActivity activity;
    private DatabaseHandler db;

    public RemindersPresenter(RemindersActivity activity) {
        this.activity = activity;
        db = new DatabaseHandler(activity);
    }

    public Calendar getCalendar() {
        return Calendar.getInstance();
    }

    public void updateReminder(Reminder reminder) {
        // Create a new object RealM unattached
        db.updateReminder(reminder);
    }

    public ListAdapter getAdapter() {
        return new RemindersAdapter(activity, R.layout.activity_reminder_item, db.getReminders());
    }

    public void addReminder(long id, Date alarmTime, String metric, boolean oneTime, boolean active) {
        Reminder reminder = new Reminder(id, alarmTime, metric, oneTime, active);
        boolean added = db.addReminder(reminder);
        if (added) {
            activity.updateRemindersList();
        } else {
            activity.showDuplicateError();
        }
    }

    public void deleteReminder(long id) {
        db.deleteReminder(id);
    }
}
