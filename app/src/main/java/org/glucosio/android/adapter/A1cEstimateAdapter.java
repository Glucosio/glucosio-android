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

import java.text.NumberFormat;
import java.util.List;

public class A1cEstimateAdapter extends ArrayAdapter<A1cEstimate> {
    private DatabaseHandler db;
    private NumberFormat numberFormat = NumberFormat.getNumberInstance();

    public A1cEstimateAdapter(Context context, int resource, List<A1cEstimate> items) {
        super(context, resource, items);
        db = new DatabaseHandler(context);
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(3);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.dialog_a1c_item, parent, false);
        }

        A1cEstimate p = getItem(position);

        if (p != null) {
            TextView value = v.findViewById(R.id.dialog_a1c_item_value);
            TextView month = v.findViewById(R.id.dialog_a1c_item_month);
            TextView glucoseAverage = v.findViewById(R.id.dialog_a1c_item_glucose_value);

            if (value != null) {
                if ("percentage".equals(db.getUser(1).getPreferred_unit_a1c())) {
                    String stringValue = p.getValue() + " %";
                    value.setText(stringValue);
                } else {
                    String stringValue = GlucosioConverter.a1cNgspToIfcc(p.getValue()) + " mmol/mol";
                    value.setText(stringValue);
                }
            }

            if (month != null) {
                month.setText(p.getMonth());
            }

            if (glucoseAverage != null) {
                if ("mg/dL".equals(db.getUser(1).getPreferred_unit())) {
                    glucoseAverage.setText(getContext().getString(R.string.mg_dL_value, p.getGlucoseAverage()));
                } else {
                    double mmol = GlucosioConverter.glucoseToMgDl(Double.parseDouble(p.getGlucoseAverage()));
                    String reading = numberFormat.format(mmol);
                    glucoseAverage.setText(getContext().getString(R.string.mmol_L_value, reading));
                }
            }
        }

        return v;
    }

}
