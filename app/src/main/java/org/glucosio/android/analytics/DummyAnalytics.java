package org.glucosio.android.analytics;

import android.content.Context;
import android.support.annotation.NonNull;

public class DummyAnalytics implements Analytics {
    @Override
    public void init(@NonNull Context context) {

    }

    @Override
    public void reportScreen(@NonNull String screenName) {

    }
}
