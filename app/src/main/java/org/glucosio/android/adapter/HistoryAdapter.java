package org.glucosio.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.glucosio.android.R;
import org.glucosio.android.presenter.HistoryPresenter;
import org.glucosio.android.tools.GlucoseConverter;
import org.glucosio.android.tools.GlucoseRanges;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final int metricId;
    private ArrayList<String> weightDataTime;
    private ArrayList<Long> weightIdArray;
    private ArrayList<Integer> weightReadingArray;
    private ArrayList<String> ketoneDataTimeArray;
    private ArrayList<Double> ketoneReadingArray;
    private ArrayList<Long> ketoneIdArray;
    private ArrayList<String> pressureDateTimeArray;
    private ArrayList<Integer> pressureMinArray;
    private ArrayList<Integer> pressureMaxArray;
    private ArrayList<Long> pressureIdArray;
    private ArrayList<String> cholesterolDateTimeArray;
    private ArrayList<Integer> cholesterolHDLArray;
    private ArrayList<Integer> cholesterolLDLArray;
    private ArrayList<Integer> cholesterolTotalArray;
    private ArrayList<Long> cholesterolIdArray;
    private ArrayList<String> hb1acDateTimeArray;
    private ArrayList<Integer> hb1acReadingArray;
    private ArrayList<Long> hb1acIdArray;
    Context mContext;
    private HistoryPresenter presenter;
    private GlucoseConverter converter;
    private ArrayList<Long> glucoseIdArray;
    private ArrayList<Integer> glucoseReadingArray;
    private ArrayList<String> glucoseDateTime;
    private ArrayList<String> glucoseReadingType;

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

        switch (metricId) {
            // Glucose
            case 0:
                // Reverse ListView order to display latest items first
                Collections.addAll(presenter.getGlucoseReading());
                Collections.addAll(presenter.getGlucoseDateTime());
                Collections.addAll(presenter.getGlucoseReadingType());
                Collections.addAll(presenter.getGlucoseId());
                glucoseReadingArray = presenter.getGlucoseReading();
                glucoseDateTime = presenter.getGlucoseDateTime();
                glucoseReadingType = presenter.getGlucoseReadingType();
                glucoseIdArray = presenter.getGlucoseId();
                break;
            // HB1AC
            case 1:
                hb1acIdArray = presenter.getHB1ACId();
                hb1acReadingArray = presenter.getHB1ACReading();
                hb1acDateTimeArray = presenter.getHB1ACDateTime();
                break;
            // Cholesterol
            case 2:
                cholesterolIdArray = presenter.getCholesterolId();
                cholesterolTotalArray = presenter.getTotalCholesterolReading();
                cholesterolLDLArray = presenter.getLDLCholesterolReading();
                cholesterolHDLArray = presenter.getHDLCholesterolReading();
                cholesterolDateTimeArray = presenter.getCholesterolDateTime();
                break;
            // Pressure
            case 3:
                pressureIdArray = presenter.getPressureId();
                pressureMaxArray = presenter.getMaxPressureReading();
                pressureMinArray = presenter.getMinPressureReading();
                pressureDateTimeArray = presenter.getPressureDateTime();
                break;
            //Ketones
            case 4:
                ketoneIdArray = presenter.getKetoneId();
                ketoneReadingArray = presenter.getKetoneReading();
                ketoneDataTimeArray = presenter.getKetoneDateTime();
                break;
            // Weight
            case 5:
                weightIdArray = presenter.getWeightId();
                weightReadingArray = presenter.getWeightReadings();
                weightDataTime = presenter.getWeightDateTime();
                break;
        }
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

        switch (metricId) {
            // Glucose
            case 0:
                idTextView.setText(glucoseIdArray.get(position).toString());
                GlucoseRanges ranges = new GlucoseRanges(mContext);
                String color = ranges.colorFromReading(glucoseReadingArray.get(position));

                if (presenter.getUnitMeasuerement().equals("mg/dL")) {
                    readingTextView.setText(glucoseReadingArray.get(position).toString() + " mg/dL");
                } else {
                    readingTextView.setText(converter.glucoseToMmolL(Double.parseDouble(glucoseReadingArray.get(position).toString())) + " mmol/L");
                }

                readingTextView.setTextColor(ranges.stringToColor(color));
                datetimeTextView.setText(presenter.convertDate(glucoseDateTime.get(position)));
                typeTextView.setText(glucoseReadingType.get(position));
                break;
            // HB1AC
            case 1:
                idTextView.setText(hb1acIdArray.get(position).toString());
                readingTextView.setText(hb1acReadingArray.get(position).toString() + " %");
                datetimeTextView.setText(presenter.convertDate(hb1acDateTimeArray.get(position)));
                typeTextView.setText("");
                typeTextView.setVisibility(View.GONE);
                readingTextView.setTextColor(mContext.getResources().getColor(R.color.glucosio_text_dark));
                break;
            // Cholesterol
            case 2:
                idTextView.setText(cholesterolIdArray.get(position).toString());
                readingTextView.setText(cholesterolTotalArray.get(position).toString() + " mg/dL");
                datetimeTextView.setText(presenter.convertDate(cholesterolDateTimeArray.get(position)));
                typeTextView.setText("LDL: "+ cholesterolLDLArray.get(position) + " - " + "HDL: " + cholesterolHDLArray.get(position));
                readingTextView.setTextColor(mContext.getResources().getColor(R.color.glucosio_text_dark));
                break;
            // Pressure
            case 3:
                idTextView.setText(pressureIdArray.get(position).toString());
                readingTextView.setText(pressureMaxArray.get(position).toString() + "/" + pressureMinArray.get(position).toString() + "  mm/Hg");
                datetimeTextView.setText(presenter.convertDate(pressureDateTimeArray.get(position)));
                typeTextView.setText("");
                typeTextView.setVisibility(View.GONE);
                readingTextView.setTextColor(mContext.getResources().getColor(R.color.glucosio_text_dark));
                break;
            //Ketones
            case 4:
                idTextView.setText(ketoneIdArray.get(position).toString());
                readingTextView.setText(ketoneReadingArray.get(position).toString() + " mmol");
                datetimeTextView.setText(presenter.convertDate(ketoneDataTimeArray.get(position)));
                typeTextView.setText("");
                typeTextView.setVisibility(View.GONE);
                readingTextView.setTextColor(mContext.getResources().getColor(R.color.glucosio_text_dark));
                break;
            // Weight
            case 5:
                idTextView.setText(weightIdArray.get(position).toString());
                readingTextView.setText(weightReadingArray.get(position).toString() + " kg");
                datetimeTextView.setText(presenter.convertDate(weightDataTime.get(position)));
                typeTextView.setText("");
                typeTextView.setVisibility(View.GONE);
                readingTextView.setTextColor(mContext.getResources().getColor(R.color.glucosio_text_dark));
                break;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        switch (metricId) {
            // Glucose
            case 0:
                return glucoseIdArray.size();
            // HB1AC
            case 1:
                return hb1acIdArray.size();
            // Cholesterol
            case 2:
                return cholesterolIdArray.size();
            // Pressure
            case 3:
                return pressureIdArray.size();
            //Ketones
            case 4:
                return ketoneIdArray.size();
            // Weight
            case 5:
                return weightIdArray.size();
            default:
                return 0;
        }
    }
}