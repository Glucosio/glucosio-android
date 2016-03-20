package org.glucosio.android.analytics;

import android.content.Context;
import android.support.annotation.NonNull;

public interface Analytics {
    void init(@NonNull final Context context);

    void reportScreen(@NonNull final String screenName);
}
