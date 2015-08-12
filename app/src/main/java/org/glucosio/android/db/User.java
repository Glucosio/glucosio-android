package org.glucosio.android.db;

public class User {


    int _id;
    String _name;
    String _preferred_language;
    String _country;
    int _age;
    String _gender;

    public User()
    {

    }

    public User(int id, String name,String preferred_language, String country, int age,String gender)
    {
        this._id=id;
        this._name=name;
        this._preferred_language=preferred_language;
        this._country=country;
        this._age=age;
        this._gender=gender;
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
    public String is_gender()
    {
        return this._gender;
    }
    public void set_gender(String gender)
    {
        this._gender=gender;
    }


}
