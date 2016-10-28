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
        if (languageTag == null) {
            return getDeviceLocale();
        } else {
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
        // TODO(raacker): deprecated. change to setLocale(). It needs to be set minSdk to 17. Configure project's minSdk first
        conf.locale = locale;
        res.updateConfiguration(conf, dm);

        Locale.setDefault(locale);
    }

    @NonNull
    public List<String> getLocalesWithTranslation(final Resources resources) {
        String[] languageList = BuildConfig.TRANSLATION_ARRAY;
        Set<String> availableLanguagesSet = new HashSet<>();

        // Glucosio support English as default
        availableLanguagesSet.add("en");

        String[] translatedLanguages = resources.getStringArray(R.array.available_languages);
        Set<String> translatedLanguageSet = new HashSet<>();

        Collections.addAll(translatedLanguageSet, translatedLanguages);

        for (String language : languageList) {
            if (translatedLanguageSet.contains(language))
                availableLanguagesSet.add(language);
        }

        List<String> availableLanguagesList = new ArrayList<>(availableLanguagesSet);
        Collections.sort(availableLanguagesList);
        return availableLanguagesList;
    }

    public Locale getDeviceLocale() {
        // TODO(raacker): deprecated. change to getLocale(). It needs to be set minSdk to 17. Configure project's minSdk first
        return Resources.getSystem().getConfiguration().locale;
    }
}
