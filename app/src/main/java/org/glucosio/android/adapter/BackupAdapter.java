package org.glucosio.android.adapter;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.drive.DriveId;

import org.glucosio.android.R;
import org.glucosio.android.activity.BackupActivity;
import org.glucosio.android.object.GlucosioBackup;
import org.w3c.dom.Text;

import java.util.List;

public class BackupAdapter extends ArrayAdapter<GlucosioBackup> {

    private Context context;


    public BackupAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public BackupAdapter(Context context, int resource, List<GlucosioBackup> items) {
        super(context, resource, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.activity_backup_drive_restore_item, null);
        }

        GlucosioBackup p = getItem(position);
        final DriveId driveId= p.getDriveId();
        final String modified = p.getModifiedDate().toString();
        final String size = humanReadableByteCount(p.getBackupSize(), true);

        if (p != null) {
            TextView modifiedTextView = (TextView) v.findViewById(R.id.item_history_time);
            TextView typeTextView = (TextView) v.findViewById(R.id.item_history_type);
            modifiedTextView.setText(modified);
            typeTextView.setText(size);
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_backup_restore);
                TextView createdTextView = (TextView) dialog.findViewById(R.id.dialog_backup_restore_created);
                TextView sizeTextView = (TextView) dialog.findViewById(R.id.dialog_backup_restore_size);
                Button restoreButton = (Button) dialog.findViewById(R.id.dialog_backup_restore_button_restore);
                Button cancelButton = (Button) dialog.findViewById(R.id.dialog_backup_restore_button_cancel);

                createdTextView.setText(modified);
                sizeTextView.setText(size);

                restoreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BackupActivity)context).downloadFromDrive(driveId.asDriveFile());
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        return v;
    }

    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
