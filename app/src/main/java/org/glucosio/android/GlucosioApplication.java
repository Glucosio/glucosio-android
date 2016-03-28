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

import com.instabug.library.IBGInvocationEvent;
import com.instabug.library.Instabug;

import org.glucosio.android.analytics.Analytics;
import org.glucosio.android.analytics.GoogleAnalytics;
import org.glucosio.android.backup.Backup;
import org.glucosio.android.backup.GoogleDriveBackup;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.invitations.GoogleInvitation;
import org.glucosio.android.invitations.Invitation;

import io.smooch.core.Smooch;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class GlucosioApplication extends Application {
    @Nullable
    private Analytics analytics;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Smooch
        Smooch.init(this, "dxopifgi0mylv4invg5lrb66r");

        // Initialize Instabug

        if (BuildConfig.DEBUG) {
            new Instabug.Builder(this, "b2226aa30fec24f6f4bed6ad68964e9b")
                    .setInvocationEvent(IBGInvocationEvent.IBGInvocationEventShake)
                    .build();
        } else {
            new Instabug.Builder(this, "820ee7db3118d03fd5f4249b5a73672e")
                    .setInvocationEvent(IBGInvocationEvent.IBGInvocationEventShake)
                    .build();
        }

        // Get Dyslexia preference and adjust font
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDyslexicModeOn = sharedPref.getBoolean("pref_font_dyslexia", false);

        if (isDyslexicModeOn) {
            setFont("fonts/opendyslexic.otf");
        } else {
            setFont("fonts/lato.ttf");
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
    public Invitation getInvitation() {
        return new GoogleInvitation();
    }

    @NonNull
    public DatabaseHandler getDBHandler() {
        return new DatabaseHandler(getApplicationContext());
    }
}