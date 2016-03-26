package org.glucosio.android.invitations;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.appinvite.AppInviteInvitation;

public class GoogleInvitation implements Invitation {
    @Override
    public void invite(@NonNull final Activity activity, @NonNull final String title,
                       @NonNull final String invitationMessage, @NonNull final String actionMessage) {
        Intent intent = new AppInviteInvitation.IntentBuilder(title)
                .setMessage(invitationMessage)
                .setCallToActionText(actionMessage)
                .build();
        activity.startActivityForResult(intent, 0);
    }
}
