package org.glucosio.android.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface OverviewView {
    @NonNull
    String convertDate(@NonNull final String date);

    @NonNull
    String convertDateToMonth(@NonNull final String date);

    @NonNull
    String getString(@StringRes final int stringId);
}
