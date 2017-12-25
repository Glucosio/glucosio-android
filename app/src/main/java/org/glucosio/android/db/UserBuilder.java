package org.glucosio.android.db;

public class UserBuilder {
    private int id;
    private String name;
    private String preferredLanguage;
    private String country;
    private int age;
    private String gender;
    private int diabetesType;
    private String preferredUnit;
    private String a1cUnit;
    private String preferredWeightUnit;
    private String pRange;
    private double minRange;
    private double maxRange;

    public UserBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public UserBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder setPreferredLanguage(String language) {
        this.preferredLanguage = language;
        return this;
    }

    public UserBuilder setCountry(String country) {
        this.country = country;
        return this;
    }

    public UserBuilder setAge(int age) {
        this.age = age;
        return this;
    }

    public UserBuilder setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public UserBuilder setDiabetesType(int dType) {
        this.diabetesType = dType;
        return this;
    }

    public UserBuilder setPreferredUnit(String unit) {
        this.preferredUnit = unit;
        return this;
    }

    public UserBuilder setPreferredA1CUnit(String a1cUnit) {
        this.a1cUnit = a1cUnit;
        return this;
    }

    public UserBuilder setPreferredWeightUnit(String weightUnit) {
        this.preferredWeightUnit = weightUnit;
        return this;
    }

    public UserBuilder setPreferredRange(String pRange) {
        this.pRange = pRange;
        return this;
    }

    public UserBuilder setMinRange(double minRange) {
        this.minRange = minRange;
        return this;
    }

    public UserBuilder setMaxRange(double maxRange) {
        this.maxRange = maxRange;
        return this;
    }

    public User createUser() {
        return new User(id, name, preferredLanguage, country, age, gender, diabetesType, preferredUnit, a1cUnit,
                preferredWeightUnit, pRange, minRange, maxRange);
    }
}
