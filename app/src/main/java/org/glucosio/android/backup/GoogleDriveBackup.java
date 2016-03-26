package org.glucosio.android.backup;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import java.lang.ref.WeakReference;

public class GoogleDriveBackup implements Backup, GoogleApiClient.OnConnectionFailedListener {
    @Nullable
    private GoogleApiClient googleApiClient;

    @Nullable
    private WeakReference<Activity> activityRef;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable task;

    @Override
    public void init(@NonNull final Activity activity) {
        this.activityRef = new WeakReference<>(activity);

        final Runnable googleBackupTask = new Runnable() {
            @Override
            public void run() {
                Log.v("test", "running background task");
                handler.postDelayed(this, 1000);
            }
        };

        googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        setupTask(googleBackupTask, 1000);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    public void start() {
        if (googleApiClient != null) {
            googleApiClient.connect();
        } else {
            throw new IllegalStateException("You should call init before start");
        }
    }

    @Override
    public void stop() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        } else {
            throw new IllegalStateException("You should call init before start");
        }
    }

    private void setupTask(Runnable backupTask, int delay) {
        handler.removeCallbacks(task);
        task = backupTask;
        handler.postDelayed(task, delay);
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult result) {
        Log.i("Connection Failed", "GoogleApiClient connection failed: " + result.toString());
        if (result.hasResolution() && activityRef != null && activityRef.get() != null) {
            Activity a = activityRef.get();
            // show the localized error dialog.
            try {
                result.startResolutionForResult(a, 1);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
            GoogleApiAvailability.getInstance().getErrorDialog(a, result.getErrorCode(), 0).show();
        } else
            Log.d("error", "cannot resolve connection issue");
    }
}
