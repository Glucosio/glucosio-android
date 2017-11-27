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

package org.glucosio.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import org.glucosio.android.activity.A1cCalculatorActivity;
import org.glucosio.android.activity.HelloActivity;
import org.glucosio.android.analytics.Analytics;
import org.glucosio.android.analytics.GoogleAnalytics;
import org.glucosio.android.backup.Backup;
import org.glucosio.android.backup.GoogleDriveBackup;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.User;
import org.glucosio.android.presenter.A1CCalculatorPresenter;
import org.glucosio.android.presenter.HelloPresenter;
import org.glucosio.android.tools.LocaleHelper;
import org.glucosio.android.tools.Preferences;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class GlucosioApplication extends Application {

    private static GlucosioApplication sInstance;

    @Nullable
    private Analytics analytics;

    @Nullable
    private LocaleHelper localeHelper;

    @Nullable
    private Preferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initFont();
        initLanguage();
    }

    @VisibleForTesting
    protected void initFont() {
        //TODO: convert of using new introduced class Preferences
        // Get Dyslexia preference and adjust font
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDyslexicModeOn = sharedPref.getBoolean("pref_font_dyslexia", false);

        if (isDyslexicModeOn) {
            setFont("fonts/opendyslexic.otf");
        } else {
            setFont("fonts/lato.ttf");
        }
    }

    @VisibleForTesting
    protected void initLanguage() {
        User user = getDBHandler().getUser(1);
        if (user != null) {
            checkBadLocale(user);

            String languageTag = user.getPreferred_language();
            if (languageTag != null) {
                getLocaleHelper().updateLanguage(this, languageTag);
            }
        }
    }

    private void checkBadLocale(User user) {
        Preferences preferences = getPreferences();
        boolean cleanLocaleDone = preferences.isLocaleCleaned();

        if (!cleanLocaleDone) {
            User updatedUser = new User(user.getId(), user.getName(), user.getPreferred_language(),
                    user.getCountry(), user.getAge(), user.getGender(), user.getD_type(),
                    user.getPreferred_unit(), user.getPreferred_unit_a1c(),
                    user.getPreferred_unit_weight(), user.getPreferred_range(),
                    user.getCustom_range_min(), user.getCustom_range_max());
            updatedUser.setPreferred_language(null);
            //TODO: is it long operation? should we move it to separate thread?
            getDBHandler().updateUser(updatedUser);
            preferences.saveLocaleCleaned();
        }
    }

    private void setFont(String font) {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(font)
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    @NonNull
    public Backup getBackup() {
        return new GoogleDriveBackup();
    }

    @NonNull
    public Analytics getAnalytics() {
        if (analytics == null) {
            analytics = new GoogleAnalytics();
            analytics.init(this);
        }

        return analytics;
    }

    @NonNull
    public DatabaseHandler getDBHandler() {
        return new DatabaseHandler(getApplicationContext());
    }

    @NonNull
    public A1CCalculatorPresenter createA1cCalculatorPresenter(@NonNull final A1cCalculatorActivity activity) {
        return new A1CCalculatorPresenter(activity, getDBHandler());
    }

    @NonNull
    public LocaleHelper getLocaleHelper() {
        if (localeHelper == null) {
            localeHelper = new LocaleHelper();
        }
        return localeHelper;
    }

    @NonNull
    public Preferences getPreferences() {
        if (preferences == null) {
            preferences = new Preferences(this);
        }

        return preferences;
    }

    public static GlucosioApplication getInstance() {
        if (sInstance == null) {
            sInstance = new GlucosioApplication();
        }
        return sInstance;
    }

    @NonNull
    public HelloPresenter createHelloPresenter(@NonNull final HelloActivity activity) {
        return new HelloPresenter(activity, getDBHandler());
    }
}