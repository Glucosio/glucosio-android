package org.glucosio.android.db;

import java.util.Date;
import java.util.UUID;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class GlucoseReading extends RealmObject {

    @PrimaryKey
    private int id;

    private int reading;
    private String reading_type;
    private String notes;
    private int user_id;
    private Date created;

    public GlucoseReading() {
    }

    public GlucoseReading(int reading,String reading_type,Date created,String notes) {
        this.reading=reading;
        this.reading_type=reading_type;
        this.created=created;
        this.notes=notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReading() {
        return reading;
    }

    public void setReading(int reading) {
        this.reading = reading;
    }

    public String getReading_type() {
        return reading_type;
    }

    public void setReading_type(String reading_type) {
        this.reading_type = reading_type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
