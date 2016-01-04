package org.glucosio.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.glucosio.android.R;
import org.glucosio.android.presenter.HistoryPresenter;
import org.glucosio.android.tools.GlucoseConverter;
import org.glucosio.android.tools.GlucoseRanges;

import java.util.Collections;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final int metricId;
    Context mContext;
    private HistoryPresenter presenter;
    private GlucoseConverter converter;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HistoryAdapter(Context context, HistoryPresenter presenter, int metricId) {
        this.mContext = context;
        this.presenter = presenter;
        this.metricId = metricId;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history_item, parent, false);

        converter = new GlucoseConverter();

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView readingTextView = (TextView) holder.mView.findViewById(R.id.item_history_reading);
        TextView datetimeTextView = (TextView) holder.mView.findViewById(R.id.item_history_time);
        TextView typeTextView = (TextView) holder.mView.findViewById(R.id.item_history_type);
        TextView idTextView = (TextView) holder.mView.findViewById(R.id.item_history_id);

        // If Glucose, color

        switch (metricId) {
            // Glucose
            case 0:
                // Reverse ListView order to display latest items first
                Collections.addAll(presenter.getGlucoseReading());
                Collections.addAll(presenter.getGlucoseDateTime());
                Collections.addAll(presenter.getGlucoseReadingType());
                Collections.addAll(presenter.getGlucoseId());

                idTextView.setText(presenter.getGlucoseId().get(position).toString());

                GlucoseRanges ranges = new GlucoseRanges(mContext);
                String color = ranges.colorFromReading(presenter.getGlucoseReading().get(position));

                if (presenter.getUnitMeasuerement().equals("mg/dL")) {
                    readingTextView.setText(presenter.getGlucoseReading().get(position).toString() + " mg/dL");
                } else {
                    readingTextView.setText(converter.toMmolL(Double.parseDouble(presenter.getGlucoseReading().get(position).toString())) + " mmol/L");
                }

                readingTextView.setTextColor(ranges.stringToColor(color));
                datetimeTextView.setText(presenter.convertDate(presenter.getGlucoseDateTime().get(position)));
                typeTextView.setText(presenter.getGlucoseReadingType().get(position));
                break;
            // HB1AC
            case 1:
                idTextView.setText(presenter.getHB1ACId().get(position).toString());
                readingTextView.setText(presenter.getGlucoseReading().get(position).toString() + " %");
                datetimeTextView.setText(presenter.convertDate(presenter.getHB1ACDateTime().get(position)));
                typeTextView.setText("");
                break;
            // Cholesterol
            case 2:
                idTextView.setText(presenter.getCholesterolId().get(position).toString());
                readingTextView.setText(presenter.getTotalCholesterolReading().get(position).toString() + " mg/dL");
                datetimeTextView.setText(presenter.convertDate(presenter.getHB1ACDateTime().get(position)));
                typeTextView.setText("LDL: "+ presenter.getLDLCholesterolReading() + " - " + "HDL: " + presenter.getHDLCholesterolReading());
                break;
            // Pressure
            case 3:
                idTextView.setText(presenter.getPressureId().get(position).toString());
                readingTextView.setText(presenter.getMinPressureReading().get(position).toString() + "/" + presenter.getMaxPressureReading().get(position).toString() + " mm/Hg");
                datetimeTextView.setText(presenter.convertDate(presenter.getPressureDateTime().get(position)));
                typeTextView.setText("");
                break;
            //Ketones
            case 4:
                idTextView.setText(presenter.getKetoneId().get(position).toString());
                readingTextView.setText(presenter.getKetoneReading().get(position).toString() + " mmol");
                datetimeTextView.setText(presenter.convertDate(presenter.getKetoneDateTime().get(position)));
                typeTextView.setText("");
                break;
            // Weight
            case 5:
                idTextView.setText(presenter.getWeightId().get(position).toString());
                readingTextView.setText(presenter.getWeightReadings().get(position).toString() + " kg");
                datetimeTextView.setText(presenter.convertDate(presenter.getKetoneDateTime().get(position)));
                typeTextView.setText("");
                break;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        switch (metricId) {
            // Glucose
            case 0:
                Toast.makeText(mContext, metricId + presenter.getGlucoseReadingsNumber() + "", Toast.LENGTH_SHORT).show();
                return presenter.getGlucoseReadingsNumber();
            // HB1AC
            case 1:
                return presenter.getHB1ACReadingsNumber();
            // Cholesterol
            case 2:
                return presenter.getCholesterolReadingsNumber();
            // Pressure
            case 3:
                return presenter.getPressureReadingsNumber();
            //Ketones
            case 4:
                return presenter.getKetoneReadingsNumber();
            // Weight
            case 5:
                return presenter.getWeightReadingsNumber();
            default:
                return 0;
        }
    }
}