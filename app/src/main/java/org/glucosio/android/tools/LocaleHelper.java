package org.glucosio.android.tools;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LocaleHelper {
    private Locale getLocale(String languageTag) {
        String[] values = languageTag.split("-");
        switch (values.length) {
            case 3:
                return new Locale(values[0], values[1], values[2]);
            case 2:
                return new Locale(values[0], values[1]);
            default:
                return new Locale(values[0]);
        }
    }

    public String getDisplayLanguage(String language) {
        String languageTag = language.replace("_", "-");
        Locale locale = getLocale(languageTag);
        return locale.getDisplayLanguage(locale);
    }

    public void updateLanguage(@NonNull final Context context, @NonNull final String language) {
        Locale locale = getLocale(language);

        Resources res = context.getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);

        Locale.setDefault(locale);
    }

}
