package org.glucosio.android.report;

import android.support.annotation.NonNull;

public interface CrashReporter {

    void log(@NonNull String crashLog);

    void report(@NonNull Throwable throwable);
}
