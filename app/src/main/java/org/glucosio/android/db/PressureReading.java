package org.glucosio.android.db;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PressureReading extends RealmObject {
    @PrimaryKey
    private long id;

    private int minReading;
    private int maxReading;
    private Date created;

    public PressureReading() {
    }

    public PressureReading(int minReading, int maxReading, Date created) {
        // mm/Hg
        this.minReading=minReading;
        this.maxReading=maxReading;
    }

    public int getMinReading() {
        return minReading;
    }

    public void setMinReading(int minReading) {
        this.minReading = minReading;
    }

    public int getMaxReading() {
        return maxReading;
    }

    public void setMaxReading(int maxReading) {
        this.maxReading = maxReading;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
