/*
 * Copyright (C) 2016 Glucosio Foundation
 *
 * This file is part of Glucosio.
 *
 * Glucosio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Glucosio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Glucosio.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package org.glucosio.android.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

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
    @Required
    private String preferred_unit_a1c;
    @Required
    private String preferred_unit_weight;
    private String preferred_range;
    private int custom_range_min;
    private int custom_range_max;

    public User() {

    }

    public User(int id, String name, String preferred_language, String country, int age, String gender, int dType, String pUnit, String a1cUnit, String weightUnit, String pRange, int minRange, int maxRange) {
        this.id = id;
        this.name = name;
        this.preferred_language = preferred_language;
        this.country = country;
        this.age = age;
        this.gender = gender;
        this.d_type = dType;
        this.preferred_unit = pUnit;
        this.preferred_unit_a1c = a1cUnit;
        this.preferred_unit_weight = weightUnit;
        this.preferred_range = pRange;
        this.custom_range_max = maxRange;
        this.custom_range_min = minRange;
    }

    public int getD_type() {
        return this.d_type;
    }

    public void setD_type(int dType) {
        this.d_type = dType;
    }

    public String getPreferred_unit() {
        return this.preferred_unit;
    }

    public void setPreferred_unit(String pUnit) {
        this.preferred_unit = pUnit;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPreferred_language() {
        return this.preferred_language;
    }

    public void setPreferred_language(String preferred_language) {
        this.preferred_language = preferred_language;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getPreferred_unit_a1c() {
        return preferred_unit_a1c;
    }

    public void setPreferred_unit_a1c(String preferred_unit_a1c) {
        this.preferred_unit_a1c = preferred_unit_a1c;
    }

    public String getPreferred_unit_weight() {
        return preferred_unit_weight;
    }

    public void setPreferred_unit_weight(String preferred_unit_weight) {
        this.preferred_unit_weight = preferred_unit_weight;
    }
}
