package org.glucosio.android.db;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Reminder extends RealmObject {
    @PrimaryKey
    private long id;

    private Date alarmTime;
    private boolean oneTime;
    private boolean active;
    private String label;
    private String metric;

    public Reminder() {
    }

    public Reminder(long id, Date alarmTime, String label, String metric, boolean oneTime, boolean active) {
        this.id = id;
        this.label = label;
        this.alarmTime = alarmTime;
        this.metric = metric;
        this.oneTime = oneTime;
        this.active = active;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setLabel(String label){
        this.label = label;
    }

    public String getLabel(){
        return label;
    }
}
