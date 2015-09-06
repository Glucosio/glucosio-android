package org.glucosio.android.db;

/**
 * Created by ahmar on 17/8/15.
 */
public class GlucoseReading {

    int _reading;
    int _id;
    int _reading_type;
    int _user_id;
    String _created;

    public GlucoseReading()
    {

    }

    public GlucoseReading(int reading,int reading_type,String created)
    {
        this._reading=reading;
        this._reading_type=reading_type;
        this._created=created;
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
    public void set_reading_type(int reading_type)
    {
        this._reading_type=reading_type;
    }

    public int get_reading()
    {
        return this._reading;
    }
    public int get_reading_type()
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
        return type(this._reading_type);
    }
    public String type(int reading_type)
    {
        String[] enums={"Before fast","after breakfast","random"};
        try{
            return   enums[reading_type+1];
        }
        catch(ArrayIndexOutOfBoundsException e){
            return "";
        }
    }
}
