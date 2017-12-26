package org.glucosio.android.db;

public enum RealmField {
    ID("id"),
    CREATED("created"),
    READING("reading"),
    MIN_READING("minReading"),
    MAX_READING("maxReading"),
    TOTAL_READING("totalReading"),
    LDL_READING("LDLReading"),
    HDL_READING("HDLReading"),
    CUSTOM_RANGE_MIN("custom_range_min"),
    CUSTOM_RANGE_MAX("custom_range_max"),
    PREFERRED_UNIT_A1C("preferred_unit_a1c"),
    PREFERRED_UNIT_WEIGHT("preferred_unit_weight"),
    METRIC("metric"),
    ALARM_TIME("alarmTime"),
    ACTIVE("active"),
    ONE_TIME("oneTime"),
    LABEL("label");

    private String key;

    RealmField(String key) {
        this.key = key;
    }

    String key() {
        return key;
    }
}
