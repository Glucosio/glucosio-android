package org.glucosio.android.db;

public class User {


    int _id;
    String _name;
    String _preferred_language;
    String _country;
    int _age;
    String _gender;
    int _d_type; //diabetes type
    String _preferred_unit; // preferred unit

    public User()
    {

    }

    public User(int id, String name,String preferred_language, String country, int age, String gender,int dType, String pUnit)
    {
        this._id=id;
        this._name=name;
        this._preferred_language=preferred_language;
        this._country=country;
        this._age=age;
        this._gender=gender;
        this._d_type=dType;
        this._preferred_unit=pUnit;
    }

    public int get_d_type(){
        return this._d_type;
    }
    public void set_d_type(int dType){
        this._d_type=dType;
    }
    public String get_preferred_unit(){
        return this._preferred_unit;
    }
    public void set_preferred_unit(String pUnit){
        this._preferred_unit=pUnit;
    }
    public int get_id()
    {
        return this._id;
    }

    public void set_id(int id)
    {
        this._id=id;
    }

    public String get_name()
    {
        return this._name;
    }

    public void set_name(String name)
    {
        this._name=name;
    }

    public String get_country()
    {
        return this._country;
    }

    public void set_country(String country)
    {
        this._country=country;
    }

    public String get_preferredLanguage()
    {
        return this._preferred_language;
    }
    public void set_preferredLanguage(String preferred_language)
    {
        this._preferred_language=preferred_language;
    }

    public int get_age()
    {
        return this._age;
    }
    public void set_age(int age)
    {
        this._age=age;
    }
    public String get_gender()
    {
        return this._gender;
    }
    public void set_gender(String gender)
    {
        this._gender=gender;
    }

    public String gender(int gender_id)
    {
        String[] enums={"Male","Female","Others"};
        try{
         return   enums[gender_id+1];
        }
        catch(ArrayIndexOutOfBoundsException e){
            return "";
        }
    }



}
