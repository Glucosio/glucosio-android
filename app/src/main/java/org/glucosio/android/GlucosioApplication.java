package org.glucosio.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.tenmiles.helpstack.HSHelpStack;
import com.tenmiles.helpstack.gears.HSEmailGear;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class GlucosioApplication extends Application {

    private Tracker mTracker;
    HSHelpStack helpStack;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(BuildConfig.GOOGLE_ANALYTICS_TRACKER);
            mTracker.enableAdvertisingIdCollection(true);

            if (BuildConfig.DEBUG) {
                GoogleAnalytics.getInstance(this).setAppOptOut(true);
                Log.i("Glucosio", "DEBUG BUILD: ANALYTICS IS DISABLED");
            }
        }
        return mTracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Get Dyslexia preference and adjust font
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDyslexicModeOn = sharedPref.getBoolean("pref_font_dyslexia", false);

        if (isDyslexicModeOn) {
            setFont("fonts/opendyslexic.otf");
        } else {
            setFont("fonts/lato.ttf");
        }

        helpStack = HSHelpStack.getInstance(this); // Get the HSHelpStack instance

        HSEmailGear emailGear = new HSEmailGear(
                "rotolopao@gmail.com",
                R.xml.articles);

        helpStack.setGear(emailGear); // Set the Gear
    }

    private void setFont(String font){
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(font)
                .setFontAttrId(R.attr.fontPath)
                .build());
    }
}