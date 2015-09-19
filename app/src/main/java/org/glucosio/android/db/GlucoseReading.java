package org.glucosio.android.db;

/**
 * Created by ahmar on 17/8/15.
 */
public class GlucoseReading {

    int _reading;
    int _id;
    String _reading_type;
    String _notes;
    int _user_id;
    String _created;

    public GlucoseReading()
    {

    }

    public GlucoseReading(int reading,String reading_type,String created,String notes)
    {
        this._reading=reading;
        this._reading_type=reading_type;
        this._created=created;
        this._notes=notes;
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
    public int get_id()
    {
        return this._id;
    }
    public void set_id(int id)
    {
        this._id=id;
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
