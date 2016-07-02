package org.glucosio.android.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    static final String LOCALE_CLEANED = "PREF_LOCALE_CLEANED";

    private final SharedPreferences sharedPreferences;

    public Preferences(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isLocaleCleaned() {
        return sharedPreferences.getBoolean(LOCALE_CLEANED, false);
    }

    public void saveLocaleCleaned() {
        sharedPreferences.edit().putBoolean(LOCALE_CLEANED, true).apply();
    }
}
