package org.glucosio.android.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.glucosio.android.R;
import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.db.DatabaseHandler;

import java.util.ArrayList;
import java.util.Collections;

public class OverviewFragment extends Fragment {

    LineChart chart;
    DatabaseHandler db;
    ArrayList<Double> reading;
    ArrayList<String> datetime;

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
        View mFragmentView = inflater.inflate(R.layout.fragment_overview, container, false);
        chart = (LineChart) mFragmentView.findViewById(R.id.chart);
        Legend legend = chart.getLegend();

        db = ((MainActivity)getActivity()).getDatabase();
        reading = db.getGlucoseReadingAsArray();
        datetime = db.getGlucoseDateTimeAsArray();

        Collections.reverse(reading);
        Collections.reverse(datetime);



        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        LimitLine ll1 = new LimitLine(130f, "");
        ll1.setLineWidth(2f);
        ll1.setLineColor(getResources().getColor(R.color.glucosio_accent));

        LimitLine ll2 = new LimitLine(70f, "");
        ll2.setLineWidth(2f);
        ll2.setLineColor(getResources().getColor(R.color.glucosio_accent));

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setStartAtZero(false);
        //leftAxis.setYOffset(20f);
        leftAxis.disableGridDashedLine();

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        chart.getAxisRight().setEnabled(false);

        setData();
        legend.setEnabled(false);
        chart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

        return mFragmentView;
    }

    private void setData() {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < datetime.size(); i++) {
            xVals.add(datetime.get(i) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < reading.size(); i++) {

            float val = Float.parseFloat(reading.get(i).toString());
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "");
        // set the line to be drawn like this "- - - - - -"
        set1.setColor(getResources().getColor(R.color.glucosio_pink));
        set1.setCircleColor(getResources().getColor(R.color.glucosio_pink));
        set1.setLineWidth(1f);
        set1.setCircleSize(4f);
        set1.setDrawCircleHole(false);
        set1.disableDashedLine();
        set1.setValueTextSize(9f);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.BLACK);
//        set1.setDrawFilled(true);
        // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
        // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        chart.setData(data);
    }
}