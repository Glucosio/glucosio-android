package org.glucosio.android.activity;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import org.glucosio.android.GlucosioApplication;
import org.glucosio.android.R;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.User;

public class BackupActivity extends PreferenceActivity implements GoogleApiClient.OnConnectionFailedListener {

    CheckBoxPreference googleBackup;
    GoogleBackup googleBackupDriver;
    GoogleApiClient mClient;
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.backup_preference);
        googleBackup = (CheckBoxPreference) findPreference("google_drive");
        final Runnable googleBackupThread = new Runnable() {
            @Override
            public void run() {
                Log.v("test", "runnig background task");
                GoogleBackup.handler.postDelayed(this, 1000);
            }
        };
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        googleBackupDriver = new GoogleBackup(googleBackupThread, 1000);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(this)
                .build();

        googleBackup.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if(o.toString().equals("true")){
                    Log.d("backupAct", "mClient connecting");
                    mClient.connect();
                } else {
                    Log.d("backupAcv", "mCLient disconnected");
                    mClient.disconnect();
                }
                return true;
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("Connection Failed", "GoogleApiClient connection failed: " + result.toString());
        if (result.hasResolution()) {
            // show the localized error dialog.
            try {
                result.startResolutionForResult(this, 1);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        } else
            Log.d("error", "cannot resolve connection issue");
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    mClient.connect();
                }
                break;
        }
    }
}

class GoogleBackup {
    static Handler handler = new Handler();
    Runnable task;

    GoogleBackup(Runnable task, long time){
        this.task = task;
        handler.removeCallbacks(task);
        handler.postDelayed(task, time);
    }
}


/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup);

        getFragmentManager().beginTransaction()
                .replace(R.id.backupPreferencesFrame, new MyPreferenceFragment()).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Backup");

    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.backup_preference);

        }
    }
}
*/