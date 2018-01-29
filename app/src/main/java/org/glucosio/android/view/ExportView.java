package org.glucosio.android.view;

import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by joaquimley on 08/09/16.
 */
public interface ExportView {

    void onExportStarted(int numberOfItemsToExport);

    void onNoItemsToExport();

    void onExportFinish(@NonNull Uri fileUri);

    void onExportError();

    void requestStoragePermission();
}
