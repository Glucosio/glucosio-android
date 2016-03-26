package org.glucosio.android.analytics;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.glucosio.android.BuildConfig;

public class GoogleAnalytics implements Analytics {
    private Tracker mTracker;

    @Override
    public void init(@NonNull final Context context) {
        com.google.android.gms.analytics.GoogleAnalytics analytics =
                com.google.android.gms.analytics.GoogleAnalytics.getInstance(context);
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        mTracker = analytics.newTracker(BuildConfig.GOOGLE_ANALYTICS_TRACKER);
        mTracker.enableAdvertisingIdCollection(true);

        if (BuildConfig.DEBUG) {
            com.google.android.gms.analytics.GoogleAnalytics.getInstance(context).setAppOptOut(true);
            Log.i("Glucosio", "DEBUG BUILD: ANALYTICS IS DISABLED");
        }
    }

    @Override
    public void reportScreen(@NonNull String name) {
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void reportAction(@NonNull String category, @NonNull String name) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(name)
                .build());
    }
}
