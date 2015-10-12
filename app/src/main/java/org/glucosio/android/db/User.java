package org.glucosio.android.db;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private String preferred_language;
    private String country;
    private int age;
    private String gender;
    private int d_type;
    private String preferred_unit;
    private String preferred_range;
    private int custom_range_min;
    private int custom_range_max;

    public User() {

    }

    public User(int id, String name,String preferred_language, String country, int age, String gender,int dType, String pUnit, String pRange, int minRange, int maxRange) {
        this.id=id;
        this.name=name;
        this.preferred_language=preferred_language;
        this.country=country;
        this.age=age;
        this.gender=gender;
        this.d_type=dType;
        this.preferred_unit=pUnit;
        this.preferred_range = pRange;
        this.custom_range_max = maxRange;
        this.custom_range_min = minRange;
    }

    public int getD_type(){
        return this.d_type;
    }
    public void setD_type(int dType){
        this.d_type=dType;
    }
    public String getPreferred_unit(){
        return this.preferred_unit;
    }
    public void setPreferred_unit(String pUnit){
        this.preferred_unit=pUnit;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public String getCountry()
    {
        return this.country;
    }

    public void setCountry(String country)
    {
        this.country=country;
    }

    public String getPreferred_language() {
        return this.preferred_language;
    }
    public void setPreferred_language(String preferred_language) {
        this.preferred_language=preferred_language;
    }

    public int getAge() {
        return this.age;
    }
    public void setAge(int age) {
        this.age=age;
    }
    public String getGender() {
        return this.gender;
    }
    public void setGender(String gender) {
        this.gender=gender;
    }
    public String getPreferred_range() {
        return preferred_range;
    }
    public void setPreferred_range(String preferred_range) {
        this.preferred_range = preferred_range;
    }
    public int getCustom_range_min() {
        return custom_range_min;
    }

    public void setCustom_range_min(int custom_range_min) {
        this.custom_range_min = custom_range_min;
    }

    public int getCustom_range_max() {
        return custom_range_max;
    }

    public void setCustom_range_max(int custom_range_max) {
        this.custom_range_max = custom_range_max;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
