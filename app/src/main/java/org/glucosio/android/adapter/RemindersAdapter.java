package org.glucosio.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.glucosio.android.R;
import org.glucosio.android.db.Reminder;

import java.util.List;

public class RemindersAdapter extends ArrayAdapter<Reminder> {
    private Context context;
    private List<Reminder> items;

    public RemindersAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public RemindersAdapter(Context context, int resource, List<Reminder> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.activity_reminder_item, parent, false);
        }


        return v;
    }
}
