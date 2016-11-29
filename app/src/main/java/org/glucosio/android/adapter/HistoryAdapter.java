/*
 * Copyright (C) 2016 Glucosio Foundation
 *
 * This file is part of Glucosio.
 *
 * Glucosio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Glucosio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Glucosio.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package org.glucosio.android.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.glucosio.android.Constants;
import org.glucosio.android.R;
import org.glucosio.android.presenter.HistoryPresenter;
import org.glucosio.android.tools.GlucoseRanges;
import org.glucosio.android.tools.GlucosioConverter;
import org.glucosio.android.tools.NumberFormatUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final int metricId;
    private final Context mContext;
    private final HistoryPresenter presenter;

    private ArrayList<String> weightDataTime;
    private ArrayList<Long> weightIdArray;
    private List<Double> weightReadingArray;
    private ArrayList<String> ketoneDataTimeArray;
    private List<Double> ketoneReadingArray;
    private ArrayList<Long> ketoneIdArray;
    private ArrayList<String> pressureDateTimeArray;
    private List<Double> pressureMinArray;
    private List<Double> pressureMaxArray;
    private ArrayList<Long> pressureIdArray;
    private ArrayList<String> cholesterolDateTimeArray;
    private List<Double> cholesterolHDLArray;
    private List<Double> cholesterolLDLArray;
    private List<Double> cholesterolTotalArray;
    private ArrayList<Long> cholesterolIdArray;
    private ArrayList<String> hb1acDateTimeArray;
    private ArrayList<Double> hb1acReadingArray;
    private ArrayList<Long> hb1acIdArray;
    private List<Long> glucoseIdArray;
    private List<String> glucoseNotes;
    private List<Double> glucoseReadingArray;
    private List<String> glucoseDateTime;
    private List<String> glucoseReadingType;

    private final NumberFormat numberFormat = NumberFormatUtils.createDefaultNumberFormat();

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
                Collections.addAll(presenter.getGlucoseNotes());
                glucoseReadingArray = presenter.getGlucoseReading();
                glucoseDateTime = presenter.getGlucoseDateTime();
                glucoseReadingType = presenter.getGlucoseReadingType();
                glucoseIdArray = presenter.getGlucoseId();
                glucoseNotes = presenter.getGlucoseNotes();
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

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView readingTextView = holder.mView.findViewById(R.id.item_history_reading);
        TextView datetimeTextView = holder.mView.findViewById(R.id.item_history_time);
        TextView typeTextView = holder.mView.findViewById(R.id.item_history_type);
        TextView idTextView = holder.mView.findViewById(R.id.item_history_id);
        TextView notesTextView = holder.mView.findViewById(R.id.item_history_notes);

        switch (metricId) {
            // Glucose
            case 0:
                idTextView.setText(glucoseIdArray.get(position).toString());
                GlucoseRanges ranges = new GlucoseRanges(mContext);
                String color = ranges.colorFromReading(glucoseReadingArray.get(position));

                if (Constants.Units.MG_DL.equals(presenter.getUnitMeasuerement())) {
                    double glucoseReading = glucoseReadingArray.get(position);
                    String reading = numberFormat.format(glucoseReading);
                    readingTextView.setText(mContext.getString(R.string.mg_dL_value, reading));
                } else {
                    double mmol = GlucosioConverter.glucoseToMmolL(glucoseReadingArray.get(position));
                    String reading = numberFormat.format(mmol);
                    readingTextView.setText(mContext.getString(R.string.mmol_L_value, reading));
                }

                readingTextView.setTextColor(ranges.stringToColor(color));
                datetimeTextView.setText(presenter.convertDate(glucoseDateTime.get(position)));
                typeTextView.setText(glucoseReadingType.get(position));
                String notes = glucoseNotes.get(position);
                if (!notes.isEmpty()) {
                    notesTextView.setText(glucoseNotes.get(position));
                    notesTextView.setVisibility(View.VISIBLE);
                } else {
                    notesTextView.setText("");
                    notesTextView.setVisibility(View.GONE);
                }
                break;
            // A1C
            case 1:
                idTextView.setText(hb1acIdArray.get(position).toString());
                if ("percentage".equals(presenter.getA1cUnitMeasurement())) {
                    readingTextView.setText(hb1acReadingArray.get(position).toString() + " %");
                } else {
                    double ifcc = GlucosioConverter.a1cNgspToIfcc(hb1acReadingArray.get(position));
                    String reading = numberFormat.format(ifcc);
                    readingTextView.setText(mContext.getString(R.string.mmol_mol_value, reading));
                }
                datetimeTextView.setText(presenter.convertDate(hb1acDateTimeArray.get(position)));
                typeTextView.setText("");
                typeTextView.setVisibility(View.GONE);
                readingTextView.setTextColor(ContextCompat.getColor(mContext, R.color.glucosio_text_dark));
                break;
            // Cholesterol
            case 2:
                idTextView.setText(cholesterolIdArray.get(position).toString());
                String reading = cholesterolTotalArray.get(position).toString();
                readingTextView.setText(mContext.getString(R.string.mg_dL_value, reading));
                datetimeTextView.setText(presenter.convertDate(cholesterolDateTimeArray.get(position)));
                typeTextView.setText("LDL: " + cholesterolLDLArray.get(position) + " - " + "HDL: " + cholesterolHDLArray.get(position));
                readingTextView.setTextColor(ContextCompat.getColor(mContext, R.color.glucosio_text_dark));
                break;
            // Pressure
            case 3:
                idTextView.setText(pressureIdArray.get(position).toString());
                readingTextView.setText(pressureMaxArray.get(position).toString() + "/" + pressureMinArray.get(position).toString() + "  mm/Hg");
                datetimeTextView.setText(presenter.convertDate(pressureDateTimeArray.get(position)));
                typeTextView.setText("");
                typeTextView.setVisibility(View.GONE);
                readingTextView.setTextColor(ContextCompat.getColor(mContext, R.color.glucosio_text_dark));
                break;
            //Ketones
            case 4:
                idTextView.setText(ketoneIdArray.get(position).toString());
                readingTextView.setText(ketoneReadingArray.get(position).toString() + " mmol");
                datetimeTextView.setText(presenter.convertDate(ketoneDataTimeArray.get(position)));
                typeTextView.setText("");
                typeTextView.setVisibility(View.GONE);
                readingTextView.setTextColor(ContextCompat.getColor(mContext, R.color.glucosio_text_dark));
                break;
            // Weight
            case 5:
                idTextView.setText(weightIdArray.get(position).toString());

                //TODO: localise
                if ("kilograms".equals(presenter.getWeightUnitMeasurement())) {
                    readingTextView.setText(numberFormat.format(weightReadingArray.get(position)) + " kg");
                } else {
                    double lb = GlucosioConverter.kgToLb(weightReadingArray.get(position));
                    readingTextView.setText(numberFormat.format(lb) + " lbs");
                }

                datetimeTextView.setText(presenter.convertDate(weightDataTime.get(position)));
                typeTextView.setText("");
                typeTextView.setVisibility(View.GONE);
                readingTextView.setTextColor(ContextCompat.getColor(mContext, R.color.glucosio_text_dark));
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
}
