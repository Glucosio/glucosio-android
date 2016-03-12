package org.glucosio.android.db;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class Migration implements RealmMigration {

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        /************************************************
            Current Database Schema
             
            class User
                @PrimaryKey
                int id;
                String name;
                String preferred_language;
                String country;
                int age;
                String gender;
                int d_type;
                String preferred_unit;
                String preferred_range;
                int custom_range_min;
                int custom_range_max;
        
             class CholesterolReading
                 @PrimaryKey
                 long id;
        
                 int totalReading;
                 int LDLReading;
                 int HDLReading;
                 Date created;
        
             class GlucoseReading
                 @PrimaryKey
                 long id;
        
                 int reading;
                 String reading_type;
                 String notes;
                 int user_id;
                 Date created;
        
             class KetoneReading
                 @PrimaryKey
                 long id;
        
                 double reading;
                 Date created;
        
             class PressureReading
                 @PrimaryKey
                 long id;
        
                 int minReading;
                 int maxReading;
                 Date created;
        
             class WeightReading
                 @PrimaryKey
                 long id;
        
                 int reading;
                 Date created;
        
                 class HB1ACReading
                 @PrimaryKey
                 long id;
                 double reading; <-- Convert from int to double
                 Date created;
         ************************************************/

        if (oldVersion == 0) {
            RealmObjectSchema weightSchema = schema.create("WeightReading")
                    .addField("id", Long.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                    .addField("created", Date.class)
                    .addField("reading", Integer.class, FieldAttribute.REQUIRED);

            RealmObjectSchema pressureSchema = schema.create("PressureReading")
                    .addField("id", Long.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                    .addField("created", Date.class)
                    .addField("minReading", Integer.class, FieldAttribute.REQUIRED)
                    .addField("maxReading", Integer.class, FieldAttribute.REQUIRED);

            RealmObjectSchema ketoneSchema = schema.create("KetoneReading")
                    .addField("id", Long.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                    .addField("created", Date.class)
                    .addField("reading", Double.class, FieldAttribute.REQUIRED);

            RealmObjectSchema HB1ACSchema = schema.create("HB1ACReading")
                    .addField("id", Long.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                    .addField("created", Date.class)
                    .addField("reading", Integer.class, FieldAttribute.REQUIRED);

            RealmObjectSchema cholesterolSchema = schema.create("CholesterolReading")
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
                            obj.setDouble("reading_tmp", Double.parseDouble(oldType+""));
                        }
                    })
                    .removeField("reading")
                    .renameField("reading_tmp", "reading");
            oldVersion++;
        }
    }
}
