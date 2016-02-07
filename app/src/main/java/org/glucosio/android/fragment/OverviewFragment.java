package org.glucosio.android.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.glucosio.android.R;
import org.glucosio.android.presenter.OverviewPresenter;
import org.glucosio.android.tools.FormatDateTime;
import org.glucosio.android.tools.GlucoseConverter;
import org.glucosio.android.tools.GlucoseRanges;
import org.glucosio.android.tools.TipsManager;

import java.util.ArrayList;
import java.util.Collections;

public class OverviewFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private LineChart chart;
    private TextView lastReadingTextView;
    private TextView lastDateTextView;
    private TextView trendTextView;
    private TextView tipTextView;
    private TextView HB1ACTextView;
    private TextView HB1ACDateTextView;
    private ImageButton graphExport;
    private Spinner graphSpinner;
    private OverviewPresenter presenter;
    private View mFragmentView;

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();


        return fragment;
    }

    public OverviewFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        presenter = new OverviewPresenter(this);
        if (!presenter.isdbEmpty()) {
            presenter.loadDatabase();
        }

        mFragmentView = inflater.inflate(R.layout.fragment_overview, container, false);

        chart = (LineChart) mFragmentView.findViewById(R.id.chart);
        Legend legend = chart.getLegend();

        if (!presenter.isdbEmpty()) {
            Collections.reverse(presenter.getReading());
            Collections.reverse(presenter.getDatetime());
            Collections.reverse(presenter.getType());
        }

        lastReadingTextView = (TextView) mFragmentView.findViewById(R.id.item_history_reading);
        lastDateTextView = (TextView) mFragmentView.findViewById(R.id.fragment_overview_last_date);
        trendTextView = (TextView) mFragmentView.findViewById(R.id.item_history_trend);
        tipTextView = (TextView) mFragmentView.findViewById(R.id.random_tip_textview);
        graphSpinner = (Spinner) mFragmentView.findViewById(R.id.chart_spinner);
        graphExport = (ImageButton) mFragmentView.findViewById(R.id.fragment_overview_graph_export);
        HB1ACTextView = (TextView) mFragmentView.findViewById(R.id.fragment_overview_hb1ac);
        HB1ACDateTextView = (TextView) mFragmentView.findViewById(R.id.fragment_overview_hb1ac_date);

        // Set array and adapter for graphSpinner
        String[] selectorArray = getActivity().getResources().getStringArray(R.array.fragment_overview_selector);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, selectorArray);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        graphSpinner.setAdapter(dataAdapter);

        graphSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!presenter.isdbEmpty()) {
                    setData();
                    chart.invalidate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(getResources().getColor(R.color.glucosio_text_light));
        xAxis.setAvoidFirstLastClipping(true);

      /*  LimitLine ll1 = new LimitLine(130f, "High");
        ll1.setLineWidth(1f);
        ll1.setLineColor(getResources().getColor(R.color.glucosio_gray_light));
        ll1.setTextColor(getResources().getColor(R.color.glucosio_text));

        LimitLine ll2 = new LimitLine(70f, "Low");
        ll2.setLineWidth(1f);
        ll2.setLineColor(getResources().getColor(R.color.glucosio_gray_light));
        ll2.setTextColor(getResources().getColor(R.color.glucosio_text));

        LimitLine ll3 = new LimitLine(200f, "Hyper");
        ll3.setLineWidth(1f);
        ll3.enableDashedLine(10, 10, 10);
        ll3.setLineColor(getResources().getColor(R.color.glucosio_gray_light));
        ll3.setTextColor(getResources().getColor(R.color.glucosio_text));

        LimitLine ll4 = new LimitLine(50f, "Hypo");
        ll4.setLineWidth(1f);
        ll4.enableDashedLine(10, 10, 10);
        ll4.setLineColor(getResources().getColor(R.color.glucosio_gray_light));
        ll4.setTextColor(getResources().getColor(R.color.glucosio_text));*/

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
/*        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.addLimitLine(ll3);
        leftAxis.addLimitLine(ll4);*/
        leftAxis.setTextColor(getResources().getColor(R.color.glucosio_text_light));
        leftAxis.setStartAtZero(false);
        //leftAxis.setYOffset(20f);
        leftAxis.disableGridDashedLine();
        leftAxis.setDrawGridLines(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        chart.getAxisRight().setEnabled(false);
        chart.setBackgroundColor(Color.parseColor("#FFFFFF"));
        chart.setDescription("");
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
/*
        loadGlucoseTrend();
*/
        loadRandomTip();

        return mFragmentView;
    }

    private void exportGraphToGallery() {
        long timestamp = System.currentTimeMillis()/1000;
        boolean saved = chart.saveToGallery("glucosio_" + timestamp , 50);
        if (saved) {
            Snackbar.make(mFragmentView, R.string.fragment_overview_graph_export_true, Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(mFragmentView, R.string.fragment_overview_graph_export_false, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void setData() {

        ArrayList<String> xVals = new ArrayList<String>();

        if (graphSpinner.getSelectedItemPosition() == 0) {
            // Day view
            for (int i = 0; i < presenter.getDatetime().size(); i++) {
                String date = presenter.convertDate(presenter.getDatetime().get(i));
                xVals.add(date + "");
            }
        } else if (graphSpinner.getSelectedItemPosition() == 1){
            // Week view
            for (int i = 0; i < presenter.getReadingsWeek().size(); i++) {
                String date = presenter.convertDate(presenter.getDatetimeWeek().get(i));
                xVals.add(date + "");
            }
        } else {
            // Month view
            for (int i = 0; i < presenter.getReadingsMonth().size(); i++) {
                String date = presenter.convertDateToMonth(presenter.getDatetimeMonth().get(i));
                xVals.add(date + "");
            }
        }

        GlucoseConverter converter = new GlucoseConverter();

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        ArrayList<Integer> colors = new ArrayList<>();

        if (graphSpinner.getSelectedItemPosition() == 0) {
            // Day view
            for (int i = 0; i < presenter.getReading().size(); i++) {
                if (presenter.getUnitMeasuerement().equals("mg/dL")) {
                    float val = Float.parseFloat(presenter.getReading().get(i).toString());
                    yVals.add(new Entry(val, i));
                } else {
                    double val = converter.glucoseToMmolL(Double.parseDouble(presenter.getReading().get(i).toString()));
                    float converted = (float) val;
                    yVals.add(new Entry(converted, i));
                }
                GlucoseRanges ranges = new GlucoseRanges(getActivity().getApplicationContext());
                colors.add(ranges.stringToColor(ranges.colorFromReading(presenter.getReading().get(i))));
            }
        } else if (graphSpinner.getSelectedItemPosition() == 1){
            // Week view
            for (int i = 0; i < presenter.getReadingsWeek().size(); i++) {
                if (presenter.getUnitMeasuerement().equals("mg/dL")) {
                    float val = Float.parseFloat(presenter.getReadingsWeek().get(i)+"");
                    yVals.add(new Entry(val, i));
                } else {
                    double val = converter.glucoseToMmolL(Double.parseDouble(presenter.getReadingsWeek().get(i)+""));
                    float converted = (float) val;
                    yVals.add(new Entry(converted, i));
                }
            }
            colors.add(getResources().getColor(R.color.glucosio_pink));
        } else {
            // Month view
            for (int i = 0; i < presenter.getReadingsMonth().size(); i++) {
                if (presenter.getUnitMeasuerement().equals("mg/dL")) {
                    float val = Float.parseFloat(presenter.getReadingsMonth().get(i)+"");
                    yVals.add(new Entry(val, i));
                } else {
                    double val = converter.glucoseToMmolL(Double.parseDouble(presenter.getReadingsMonth().get(i)+""));
                    float converted = (float) val;
                    yVals.add(new Entry(converted, i));
                }
            }
            colors.add(getResources().getColor(R.color.glucosio_pink));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "");
        // set the line to be drawn like this "- - - - - -"
        set1.setColor(getResources().getColor(R.color.glucosio_pink));
        set1.setCircleColors(colors);
        set1.setLineWidth(0f);
        set1.setCircleSize(2.8f);
        set1.setDrawCircleHole(false);
        set1.disableDashedLine();
        set1.setFillAlpha(255);
        set1.setDrawFilled(true);
        set1.setValueTextSize(0);
        set1.setValueTextColor(Color.parseColor("#FFFFFF"));
        set1.setFillColor(Color.parseColor("#FCE2EA"));

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2){
            set1.setDrawFilled(false);
            set1.setLineWidth(3f);
            set1.setCircleSize(4.5f);
            set1.setDrawCircleHole(true);
        }

//        set1.setDrawFilled(true);
        // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
        // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        chart.setData(data);
        chart.setPinchZoom(true);
        chart.setHardwareAccelerationEnabled(true);
        chart.animateY(1000, Easing.EasingOption.EaseOutCubic);
    }

    private void loadHB1AC(){
        if (!presenter.isdbEmpty()){
            HB1ACTextView.setText(presenter.getHB1AC());
            HB1ACDateTextView.setText(presenter.getH1ACMonth());
        }
    }

    private void loadLastReading(){
        if (!presenter.isdbEmpty()) {
            if (presenter.getUnitMeasuerement().equals("mg/dL")) {
                lastReadingTextView.setText(presenter.getLastReading() + " mg/dL");
            } else {
                GlucoseConverter converter = new GlucoseConverter();
                lastReadingTextView.setText(converter.glucoseToMmolL(Double.parseDouble(presenter.getLastReading().toString())) + " mmol/L");
            }

            FormatDateTime dateTime = new FormatDateTime(getActivity().getApplicationContext());

            lastDateTextView.setText(dateTime.convertDate(presenter.getLastDateTime()));
            GlucoseRanges ranges = new GlucoseRanges(getActivity().getApplicationContext());
            String color = ranges.colorFromReading(Integer.parseInt(presenter.getLastReading()));
            lastReadingTextView.setTextColor(ranges.stringToColor(color));
        }
    }

/*    private void loadGlucoseTrend(){
        if (!presenter.isdbEmpty()) {
            trendTextView.setText(presenter.getGlucoseTrend() + "");
        }
    }*/

    private void loadRandomTip(){
        TipsManager tipsManager = new TipsManager(getActivity().getApplicationContext(), presenter.getUserAge());
        tipTextView.setText(presenter.getRandomTip(tipsManager));
    }

    public String convertDate(String date){
        FormatDateTime dateTime = new FormatDateTime(getActivity().getApplicationContext());
        return dateTime.convertDate(date);
    }

    public String convertDateToMonth(String date){
        FormatDateTime dateTime = new FormatDateTime((getActivity().getApplication()));
        return dateTime.convertDateToMonthOverview(date);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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