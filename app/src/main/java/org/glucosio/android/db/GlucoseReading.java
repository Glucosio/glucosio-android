package org.glucosio.android.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class GlucoseReading extends RealmObject {

    @PrimaryKey
    private long id;

    private int reading;
    private String reading_type;
    private String notes;
    private int user_id;
    private Date created;
    Realm realm;
    public GlucoseReading() {
        //it should be the context  || How can we achieve it u can refer
        this.realm=Realm.getInstance(this);
    }

    public GlucoseReading(int reading,String reading_type,Date created,String notes)
    {
        this.reading=reading;
        this.reading_type=reading_type;
        this.created=created;
        this.notes=notes;
    }

    public static RealmResults<GlucoseReading> getGlucoseReading(int id) {
        return realm.where(GlucoseReading.class)
                        .equalTo("id",Integer.toString(id))
                        .findFirst();
    }

    public static RealmResults<GlucoseReading> getAllGlucoseReading() {
        return realm.where(GlucoseReading.class)
                    .findAll();
    }

    public static RealmResults<GlucoseReading> getGlucoseReadings(String where) {
        return realm.where(GlucoseReading.class)
                    .
    }

    public static List<GlucoseReading> getGlucoseReadings(String where, Object args) {
        return new Select()
                .from(GlucoseReading.class)
                .orderBy("created DESC")
                .where(where, args)
                .execute();
    }

    public static List<GlucoseReading> getGlucoseReadings(String where, Object args, String[] columns) {
        return new Select(columns)
                .from(GlucoseReading.class)
                .orderBy("created DESC")
                .where(where, args)
                .execute();
    }

    public static List<GlucoseReading> getGlucoseReadingsByGroup(String[] columns, String groupBy) {
        return new Select(columns)
                .from(GlucoseReading.class)
                .orderBy("created DESC")
                .groupBy(groupBy)
                .execute();
    }

    public String getNotes(){
        return this.notes;
    }
    public void setNotes(String notes){
        this.notes=notes;
    }
    public int getUser_id()
    {
        return this.user_id;
    }
    public void setUser_id(int user_id)
    {
        this.user_id=user_id;
    }
    public long getId()
    {
        return this.getId();
    }
    public void setId()
    {
        String uniqueID= UUID.randomUUID().toString();
        this.id=Long.parseLong(uniqueID);
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
