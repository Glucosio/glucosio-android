package org.glucosio.android.db;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Reminder extends RealmObject {
    @PrimaryKey
    private long id;

    private Date alarmTime;
    private boolean oneTime;
    private String metric;

    public Reminder() {
    }

    public Reminder(Date alarmTime, String metric, boolean oneTime) {
        this.alarmTime = alarmTime;
        this.metric = metric;
        this.oneTime = oneTime;
    }

    public Date getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public boolean isOneTime() {
        return oneTime;
    }

    public void setOneTime(boolean oneTime) {
        this.oneTime = oneTime;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
