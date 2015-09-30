package org.glucosio.android.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "GlucoseReadings")
public class GlucoseReading extends Model {

    @Column(name = "reading")
    int _reading;

    @Column(name = "reading_type")
    String _reading_type;

    @Column(name = "notes")
    String _notes;

    @Column(name = "user_id")
    int _user_id;

    @Column(name = "created", index = true)
    String _created;

    public GlucoseReading() {
        super();
    }

    public GlucoseReading(int reading,String reading_type,String created,String notes)
    {
        this._reading=reading;
        this._reading_type=reading_type;
        this._created=created;
        this._notes=notes;
    }

    public static GlucoseReading getGlucoseReading(int id) {
        return new Select()
                .from(User.class)
                .where("id = " + id)
                .orderBy("RANDOM()")
                .executeSingle();
    }

    public static List<GlucoseReading> getAllGlucoseReading() {
        return new Select()
                .from(GlucoseReading.class)
                .orderBy("created DESC")
                .execute();
    }

    public static List<GlucoseReading> getGlucoseReadings(String where) {
        return new Select()
                .from(GlucoseReading.class)
                .orderBy("created DESC")
                .where(where)
                .execute();
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

    public String get_notes(){
        return this._notes;
    }
    public void set_notes(String notes){
        this._notes=notes;
    }
    public int get_user_id()
    {
        return this._user_id;
    }
    public void set_user_id(int user_id)
    {
        this._user_id=user_id;
    }
    public long get_id()
    {
        return this.getId();
    }

    public void set_reading(int reading)
    {
        this._reading=reading;
    }
    public void set_reading_type(String reading_type)
    {
        this._reading_type=reading_type;
    }

    public int get_reading()
    {
        return this._reading;
    }
    public String get_reading_type()
    {
        return this._reading_type;
    }
    public String get_created()
    {
        return this._created;
    }
    public void set_created(String created)
    {
       this._created=created;
    }
    public String get_type()
    {
        return this._reading_type;
    }
}
