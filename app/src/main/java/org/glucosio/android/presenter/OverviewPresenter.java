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

package org.glucosio.android.presenter;

import org.glucosio.android.R;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.fragment.OverviewFragment;
import org.glucosio.android.object.A1cEstimate;
import org.glucosio.android.tools.GlucoseConverter;
import org.glucosio.android.tools.TipsManager;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OverviewPresenter {

    private DatabaseHandler dB;
    private ArrayList<Integer> glucoseReading;
    private ArrayList<String> glucoseType;
    private ArrayList<String> glucoseDatetime;
    private List<Integer> glucoseReadingsWeek;
    private List<Integer> glucoseReadingsMonth;
    private List<String> glucoseDatetimeWeek;
    private List<String> glucoseDatetimeMonth;
    private int glucoseMinValue = 0;
    private int glucoseMaxValue = 0;
    private OverviewFragment fragment;


    public OverviewPresenter(OverviewFragment overviewFragment) {
        dB = new DatabaseHandler(overviewFragment.getContext());
        this.fragment = overviewFragment;
    }

    public boolean isdbEmpty() {
        return dB.getGlucoseReadings().size() == 0;
    }

    public void loadDatabase() {
        this.glucoseReading = dB.getGlucoseReadingAsArray();
        this.glucoseReadingsMonth = dB.getAverageGlucoseReadingsByMonth();
        this.glucoseReadingsWeek = dB.getAverageGlucoseReadingsByWeek();
        this.glucoseDatetimeWeek = dB.getGlucoseDatetimesByWeek();
        this.glucoseDatetimeMonth = dB.getGlucoseDatetimesByMonth();
        this.glucoseType = dB.getGlucoseTypeAsArray();
        this.glucoseDatetime = dB.getGlucoseDateTimeAsArray();
        this.glucoseMaxValue = dB.getUser(1).getCustom_range_max();
        this.glucoseMinValue = dB.getUser(1).getCustom_range_min();
    }

    public String convertDate(String date) {
        return fragment.convertDate(date);
    }

/*    public int getGlucoseTrend(){
        return dB.getAverageGlucoseReadingForLastMonth();
    }*/

    public String getHB1AC() {
        // Check if last month is available first
        if (getGlucoseReadingsMonth().size() > 1) {
            GlucoseConverter converter = new GlucoseConverter();
            return converter.glucoseToA1C(getGlucoseReadingsMonth().get(getGlucoseReadingsMonth().size() - 2)) + " %";
        } else {
            return fragment.getResources().getString(R.string.overview_hb1ac_error_no_data);
        }
    }

    public boolean isA1cAvailable(int depth){
        return getGlucoseReadingsMonth().size()>depth;
    }

    public ArrayList<A1cEstimate> getA1cEstimateList(){
        GlucoseConverter converter = new GlucoseConverter();
        ArrayList<A1cEstimate> a1cEstimateList = new ArrayList<>();

        // We don't take this month because A1C is incomplete
        for (int i=0; i<getGlucoseReadingsMonth().size()-1; i++){
            double value = converter.glucoseToA1C(getGlucoseReadingsMonth().get(i));
            String month = convertDateToMonth(getGlucoseDatetimeMonth().get(i));
            a1cEstimateList.add(new A1cEstimate(value, month));
        }
        Collections.reverse(a1cEstimateList);
        return a1cEstimateList;
    }

    public String getH1ACMonth() {
        // Check if last month is available first
        if (getGlucoseReadingsMonth().size() > 1) {
            return convertDateToMonth(getGlucoseDatetimeMonth().get(getGlucoseDatetimeMonth().size() - 2)) + "";
        } else {
            return " ";
        }
    }

    public String getLastReading() {
        return getGlucoseReading().get(getGlucoseReading().size() - 1) + "";
    }

    public String getLastDateTime() {
        return getGlucoseDatetime().get(getGlucoseDatetime().size() - 1) + "";
    }

    public String getRandomTip(TipsManager manager) {
        ArrayList<String> tips = manager.getTips();

        // Get random tip from array
        int randomNumber = new Random().nextInt(tips.size());
        return tips.get(randomNumber);
    }

    public String getUnitMeasuerement() {
        return dB.getUser(1).getPreferred_unit();
    }

    public int getUserAge() {
        return dB.getUser(1).getAge();
    }

    public ArrayList<Integer> getGlucoseReading() {
        return glucoseReading;
    }

    public ArrayList<String> getGlucoseType() {
        return glucoseType;
    }

    public ArrayList<String> getGlucoseDatetime() {
        return glucoseDatetime;
    }

    public List<Integer> getGlucoseReadingsWeek() {
        return glucoseReadingsWeek;
    }

    public List<Integer> getGlucoseReadingsMonth() {
        return glucoseReadingsMonth;
    }

    public List<String> getGlucoseDatetimeWeek() {
        return glucoseDatetimeWeek;
    }

    public List<String> getGlucoseDatetimeMonth() {
        return glucoseDatetimeMonth;
    }

    public String convertDateToMonth(String s) {
        return fragment.convertDateToMonth(s);
    }

    public int getGlucoseMinValue() {
        return glucoseMinValue;
    }

    public int getGlucoseMaxValue() {
        return glucoseMaxValue;
    }
}
