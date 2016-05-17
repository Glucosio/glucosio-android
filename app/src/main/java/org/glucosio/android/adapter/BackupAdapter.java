package org.glucosio.android.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.glucosio.android.R;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.object.A1cEstimate;
import org.glucosio.android.tools.GlucosioConverter;

import java.util.List;

public class BackupAdapter extends ArrayAdapter<A1cEstimate> {

    DatabaseHandler db;

    public BackupAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public BackupAdapter(Context context, int resource, List<A1cEstimate> items) {
        super(context, resource, items);
        db = new DatabaseHandler(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.dialog_a1c_item, null);
        }

        A1cEstimate p = getItem(position);

        if (p != null) {
            TextView value = (TextView) v.findViewById(R.id.dialog_a1c_item_value);
            TextView month = (TextView) v.findViewById(R.id.dialog_a1c_item_month);
            TextView glucoseAverage = (TextView) v.findViewById(R.id.dialog_a1c_item_glucose_value);

            if (value != null) {
                if ("percentage".equals(db.getUser(1).getPreferred_unit_a1c())) {
                    String stringValue = p.getValue() + " %";
                    value.setText(stringValue);
                } else {
                    GlucosioConverter converter = new GlucosioConverter();
                    String stringValue = converter.a1cNgspToIfcc(p.getValue()) + " mmol/mol";
                    value.setText(stringValue);
                }
            }

            if (month != null) {
                month.setText(p.getMonth());
            }

            if (glucoseAverage != null) {
                if ("mg/dL".equals(db.getUser(1).getPreferred_unit())) {
                    glucoseAverage.setText(p.getGlucoseAverage() + " mg/dL");
                } else {
                    GlucosioConverter converter = new GlucosioConverter();
                    glucoseAverage.setText(converter.glucoseToMgDl(Double.parseDouble(p.getGlucoseAverage())) + " mmol/L");
                }
            }
        }

        return v;
    }

}
