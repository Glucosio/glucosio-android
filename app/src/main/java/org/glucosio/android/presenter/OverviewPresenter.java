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
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.fragment.OverviewView;
import org.glucosio.android.object.A1cEstimate;
import org.glucosio.android.object.GlucoseGraphObject;
import org.glucosio.android.tools.GlucosioConverter;
import org.glucosio.android.tools.TipsManager;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OverviewPresenter {

    private DatabaseHandler dB;
    private OverviewView view;

    private ArrayList<String> glucoseType;
    private List<Integer> glucoseReadingsWeek;
    private List<Integer> glucoseReadingsMonth;
    private List<String> glucoseDatetimeWeek;
    private List<String> glucoseDatetimeMonth;
    private List<GlucoseReading> glucoseReadings;
    private List<GlucoseGraphObject> glucoseGraphObjects;
    private int glucoseMinValue = 0;
    private int glucoseMaxValue = 0;

    public OverviewPresenter(OverviewView view, DatabaseHandler dB) {
        this.dB = dB;
        this.view = view;
    }

    public boolean isdbEmpty() {
        return dB.getGlucoseReadings().size() == 0;
    }

    public void loadDatabase() {
        this.glucoseReadings = dB.getGlucoseReadings();
        this.glucoseGraphObjects = generateGlucoseGraphPoints();
        this.glucoseReadingsMonth = dB.getAverageGlucoseReadingsByMonth();
        this.glucoseReadingsWeek = dB.getAverageGlucoseReadingsByWeek();
        this.glucoseDatetimeWeek = dB.getGlucoseDatetimesByWeek();
        this.glucoseDatetimeMonth = dB.getGlucoseDatetimesByMonth();
        this.glucoseType = dB.getGlucoseTypeAsArray();
        this.glucoseMaxValue = dB.getUser(1).getCustom_range_max();
        this.glucoseMinValue = dB.getUser(1).getCustom_range_min();
    }

    public String convertDate(String date) {
        return view.convertDate(date);
    }

/*    public int getGlucoseTrend(){
        return dB.getAverageGlucoseReadingForLastMonth();
    }*/

    public String getHB1AC() {
        // Check if last month is available first
        if (getGlucoseReadingsMonth().size() > 1) {
            GlucosioConverter converter = new GlucosioConverter();
            if ("percentage".equals(dB.getUser(1).getPreferred_unit_a1c())) {
                return converter.glucoseToA1C(getGlucoseReadingsMonth().get(getGlucoseReadingsMonth().size() - 2)) + " %";
            } else {
                return converter.a1cNgspToIfcc(converter.glucoseToA1C(getGlucoseReadingsMonth().get(getGlucoseReadingsMonth().size() - 2))) + " mmol/mol";
            }
        } else {
            return view.getString(R.string.overview_hb1ac_error_no_data);
        }
    }

    public boolean isA1cAvailable(int depth) {
        return getGlucoseReadingsMonth().size() > depth;
    }

    public ArrayList<A1cEstimate> getA1cEstimateList() {
        GlucosioConverter converter = new GlucosioConverter();
        ArrayList<A1cEstimate> a1cEstimateList = new ArrayList<>();

        // We don't take this month because A1C is incomplete
        for (int i = 0; i < getGlucoseReadingsMonth().size() - 1; i++) {
            double value = converter.glucoseToA1C(getGlucoseReadingsMonth().get(i));
            String month = convertDateToMonth(getGlucoseDatetimeMonth().get(i));
            a1cEstimateList.add(new A1cEstimate(value, month));
        }
        Collections.reverse(a1cEstimateList);
        return a1cEstimateList;
    }

    public String getA1cMonth() {
        // Check if last month is available first
        if (getGlucoseReadingsMonth().size() > 1) {
            return convertDateToMonth(getGlucoseDatetimeMonth().get(getGlucoseDatetimeMonth().size() - 2)) + "";
        } else {
            return " ";
        }
    }

    public String getLastReading() {
        return glucoseReadings.get(glucoseReadings.size() - 1).getReading() + "";
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

    public List<Integer> getGlucoseReadings() {

        ArrayList<Integer> glucoseReadings = new ArrayList<>();
        for (int i=0; i<glucoseGraphObjects.size(); i++) {
            glucoseReadings.add(glucoseGraphObjects.get(i).getReading());
        }

        return glucoseReadings;
    }

    private List<GlucoseGraphObject> generateGlucoseGraphPoints() {
        final List<GlucoseReading> glucoseReadings = dB.getGlucoseReadings();

        DateTime startDate = glucoseReadings.size() > 0 ?
                new DateTime(glucoseReadings.get(0).getCreated()) :
                DateTime.now();
        // This will contain final values
        final ArrayList<GlucoseGraphObject> finalGlucoseGraphObjects = new ArrayList<>();
        // Transfer values from database to ArrayList as GlucoseGraphObjects
        for (int i=0; i<glucoseReadings.size(); i++){
            final GlucoseReading reading = glucoseReadings.get(i);
            final DateTime createdDate = new DateTime(reading.getCreated());
            //add zero values between current value and last added value
            addReadings(finalGlucoseGraphObjects, startDate, createdDate);
            //add new value
            finalGlucoseGraphObjects.add(
                    new GlucoseGraphObject(createdDate, reading.getReading())
            );
            //update start date
            startDate = createdDate;
        }
        //add last zeros till now
        addReadings(finalGlucoseGraphObjects, startDate, DateTime.now());

        return finalGlucoseGraphObjects;
    }

    private void addReadings(final ArrayList<GlucoseGraphObject> graphObjects,
                             final DateTime firstDate,
                             final DateTime lastDate) {
        int daysBetween = Days.daysBetween(firstDate, lastDate).getDays();
        for (int i = 1; i < daysBetween; i++) {
            graphObjects.add(new GlucoseGraphObject(firstDate.plusDays(i), 0));
        }
    }

    public ArrayList<String> getGlucoseType() {
        return glucoseType;
    }

    public ArrayList<String> getGlucoseDatetime() {
        return dB.getGlucoseDateTimeAsArray();
    }

    public ArrayList<String> getGraphGlucoseDateTime(){
        ArrayList<String> glucoseDatetime = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

        for (int i=0; i<glucoseGraphObjects.size(); i++){
            glucoseDatetime.add(dateTimeFormatter.print(glucoseGraphObjects.get(i).getCreated()));
        }
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
        return view.convertDateToMonth(s);
    }

    public int getGlucoseMinValue() {
        return glucoseMinValue;
    }

    public int getGlucoseMaxValue() {
        return glucoseMaxValue;
    }
}
