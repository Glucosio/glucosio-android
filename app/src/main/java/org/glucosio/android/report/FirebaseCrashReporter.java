package org.glucosio.android.report;

import android.support.annotation.NonNull;

import com.google.firebase.crash.FirebaseCrash;

public class FirebaseCrashReporter implements CrashReporter {

    @Override
    public void log(@NonNull String crashLog) {
        FirebaseCrash.log(crashLog);
    }

    @Override
    public void report(@NonNull Throwable throwable) {
        FirebaseCrash.report(throwable);
    }
}
