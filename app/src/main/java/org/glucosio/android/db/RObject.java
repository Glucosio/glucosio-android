package org.glucosio.android.db;

/**
 * I can not leave it as Object and I can not name it as RealmObject
 */
public enum RObject {
    GLUCOSE("GlucoseReading"),
    HB_1_AC("HB_1_ACReading"),
    WEIGHT("WeightReading"),
    PRESSURE("PressureReading"),
    KETONE("KetoneReading"),
    CHOLESTEROL("CholesterolReading"),
    USER("User"),
    REMINDER("Reminder");

    private String key;

    RObject(String key) {
        this.key = key;
    }

    String key() {
        return key;
    }
}
