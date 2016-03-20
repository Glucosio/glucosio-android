package org.glucosio.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import org.glucosio.android.GlucosioApplication;
import org.glucosio.android.R;
import org.glucosio.android.backup.Backup;

public class BackupActivity extends AppCompatActivity {

    Backup backup;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_backup);

        getFragmentManager().beginTransaction()
                .replace(R.id.backupPreferencesFrame, new MyPreferenceFragment()).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_backup));

        backup = ((GlucosioApplication) getApplicationContext()).getBackup();
        backup.init(this);
    }

    public void connectClient() {
        backup.start();
    }

    public void disconnectClient() {
        backup.stop();
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    backup.start();
                }
                break;
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        CheckBoxPreference driveBackup;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.backup_preference);

            driveBackup = (CheckBoxPreference) findPreference("backup_google_drive_enabled");

            driveBackup.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (o.toString().equals("true")) {
                        Log.d("backupAct", "mClient connecting");
                        ((BackupActivity) getActivity()).connectClient();
                    } else {
                        Log.d("backupAcv", "mClient disconnected");
                        ((BackupActivity) getActivity()).disconnectClient();
                    }
                    return true;
                }
            });
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}