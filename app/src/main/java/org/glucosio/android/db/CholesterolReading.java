package org.glucosio.android.db;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class CholesterolReading extends RealmObject {
    @PrimaryKey
    private long id;

    private int totalReading;
    private int LDLReading;
    private int HDLReading;
    private Date created;

    public CholesterolReading() {
    }

    public CholesterolReading(int totalReading, int LDLReading, int HDLReading, Date created) {
        // mg/dL
        // 0-200
        this.totalReading=totalReading;
        this.LDLReading = LDLReading;
        this.HDLReading = HDLReading;
        this.created=created;
    }

    public int getTotalReading() {
        return totalReading;
    }

    public void setTotalReading(int totalReading) {
        this.totalReading = totalReading;
    }

    public int getLDLReading() {
        return LDLReading;
    }

    public void setLDLReading(int LDLReading) {
        this.LDLReading = LDLReading;
    }

    public int getHDLReading() {
        return HDLReading;
    }

    public void setHDLReading(int HDLReading) {
        this.HDLReading = HDLReading;
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
