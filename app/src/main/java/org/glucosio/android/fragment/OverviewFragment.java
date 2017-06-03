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

package org.glucosio.android.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.glucosio.android.GlucosioApplication;
import org.glucosio.android.R;
import org.glucosio.android.adapter.A1cEstimateAdapter;
import org.glucosio.android.presenter.OverviewPresenter;
import org.glucosio.android.tools.FormatDateTime;
import org.glucosio.android.tools.GlucoseRanges;
import org.glucosio.android.tools.GlucosioConverter;
import org.glucosio.android.tools.TipsManager;
import org.glucosio.android.view.OverviewView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class OverviewFragment extends Fragment implements OverviewView {

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private ImageButton HB1ACMoreButton;
    private LineChart chart;
    private TextView lastReadingTextView;
    private TextView lastDateTextView;
    private TextView tipTextView;
    private TextView HB1ACTextView;
    private TextView HB1ACDateTextView;
    private Spinner graphSpinnerRange;
    private OverviewPresenter presenter;

    private CheckBox graphCheckboxGlucose;
    private CheckBox graphCheckboxKetones;
    private CheckBox graphCheckboxCholesterol;
    private CheckBox graphCheckboxA1c;
    private CheckBox graphCheckboxWeight;
    private CheckBox graphCheckboxPressure;
    private View mFragmentView;

    private List<String> xValues = new ArrayList<>();


    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    public static void disableTouchTheft(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        GlucosioApplication app = (GlucosioApplication) getActivity().getApplicationContext();
        presenter = new OverviewPresenter(this, app.getDBHandler());
        if (!presenter.isdbEmpty()) {
            presenter.loadDatabase(isNewGraphEnabled());
        }

        mFragmentView = inflater.inflate(R.layout.fragment_overview, container, false);

        chart = (LineChart) mFragmentView.findViewById(R.id.chart);
        disableTouchTheft(chart);
        Legend legend = chart.getLegend();

        lastReadingTextView = (TextView) mFragmentView.findViewById(R.id.item_history_reading);
        lastDateTextView = (TextView) mFragmentView.findViewById(R.id.fragment_overview_last_date);
        tipTextView = (TextView) mFragmentView.findViewById(R.id.random_tip_textview);
        graphSpinnerRange = (Spinner) mFragmentView.findViewById(R.id.chart_spinner_range);
        Spinner graphSpinnerMetric = (Spinner) mFragmentView.findViewById(R.id.chart_spinner_metrics);
        ImageButton graphExport = (ImageButton) mFragmentView.findViewById(R.id.fragment_overview_graph_export);
        HB1ACTextView = (TextView) mFragmentView.findViewById(R.id.fragment_overview_hb1ac);
        HB1ACDateTextView = (TextView) mFragmentView.findViewById(R.id.fragment_overview_hb1ac_date);
        HB1ACMoreButton = (ImageButton) mFragmentView.findViewById(R.id.fragment_overview_a1c_more);
        graphCheckboxGlucose = (CheckBox) mFragmentView.findViewById(R.id.fragment_overview_graph_glucose);
        graphCheckboxKetones = (CheckBox) mFragmentView.findViewById(R.id.fragment_overview_graph_ketones);
        graphCheckboxCholesterol = (CheckBox) mFragmentView.findViewById(R.id.fragment_overview_graph_cholesterol);
        graphCheckboxA1c = (CheckBox) mFragmentView.findViewById(R.id.fragment_overview_graph_a1c);
        graphCheckboxWeight = (CheckBox) mFragmentView.findViewById(R.id.fragment_overview_graph_weight);
        graphCheckboxPressure = (CheckBox) mFragmentView.findViewById(R.id.fragment_overview_graph_pressure);

        graphCheckboxGlucose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setData();
                graphCheckboxWeight.setChecked(false);
                graphCheckboxCholesterol.setChecked(false);
                graphCheckboxKetones.setChecked(false);
                graphCheckboxPressure.setChecked(false);
                graphCheckboxWeight.setChecked(false);
                graphCheckboxA1c.setChecked(false);
                graphCheckboxGlucose.setChecked(b);
            }
        });

        graphCheckboxA1c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setData();
                graphCheckboxGlucose.setChecked(false);
                graphCheckboxWeight.setChecked(false);
                graphCheckboxCholesterol.setChecked(false);
                graphCheckboxKetones.setChecked(false);
                graphCheckboxPressure.setChecked(false);
                graphCheckboxWeight.setChecked(false);
                graphSpinnerRange.setEnabled(!b);
                graphCheckboxA1c.setChecked(b);
            }
        });

        graphCheckboxKetones.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setData();
                graphCheckboxGlucose.setChecked(false);
                graphCheckboxWeight.setChecked(false);
                graphCheckboxCholesterol.setChecked(false);
                graphCheckboxPressure.setChecked(false);
                graphCheckboxWeight.setChecked(false);
                graphCheckboxA1c.setChecked(false);
                graphSpinnerRange.setEnabled(!b);
                graphCheckboxKetones.setChecked(b);
            }
        });

        graphCheckboxWeight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setData();
                graphCheckboxGlucose.setChecked(false);
                graphCheckboxWeight.setChecked(false);
                graphCheckboxCholesterol.setChecked(false);
                graphCheckboxKetones.setChecked(false);
                graphCheckboxPressure.setChecked(false);
                graphCheckboxA1c.setChecked(false);
                graphSpinnerRange.setEnabled(!b);
                graphCheckboxWeight.setChecked(b);
            }
        });

        graphCheckboxPressure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setData();
                graphCheckboxGlucose.setChecked(false);
                graphCheckboxWeight.setChecked(false);
                graphCheckboxCholesterol.setChecked(false);
                graphCheckboxKetones.setChecked(false);
                graphCheckboxWeight.setChecked(false);
                graphCheckboxA1c.setChecked(false);
                graphSpinnerRange.setEnabled(!b);
                graphCheckboxPressure.setChecked(b);
            }
        });

        graphCheckboxCholesterol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setData();
                graphCheckboxGlucose.setChecked(false);
                graphCheckboxWeight.setChecked(false);
                graphCheckboxKetones.setChecked(false);
                graphCheckboxPressure.setChecked(false);
                graphCheckboxWeight.setChecked(false);
                graphCheckboxA1c.setChecked(false);
                graphSpinnerRange.setEnabled(!b);
                graphCheckboxCholesterol.setChecked(b);
            }
        });

        HB1ACMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showA1cDialog();
            }
        });
        // Set array and adapter for graphSpinnerRange
        String[] selectorRangeArray = getActivity().getResources().getStringArray(R.array.fragment_overview_selector_range);
        String[] selectorMetricArray = getActivity().getResources().getStringArray(R.array.fragment_overview_selector_metric);
        ArrayAdapter<String> dataRangeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, selectorRangeArray);
        ArrayAdapter<String> dataMetricAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, selectorMetricArray);
        dataRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataMetricAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        graphSpinnerRange.setAdapter(dataRangeAdapter);
        graphSpinnerMetric.setAdapter(dataMetricAdapter);

        graphSpinnerRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!presenter.isdbEmpty()) {
                    setData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(getResources().getColor(R.color.glucosio_text_light));
        xAxis.setAvoidFirstLastClipping(true);

        int minGlucoseValue = presenter.getGlucoseMinValue();
        int maxGlucoseValue = presenter.getGlucoseMaxValue();

        LimitLine ll1;
        LimitLine ll2;

        if (("mg/dL").equals(presenter.getUnitMeasuerement())) {
            ll1 = new LimitLine(minGlucoseValue);
            ll2 = new LimitLine(maxGlucoseValue);
        } else {
            ll1 = new LimitLine((float) GlucosioConverter.glucoseToMmolL(maxGlucoseValue), getString(R.string.reading_high));
            ll2 = new LimitLine((float) GlucosioConverter.glucoseToMmolL(minGlucoseValue), getString(R.string.reading_low));
        }

        ll1.setLineWidth(0.8f);
        ll1.setLineColor(getResources().getColor(R.color.glucosio_reading_low));

        ll2.setLineWidth(0.8f);
        ll2.setLineColor(getResources().getColor(R.color.glucosio_reading_high));

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(getResources().getColor(R.color.glucosio_text_light));
        leftAxis.setStartAtZero(false);
        leftAxis.disableGridDashedLine();
        leftAxis.setDrawGridLines(false);
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setDrawLimitLinesBehindData(true);

        chart.getAxisRight().setEnabled(false);
        chart.setBackgroundColor(Color.parseColor("#FFFFFF"));
        chart.setGridBackgroundColor(Color.parseColor("#FFFFFF"));
        if (!presenter.isdbEmpty()) {
            setData();
        }
        legend.setEnabled(false);

        graphExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // If we don't have permission, ask the user

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                    Snackbar.make(mFragmentView, getString(R.string.fragment_overview_permission_storage), Snackbar.LENGTH_SHORT).show();
                } else {
                    // else save the image to gallery
                    exportGraphToGallery();
                }
            }
        });

        loadLastReading();
        loadHB1AC();
        loadRandomTip();

        return mFragmentView;
    }

    private void exportGraphToGallery() {
        long timestamp = System.currentTimeMillis() / 1000;
        boolean saved = chart.saveToGallery("glucosio_" + timestamp, 50);
        if (saved) {
            Snackbar.make(mFragmentView, R.string.fragment_overview_graph_export_true, Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(mFragmentView, R.string.fragment_overview_graph_export_false, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void setData() {
        LineData data = new LineData();
        if (graphCheckboxGlucose.isChecked()) {
            data = generateGlucoseData();
        }

        if (graphCheckboxA1c.isChecked()) {
            data = generateA1cData();
        }

        if (graphCheckboxKetones.isChecked()) {
            data = generateKetonesData();
        }

        if (graphCheckboxWeight.isChecked()) {
            data = generateWeightData();
        }

        if (graphCheckboxPressure.isChecked()) {
            data = generatePressureData();
        }

        if (graphCheckboxCholesterol.isChecked()) {
            data = generateCholesterolData();
        }


        if (data.getEntryCount()!=0) {
            chart.setData(data);
        } else {
            chart.setData(null);
        }
        chart.setPinchZoom(true);
        chart.setHardwareAccelerationEnabled(true);
        chart.setNoDataTextColor(getResources().getColor(R.color.glucosio_text));
        chart.animateY(1000, Easing.EasingOption.EaseOutCubic);
        chart.invalidate();
        chart.notifyDataSetChanged();
        chart.fitScreen();
        chart.setDescription(null);
        chart.setVisibleXRangeMaximum(20);
        chart.moveViewToX(data.getXMax());

        XAxis xAxis = chart.getXAxis();

        final LineData finalData = data;
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                // Dirty fix for a library bug. I have to report it online because 'value' returns old values even if the dataset is changed
                if (value < xValues.size() && value > 0) {
                    return xValues.get((int) value);
                } else {
                    return "";
                }
            }
        };
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

    }

    private LineData generateGlucoseData() {
        List<String> xVals = new ArrayList<>();
        List<Entry> yVals = new ArrayList<>();

        if (graphSpinnerRange.getSelectedItemPosition() == 0) {
            List<Integer> glucosioReadings = presenter.getGlucoseReadings();

            // Day view
            for (int i = 0; i < glucosioReadings.size(); i++) {
                if (presenter.getUnitMeasuerement().equals("mg/dL")) {
                    float val = Float.parseFloat(glucosioReadings.get(i).toString());
                    yVals.add(new Entry(i, val));
                } else {
                    double val = GlucosioConverter.glucoseToMmolL(Double.parseDouble(glucosioReadings.get(i).toString()));
                    float converted = (float) val;
                    yVals.add(new Entry(i, converted));
                }
            }
        } else if (graphSpinnerRange.getSelectedItemPosition() == 1) {
            List<Integer> glucosioReadingsWeek = presenter.getGlucoseReadingsWeek();
            // Week view
            for (int i = 0; i < presenter.getGlucoseReadingsWeek().size(); i++) {
                if (presenter.getUnitMeasuerement().equals("mg/dL")) {
                    float val = Float.parseFloat(glucosioReadingsWeek.get(i) + "");
                    yVals.add(new Entry(i, val));
                } else {
                    double val = GlucosioConverter.glucoseToMmolL(Double.parseDouble(glucosioReadingsWeek.get(i) + ""));
                    float converted = (float) val;
                    yVals.add(new Entry(i, converted));
                }
            }
        } else {
            List<Integer> glucosioReadingsMonth = presenter.getGlucoseReadingsMonth();
            // Month view
            for (int i = 0; i < presenter.getGlucoseReadingsMonth().size(); i++) {
                if (presenter.getUnitMeasuerement().equals("mg/dL")) {
                    float val = Float.parseFloat(glucosioReadingsMonth.get(i) + "");
                    yVals.add(new Entry(i, val));
                } else {
                    double val = GlucosioConverter.glucoseToMmolL(Double.parseDouble(glucosioReadingsMonth.get(i) + ""));
                    float converted = (float) val;
                    yVals.add(new Entry(i, converted));
                }
            }
        }

        if (graphSpinnerRange.getSelectedItemPosition() == 0) {
            // Day view
            for (int i = 0; i < presenter.getGraphGlucoseDateTime().size(); i++) {
                String date = presenter.convertDate(presenter.getGraphGlucoseDateTime().get(i));
                xVals.add(date);
            }
        } else if (graphSpinnerRange.getSelectedItemPosition() == 1) {
            // Week view
            for (int i = 0; i < presenter.getGlucoseReadingsWeek().size(); i++) {
                String date = presenter.convertDate(presenter.getGlucoseDatetimeWeek().get(i));
                xVals.add(date);
            }
        } else {
            // Month view
            for (int i = 0; i < presenter.getGlucoseReadingsMonth().size(); i++) {
                String date = presenter.convertDateToMonth(presenter.getGlucoseDatetimeMonth().get(i));
                xVals.add(date);
            }
        }

        xValues = xVals;
        LineData data = new LineData(generateLineDataSet(yVals, getResources().getColor(R.color.glucosio_pink)));
        return data;
    }

    private LineData generateA1cData() {
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals = new ArrayList<>();

        int k = 0;
        for (int i = presenter.getA1cReadings().size() - 1; i >= 0; i--) {
            float val = Float.parseFloat(presenter.getA1cReadings().get(i).toString());
            yVals.add(new Entry(k, val));
            k++;
        }

        for (int i = presenter.getA1cReadingsDateTime().size() - 1; i >= 0; i--) {
            String date = presenter.convertDate(presenter.getA1cReadingsDateTime().get(i));
            xVals.add(date);
        }

        xValues = xVals;
        // create a data object with the datasets
        return new LineData(generateLineDataSet(yVals, getResources().getColor(R.color.glucosio_fab_HB1AC)));
    }

    private LineData generateKetonesData() {
        List<String> xVals = new ArrayList<>();
        List<Entry> yVals = new ArrayList<>();

        int  k = 0;
        for (int i = presenter.getKetonesReadings().size() - 1; i >= 0; i--) {
            float val = Float.parseFloat(presenter.getKetonesReadings().get(i).toString());
            yVals.add(new Entry(k, val));
            k++;
        }

        for (int i = presenter.getKetonesReadingsDateTime().size() - 1; i >= 0; i--) {
            String date = presenter.convertDate(presenter.getKetonesReadingsDateTime().get(i));
            xVals.add(date);
        }

        xValues = xVals;
        // create a data object with the datasets
        return new LineData(generateLineDataSet(yVals, getResources().getColor(R.color.glucosio_fab_ketones)));
    }

    private LineData generateWeightData() {
        List<String> xVals = new ArrayList<>();
        List<Entry> yVals = new ArrayList<>();

        int k = 0;
        for (int i = presenter.getWeightReadings().size() - 1; i >= 0; i--) {
            float val = Float.parseFloat(presenter.getWeightReadings().get(i).toString());
            yVals.add(new Entry(k, val));
            k++;
        }

        for (int i = presenter.getWeightReadingsDateTime().size() - 1; i >= 0; i--) {
            String date = presenter.convertDate(presenter.getWeightReadingsDateTime().get(i));
            xVals.add(date);
        }

        xValues = xVals;
        // create a data object with the datasets
        return new LineData(generateLineDataSet(yVals, getResources().getColor(R.color.glucosio_fab_weight)));
    }

    private LineData generatePressureData() {
        List<String> xVals = new ArrayList<>();
        List<Entry> yValsMax = new ArrayList<>();
        List<Entry> yValsMin = new ArrayList<>();

        int k = 0;
        for (int i = presenter.getMaxPressureReadings().size() - 1; i >= 0; i--) {
            float val = Float.parseFloat(presenter.getMaxPressureReadings().get(i).toString());
            yValsMax.add(new Entry(k, val));
            k++;
        }

        int j = 0;
        for (int i = presenter.getMinPressureReadings().size() - 1; i >= 0; i--) {
            float val = Float.parseFloat(presenter.getMinPressureReadings().get(i).toString());
            yValsMin.add(new Entry(j, val));
            j++;
        }

        for (int i = presenter.getPressureReadingsDateTime().size() - 1; i >= 0; i--) {
            String date = presenter.convertDate(presenter.getPressureReadingsDateTime().get(i));
            xVals.add(date);
        }

        xValues = xVals;
        LineData data = new LineData(generateLineDataSet(yValsMax, getResources().getColor(R.color.glucosio_fab_pressure)));
        data.addDataSet(generateLineDataSet(yValsMin, getResources().getColor(R.color.glucosio_fab_pressure)));
        // create a data object with the datasets
        return data;
    }

    private LineData generateCholesterolData() {
        List<String> xVals = new ArrayList<>();
        List<Entry> yVals = new ArrayList<>();

        int k = 0;
        for (int i = presenter.getCholesterolReadings().size() - 1; i >= 0; i--) {
            float val = Float.parseFloat(presenter.getCholesterolReadings().get(i).toString());
            yVals.add(new Entry(k, val));
            k++;
        }

        for (int i = presenter.getCholesterolReadingsDateTime().size() - 1; i >= 0; i--) {
            String date = presenter.convertDate(presenter.getCholesterolReadingsDateTime().get(i));
            xVals.add(date);
        }

        xValues = xVals;
        // create a data object with the datasets
        return new LineData(generateLineDataSet(yVals, getResources().getColor(R.color.glucosio_fab_cholesterol)));
    }

    private LineDataSet generateLineDataSet(List<Entry> vals, int color) {
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(vals, "");
        List<Integer> colors = new ArrayList<>();

        if (color == getResources().getColor(R.color.glucosio_pink)) {
            for (Entry val : vals) {
                if (val.getY() == (0)) {
                    colors.add(Color.TRANSPARENT);
                } else {
                    colors.add(color);
                }
            }
            set1.setCircleColors(colors);
        } else {
            set1.setCircleColor(color);
        }

        set1.setColor(color);
        set1.setLineWidth(2f);
        set1.setCircleSize(4f);
        set1.setDrawCircleHole(true);
        set1.disableDashedLine();
        set1.setFillAlpha(255);
        set1.setDrawFilled(true);
        set1.setValueTextSize(0);
        set1.setValueTextColor(Color.parseColor("#FFFFFF"));
        set1.setFillDrawable(getResources().getDrawable(R.drawable.graph_gradient));
        set1.setHighLightColor(getResources().getColor(R.color.glucosio_gray_light));
        set1.setCubicIntensity(0.2f);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            set1.setDrawFilled(false);
            set1.setLineWidth(2f);
            set1.setCircleSize(4f);
            set1.setDrawCircleHole(true);
        }
        return set1;
    }

    private void showA1cDialog() {
        final Dialog a1CDialog = new Dialog(getActivity(), R.style.GlucosioTheme);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(a1CDialog.getWindow().getAttributes());
        a1CDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        a1CDialog.setContentView(R.layout.dialog_a1c);
        a1CDialog.getWindow().setAttributes(lp);
        a1CDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        a1CDialog.getWindow().setDimAmount(0.5f);
        a1CDialog.setCanceledOnTouchOutside(true);
        a1CDialog.show();

        ListView a1cListView = (ListView) a1CDialog.findViewById(R.id.dialog_a1c_listview);

        A1cEstimateAdapter customAdapter = new A1cEstimateAdapter(
                getActivity(), R.layout.dialog_a1c_item, presenter.getA1cEstimateList());

        a1cListView.setAdapter(customAdapter);
    }

    private void loadHB1AC() {
        if (!presenter.isdbEmpty()) {
            HB1ACTextView.setText(presenter.getHB1AC());
            HB1ACDateTextView.setText(presenter.getA1cMonth());
            // We show the A1C more button only if 2 or more A1C estimates are available
            if (!presenter.isA1cAvailable(2)) {
                HB1ACMoreButton.setVisibility(View.GONE);
            }
        }
    }

    private void loadLastReading() {
        if (!presenter.isdbEmpty()) {
            if (presenter.getUnitMeasuerement().equals("mg/dL")) {
                String reading = presenter.getLastReading();
                lastReadingTextView.setText(getString(R.string.mg_dL_value, reading));
            } else {
                String mgdl = presenter.getLastReading();
                double mmol = GlucosioConverter.glucoseToMmolL(Double.parseDouble(mgdl));
                String reading = NumberFormat.getInstance().format(mmol);
                lastReadingTextView.setText(getString(R.string.mmol_L_value, reading));
            }

            FormatDateTime dateTime = new FormatDateTime(getActivity().getApplicationContext());

            lastDateTextView.setText(dateTime.convertDate(presenter.getLastDateTime()));
            GlucoseRanges ranges = new GlucoseRanges(getActivity().getApplicationContext());
            String color = ranges.colorFromReading(Integer.parseInt(presenter.getLastReading()));
            lastReadingTextView.setTextColor(ranges.stringToColor(color));
        }
    }

    private void loadRandomTip() {
        TipsManager tipsManager = new TipsManager(getActivity().getApplicationContext(), presenter.getUserAge());
        tipTextView.setText(presenter.getRandomTip(tipsManager));
    }

    private boolean isNewGraphEnabled() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        return !sharedPref.getBoolean("pref_graph_old", false);
    }

    @NonNull
    public String convertDate(@NonNull final String date) {
        FormatDateTime dateTime = new FormatDateTime(getActivity().getApplicationContext());
        return dateTime.convertDate(date);
    }

    @NonNull
    public String convertDateToMonth(@NonNull final String date) {
        FormatDateTime dateTime = new FormatDateTime((getActivity().getApplication()));
        return dateTime.convertDateToMonthOverview(date);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    exportGraphToGallery();
                } else {
                    Snackbar.make(mFragmentView, R.string.fragment_overview_permission_storage, Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }
}