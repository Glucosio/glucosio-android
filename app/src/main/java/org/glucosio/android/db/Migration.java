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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

import java.util.Date;

/************************************************
 Current Database Schema

 class User
 @PrimaryKey int id;
 String name;
 String preferred_language;
 String country;
 int age;
 String gender;
 int d_type;
 String preferred_unit;
 String preferred_unit_a1c;
 String preferred_unit_weight;
 String preferred_range;
 double custom_range_min;
 double custom_range_max;

 class Reminder
 @PrimaryKey long id;
 Date alarmTime;
 boolean oneTime;
 String metric;

 class CholesterolReading
 @PrimaryKey long id;
 double totalReading;
 double LDLReading;
 double HDLReading;
 Date created;

 class GlucoseReading
 @PrimaryKey long id;
 double reading;
 String reading_type;
 String notes;
 int user_id;
 Date created;

 class KetoneReading
 @PrimaryKey long id;
 double reading;
 Date created;

 class PressureReading
 @PrimaryKey long id;
 double minReading;
 double maxReading;
 Date created;

 class WeightReading
 @PrimaryKey long id;
 double reading;
 Date created;

 class HB1ACReading
 @PrimaryKey long id;
 double reading;
 Date created;
 ************************************************/

class Migration implements RealmMigration {

    @Override
    public void migrate(@NonNull DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {
            createInitialSchema(schema);
            oldVersion++;
        }

        if (oldVersion == 1) {
            // Change HB1AC reading from int to double
            changeHB1ACReadingsToDouble(schema);

            oldVersion++;
        }

        if (oldVersion == 2) {
            // Add 2 new fields in User:
            // String preferred_unit_a1c
            // String preferred_unit_weight
            // and populate them with default values (% and kilograms)
            addWeightAndA1CUnitsToUser(schema);

            oldVersion++;
        }

        if (oldVersion == 3) {
            // Add Reminders
            addReminders(schema);

            oldVersion++;
        }

        if (oldVersion == 4) {
            migrateAllReadingsToDouble(schema);

            oldVersion++;
        }
    }

    private void migrateAllReadingsToDouble(RealmSchema schema) {
        // Change Weight reading from int to double
        RealmObjectSchema objectSchema = schema.get(RObject.WEIGHT.key());
        safeMigrationIntToDouble(objectSchema, RealmField.READING.key());

        // Change Pressure max and min readings from int to double
        objectSchema = schema.get(RObject.PRESSURE.key());
        safeMigrationIntToDouble(objectSchema, RealmField.MIN_READING.key());
        safeMigrationIntToDouble(objectSchema, RealmField.MAX_READING.key());

        // Change Glucose reading from int to double
        objectSchema = schema.get(RObject.GLUCOSE.key());
        safeMigrationIntToDouble(objectSchema, RealmField.READING.key());

        // Change Cholesterol total, ldl and hdl readings from int to double
        objectSchema = schema.get(RObject.CHOLESTEROL.key());
        safeMigrationIntToDouble(objectSchema, RealmField.TOTAL_READING.key());
        safeMigrationIntToDouble(objectSchema, RealmField.LDL_READING.key());
        safeMigrationIntToDouble(objectSchema, RealmField.HDL_READING.key());

        // Change User custom range min and max from int to double
        objectSchema = schema.get(RObject.USER.key());
        safeMigrationIntToDouble(objectSchema, RealmField.CUSTOM_RANGE_MIN.key());
        safeMigrationIntToDouble(objectSchema, RealmField.CUSTOM_RANGE_MAX.key());
    }

    private void addReminders(RealmSchema schema) {
        schema.create(RObject.REMINDER.key())
                .addField(RealmField.ID.key(), Long.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                .addField(RealmField.METRIC.key(), String.class)
                .addField(RealmField.ALARM_TIME.key(), Date.class)
                .addField(RealmField.ACTIVE.key(), Boolean.class, FieldAttribute.REQUIRED)
                .addField(RealmField.ONE_TIME.key(), Boolean.class, FieldAttribute.REQUIRED)
                .addField(RealmField.LABEL.key(), String.class);
    }

    private void addWeightAndA1CUnitsToUser(RealmSchema schema) {
        schema.get(RObject.USER.key())
                .addField(RealmField.PREFERRED_UNIT_A1C.key(), String.class, FieldAttribute.REQUIRED)
                .addField(RealmField.PREFERRED_UNIT_WEIGHT.key(), String.class, FieldAttribute.REQUIRED)
                .transform(new RealmObjectSchema.Function() {
                    @Override
                    public void apply(DynamicRealmObject obj) {
                        obj.set(RealmField.PREFERRED_UNIT_A1C.key(), "percentage");
                        obj.set(RealmField.PREFERRED_UNIT_WEIGHT.key(), "kilograms");
                    }
                });
    }

    private void changeHB1ACReadingsToDouble(RealmSchema schema) {
        RealmObjectSchema objectSchema = schema.get(RObject.HB_1_AC.key());
        safeMigrationIntToDouble(objectSchema, RealmField.READING.key());
    }

    private void createInitialSchema(RealmSchema schema) {
        schema.create(RObject.WEIGHT.key())
                .addField(RealmField.ID.key(), Long.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                .addField(RealmField.CREATED.key(), Date.class)
                .addField(RealmField.READING.key(), Integer.class, FieldAttribute.REQUIRED);

        schema.create(RObject.PRESSURE.key())
                .addField(RealmField.ID.key(), Long.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                .addField(RealmField.CREATED.key(), Date.class)
                .addField(RealmField.MIN_READING.key(), Integer.class, FieldAttribute.REQUIRED)
                .addField(RealmField.MAX_READING.key(), Integer.class, FieldAttribute.REQUIRED);

        schema.create(RObject.KETONE.key())
                .addField(RealmField.ID.key(), Long.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                .addField(RealmField.CREATED.key(), Date.class)
                .addField(RealmField.READING.key(), Double.class, FieldAttribute.REQUIRED);

        schema.create(RObject.HB_1_AC.key())
                .addField(RealmField.ID.key(), Long.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                .addField(RealmField.CREATED.key(), Date.class)
                .addField(RealmField.READING.key(), Integer.class, FieldAttribute.REQUIRED);

        schema.create(RObject.CHOLESTEROL.key())
                .addField(RealmField.ID.key(), Long.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                .addField(RealmField.CREATED.key(), Date.class)
                .addField(RealmField.TOTAL_READING.key(), Integer.class, FieldAttribute.REQUIRED)
                .addField(RealmField.LDL_READING.key(), Integer.class, FieldAttribute.REQUIRED)
                .addField(RealmField.HDL_READING.key(), Integer.class, FieldAttribute.REQUIRED);
    }

    private void safeMigrationIntToDouble(@Nullable RealmObjectSchema objectSchema, @NonNull String columnName) {
        if (objectSchema != null) {
            migrateIntColumnToDouble(objectSchema, columnName);
        }
    }

    private void migrateIntColumnToDouble(@NonNull RealmObjectSchema objectSchema, @NonNull final String columnName) {
        final String tempColumnName = columnName + "_tmp";
        objectSchema
                .addField(tempColumnName, Double.class, FieldAttribute.REQUIRED)
                .transform(new RealmObjectSchema.Function() {
                    @Override
                    public void apply(@NonNull DynamicRealmObject obj) {
                        int oldType = obj.getInt(columnName);
                        obj.setDouble(tempColumnName, oldType);
                    }
                })
                .removeField(columnName)
                .renameField(tempColumnName, columnName);
    }
}
