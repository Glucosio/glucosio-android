package org.glucosio.android.invitations;

import android.app.Activity;
import android.support.annotation.NonNull;

public interface Invitation {
    void invite(@NonNull final Activity activity, @NonNull final String title,
                @NonNull final String invitationMessage, @NonNull final String actionString);
}
