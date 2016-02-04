package org.glucosio.android.db;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class HB1ACReading extends RealmObject {
    @PrimaryKey
    private long id;

    private double reading;
    private Date created;

    public HB1ACReading() {
    }

    public HB1ACReading(double reading,Date created) {
        // %
        this.reading=reading;
        this.created=created;
    }

    public double getReading() {
        return reading;
    }

    public void setReading(double reading) {
        this.reading = reading;
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
