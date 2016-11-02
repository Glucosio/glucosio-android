package org.glucosio.android.view;

import android.net.Uri;

/**
 * Created by joaquimley on 08/09/16.
 */
public interface ExportView {

    void onExportStarted(int numberOfItemsToExport);

    void onNoItemsToExport();

    void onExportFinish(Uri fileUri);

    void onExportError();
}
