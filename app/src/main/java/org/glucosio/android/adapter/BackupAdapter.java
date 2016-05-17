package org.glucosio.android.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.glucosio.android.R;
import org.glucosio.android.object.GlucosioBackup;

import java.util.List;

public class BackupAdapter extends ArrayAdapter<GlucosioBackup> {


    public BackupAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public BackupAdapter(Context context, int resource, List<GlucosioBackup> items) {
        super(context, resource, items);
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

        if (p != null) {
            TextView modifiedTextView = (TextView) v.findViewById(R.id.item_history_time);
            TextView typeTextView = (TextView) v.findViewById(R.id.item_history_type);
            modifiedTextView.setText(p.getModifiedDate().toString());
            typeTextView.setText(p.getBackupSize()+"");

        }

        return v;
    }

}
