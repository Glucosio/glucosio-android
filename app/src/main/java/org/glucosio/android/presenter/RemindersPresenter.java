package org.glucosio.android.presenter;

import org.glucosio.android.activity.RemindersActivity;

import java.util.Calendar;

public class RemindersPresenter {

    RemindersActivity activity;

    public RemindersPresenter(RemindersActivity activity) {
        this.activity = activity;
    }

    public Calendar getCalendar() {
        return Calendar.getInstance();
    }
}
