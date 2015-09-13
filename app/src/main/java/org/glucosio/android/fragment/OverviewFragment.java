package org.glucosio.android.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import org.glucosio.android.presenter.OverviewPresenter;
import org.glucosio.android.tools.ReadingTools;
import org.glucosio.android.tools.TipsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class OverviewFragment extends Fragment {

    LineChart chart;
    TextView readingTextView;
    TextView trendTextView;
    TextView tipTextView;
    OverviewPresenter presenter;

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
        View mFragmentView;
        presenter = new OverviewPresenter(this);
        presenter.loadDatabase();

        if (!presenter.isdbEmpty()) {
            mFragmentView = inflater.inflate(R.layout.fragment_overview, container, false);

            chart = (LineChart) mFragmentView.findViewById(R.id.chart);
            Legend legend = chart.getLegend();

            Collections.reverse(presenter.getReading());
            Collections.reverse(presenter.getDatetime());
            Collections.reverse(presenter.getType());

            readingTextView = (TextView) mFragmentView.findViewById(R.id.item_history_reading);
            trendTextView = (TextView) mFragmentView.findViewById(R.id.item_history_trend);
            tipTextView = (TextView) mFragmentView.findViewById(R.id.random_tip_textview);

            XAxis xAxis = chart.getXAxis();
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextColor(getResources().getColor(R.color.glucosio_text_light));

            LimitLine ll1 = new LimitLine(130f, "High");
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
            ll4.setTextColor(getResources().getColor(R.color.glucosio_text));

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
            leftAxis.addLimitLine(ll1);
            leftAxis.addLimitLine(ll2);
            leftAxis.addLimitLine(ll3);
            leftAxis.addLimitLine(ll4);
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
            setData();
            legend.setEnabled(false);

            loadLastReading();
            loadGlucoseTrend();
            loadRandomTip();

        } else {
            mFragmentView = inflater.inflate(R.layout.fragment_empty, container, false);
        }
        return mFragmentView;
    }

    private void setData() {

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < presenter.getDatetime().size(); i++) {
            String date = presenter.convertDate(presenter.getDatetime().get(i));
            xVals.add(date + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < presenter.getReading().size(); i++) {

            float val = Float.parseFloat(presenter.getReading().get(i).toString());
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
        set1.setFillAlpha(65);
        set1.setValueTextSize(0);
        set1.setValueTextColor(Color.parseColor("#FFFFFF"));
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

    private void loadLastReading(){
        if (!presenter.isdbEmpty()) {
            readingTextView.setText(presenter.getLastReading());
        }
    }

    private void loadGlucoseTrend(){
        if (!presenter.isdbEmpty()) {
            trendTextView.setText(presenter.getGlucoseTrend() + "");
        }
    }

    private void loadRandomTip(){
        TipsManager tipsManager = new TipsManager(getActivity().getApplicationContext(), presenter.getUserAge());
        tipTextView.setText(presenter.getRandomTip(tipsManager));
    }
}