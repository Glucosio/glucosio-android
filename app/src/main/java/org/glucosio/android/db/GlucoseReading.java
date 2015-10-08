package org.glucosio.android.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.List;

@Table(name = "GlucoseReadings")
public class GlucoseReading extends RealmObject {

    @PrimaryKey
    int id;
    int reading;
    String reading_type;
    String notes;
    int user_id;

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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    String created;

    /*public GlucoseReading() {
        super();
    }*/

    public GlucoseReading(int reading,String reading_type,String created,String notes)
    {
        //initiate
    }

    public static GlucoseReading getGlucoseReading(int id) {
       //a single reading
    }

    public static List<GlucoseReading> getAllGlucoseReading() {
        //all rradings
    }

    public static List<GlucoseReading> getGlucoseReadings(String where) {
        //where reading
    }

    public static List<GlucoseReading> getGlucoseReadings(String where, Object args) {
       //where with args
    }

    public static List<GlucoseReading> getGlucoseReadings(String where, Object args, String[] columns) {
        //where with args and required columns
    }

    public static List<GlucoseReading> getGlucoseReadingsByGroup(String[] columns, String groupBy) {
        // required columns and group by week
    }


}
