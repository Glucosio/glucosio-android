package org.glucosio.android.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Users")
public class User extends Model {

    @Column(name = "name")
    String _name;

    @Column(name = "preferred_language")
    String _preferred_language;

    @Column(name = "country")
    String _country;

    @Column(name = "age")
    int _age;

    @Column(name = "gender")
    String _gender;

    @Column(name = "d_type")
    int _d_type; //diabetes type

    @Column(name = "preferred_unit")
    String _preferred_unit; // preferred unit

    public User() {
        super();
    }

    public User(int id, String name,String preferred_language, String country, int age, String gender,int dType, String pUnit)
    {
        this._name=name;
        this._preferred_language=preferred_language;
        this._country=country;
        this._age=age;
        this._gender=gender;
        this._d_type=dType;
        this._preferred_unit=pUnit;
    }

    public static User getUser(int id) {
        return new Select()
                .from(User.class)
                .where("id = ?", id)
                .orderBy("RANDOM()")
                .executeSingle();
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
}
