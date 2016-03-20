package org.glucosio.android;

import org.glucosio.android.backup.Backup;
import org.glucosio.android.backup.GoogleDriveBackup;

public class GoogleGlucosioApplication extends GlucosioApplication {
    @Override
    public Backup getBackup() {
        return new GoogleDriveBackup();
    }
}
