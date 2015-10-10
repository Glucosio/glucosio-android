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

    public String getNotes(){
        return this.notes;
    }
    public void setNotes(String notes){
        this.notes=notes;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getId()
    {
        return id;
    }

    public void setReading(int reading)
    {
        this.reading=reading;
    }
    public void setReading_type(String reading_type)
    {
        this.reading_type=reading_type;
    }

    public int getReading()
    {
        return this.reading;
    }
    public String getReading_type()
    {
        return this.reading_type;
    }
    public Date getCreated()
    {
        return this.created;
    }
    public void setCreated(Date created)
    {
       this.created=created;
    }
    public String getType()
    {
        return this.reading_type;
    }
}
