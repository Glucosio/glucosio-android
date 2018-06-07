package org.glucosio.android.analytics;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import org.glucosio.android.BuildConfig;

public class GlucosioGoogleAnalytics implements Analytics {
    private Tracker mTracker;

    @Override
    public void init(@NonNull final Context context) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        mTracker = analytics.newTracker(BuildConfig.GOOGLE_ANALYTICS_TRACKER);
        mTracker.enableAdvertisingIdCollection(true);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enabled = sharedPref.getBoolean("pref_analytics_opt_in", false);

        if (BuildConfig.DEBUG) {
            GoogleAnalytics.getInstance(context).setAppOptOut(true);
            Log.i("Glucosio", "DEBUG BUILD: ANALYTICS IS DISABLED");
        } else {
            GoogleAnalytics.getInstance(context).setAppOptOut(!enabled);
        }
    }

    @Override
    public void reportScreen(@NonNull String name) {
        // No need to check flag since we set opt out and Google Analytics will ignore sending
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void reportAction(@NonNull String category, @NonNull String name) {
        // No need to check flag since we set opt out and Google Analytics will ignore sending
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(name)
                .build());
    }
}
