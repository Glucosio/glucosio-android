package org.glucosio.android.tools;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import org.glucosio.android.BuildConfig;
import org.glucosio.android.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
        return locale.getDisplayName(locale);
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

    @NonNull
    public List<String> getLocalesWithTranslation(final Resources resources) {
        String[] languages = BuildConfig.TRANSLATION_ARRAY;
        Set<String> availableLanguagesSet = new HashSet<>();
        // We always support english
        availableLanguagesSet.add("en");

        // Get english string to confront
        // I know, it's a weird workaround
        // Sorry :/
        String englishString = "Automatic backup";

        for (String localString : languages) {
            // For each locale, check if we have translations
            Configuration conf = resources.getConfiguration();
            Locale savedLocale = conf.locale;
            conf.locale = getLocale(localString);
            resources.updateConfiguration(conf, null);

            // Retrieve an example string from this locale
            String localizedString = resources.getString(R.string.activity_backup_drive_automatic);

            if (!englishString.equals(localizedString)) {
                // if english string is not the same of localized one
                // a translation is available
                availableLanguagesSet.add(localString);
            }

            // restore original locale
            conf.locale = savedLocale;
            resources.updateConfiguration(conf, null);
        }

        List<String> availableLanguagesList = new ArrayList<>(availableLanguagesSet);
        Collections.sort(availableLanguagesList);
        return availableLanguagesList;
    }
}
