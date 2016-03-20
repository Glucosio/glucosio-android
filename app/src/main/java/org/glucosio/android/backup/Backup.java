package org.glucosio.android.backup;

import android.app.Activity;
import android.support.annotation.NonNull;

public interface Backup {
    void init(@NonNull final Activity activity);

    void start();

    void stop();
}
