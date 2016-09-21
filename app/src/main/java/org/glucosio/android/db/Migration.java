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

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

class Migration implements RealmMigration {

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

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
         int custom_range_min;
         int custom_range_max;

         class Reminder
         @PrimaryKey long id;
         Date alarmTime;
         boolean oneTime;
         String metric;

         class CholesterolReading
         @PrimaryKey long id;
         int totalReading;
         int LDLReading;
         int HDLReading;
         Date created;

         class GlucoseReading
         @PrimaryKey long id;
         int reading;
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
         int minReading;
         int maxReading;
         Date created;

         class WeightReading
         @PrimaryKey long id;
         int reading;
         Date created;

         class HB1ACReading
         @PrimaryKey long id;
         double reading;
         Date created;
         ************************************************/

        if (oldVersion == 0) {
            schema.create("WeightReading")
                    .addField("id", Long.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                    .addField("created", Date.class)
                    .addField("reading", Integer.class, FieldAttribute.REQUIRED);

            schema.create("PressureReading")
                    .addField("id", Long.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                    .addField("created", Date.class)
                    .addField("minReading", Integer.class, FieldAttribute.REQUIRED)
                    .addField("maxReading", Integer.class, FieldAttribute.REQUIRED);

            schema.create("KetoneReading")
                    .addField("id", Long.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                    .addField("created", Date.class)
                    .addField("reading", Double.class, FieldAttribute.REQUIRED);

            schema.create("HB1ACReading")
                    .addField("id", Long.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                    .addField("created", Date.class)
                    .addField("reading", Integer.class, FieldAttribute.REQUIRED);

            schema.create("CholesterolReading")
                    .addField("id", Long.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                    .addField("created", Date.class)
                    .addField("totalReading", Integer.class, FieldAttribute.REQUIRED)
                    .addField("LDLReading", Integer.class, FieldAttribute.REQUIRED)
                    .addField("HDLReading", Integer.class, FieldAttribute.REQUIRED);
            oldVersion++;
        }

        if (oldVersion == 1) {
            // Change HB1AC reading from int to double
            schema.get("HB1ACReading")
                    .addField("reading_tmp", Double.class, FieldAttribute.REQUIRED)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            int oldType = obj.getInt("reading");
                            obj.setDouble("reading_tmp", Double.parseDouble(oldType + ""));
                        }
                    })
                    .removeField("reading")
                    .renameField("reading_tmp", "reading");
            oldVersion++;
        }

        if (oldVersion == 2) {
            // Add 2 new fields in User:
            // String preferred_unit_a1c
            // String preferred_unit_weight
            // and populate them with default values (% and kilograms)

            schema.get("User")
                    .addField("preferred_unit_a1c", String.class, FieldAttribute.REQUIRED)
                    .addField("preferred_unit_weight", String.class, FieldAttribute.REQUIRED)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.set("preferred_unit_a1c", "percentage");
                            obj.set("preferred_unit_weight", "kilograms");
                        }
                    });
            oldVersion++;
        }

        if (oldVersion == 3) {
            // Add Reminders
            schema.create("Reminder")
                    .addField("id", Long.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                    .addField("metric", String.class)
                    .addField("alarmTime", Date.class)
                    .addField("active", Boolean.class, FieldAttribute.REQUIRED)
                    .addField("oneTime", Boolean.class, FieldAttribute.REQUIRED)
                    .addField("label", String.class);
            oldVersion++;
        }
    }
}
