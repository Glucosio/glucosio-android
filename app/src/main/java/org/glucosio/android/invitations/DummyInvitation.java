package org.glucosio.android.invitations;

import android.app.Activity;
import android.support.annotation.NonNull;

public class DummyInvitation implements Invitation {
    @Override
    public void invite(@NonNull Activity activity, @NonNull final String title,
                       @NonNull final String invitationMessage, @NonNull final String actionString) {

    }
}
