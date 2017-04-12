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
import org.glucosio.android.db.CholesterolReading;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.db.HB1ACReading;
import org.glucosio.android.db.KetoneReading;
import org.glucosio.android.db.PressureReading;
import org.glucosio.android.db.WeightReading;
import org.glucosio.android.object.A1cEstimate;
import org.glucosio.android.object.IntGraphObject;
import org.glucosio.android.tools.GlucosioConverter;
import org.glucosio.android.tools.TipsManager;
import org.glucosio.android.view.OverviewView;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class OverviewPresenter {

    private DatabaseHandler dB;
    private OverviewView view;

    private List<Integer> glucoseReadingsWeek;
    private List<Integer> glucoseReadingsMonth;
    private List<String> glucoseDatetimeWeek;
    private List<String> glucoseDatetimeMonth;
    private List<GlucoseReading> glucoseReadings;
    private List<IntGraphObject> glucoseGraphObjects;
    private int glucoseMinValue = 0;
    private int glucoseMaxValue = 0;

    public OverviewPresenter(OverviewView view, DatabaseHandler dB) {
        this.dB = dB;
        this.view = view;
    }

    public boolean isdbEmpty() {
        return dB.getGlucoseReadings().size() == 0;
    }

    public void loadDatabase(boolean isNewGraphEnabled) {
        this.glucoseReadings = dB.getGlucoseReadings();
        this.glucoseGraphObjects = generateGlucoseGraphPoints(isNewGraphEnabled);
        this.glucoseReadingsMonth = dB.getAverageGlucoseReadingsByMonth();
        this.glucoseReadingsWeek = dB.getAverageGlucoseReadingsByWeek();
        this.glucoseDatetimeWeek = dB.getGlucoseDatetimesByWeek();
        this.glucoseDatetimeMonth = dB.getGlucoseDatetimesByMonth();
        this.glucoseMaxValue = dB.getUser(1).getCustom_range_max();
        this.glucoseMinValue = dB.getUser(1).getCustom_range_min();
    }

    public String convertDate(String date) {
        return view.convertDate(date);
    }

    public String getHB1AC() {
        // Check if last month is available first
        if (getGlucoseReadingsMonth().size() > 1) {
            if ("percentage".equals(dB.getUser(1).getPreferred_unit_a1c())) {
                return GlucosioConverter.glucoseToA1C(getGlucoseReadingsMonth().get(getGlucoseReadingsMonth().size() - 2)) + " %";
            } else {
                return GlucosioConverter.a1cNgspToIfcc(GlucosioConverter.glucoseToA1C(getGlucoseReadingsMonth().get(getGlucoseReadingsMonth().size() - 2))) + " mmol/mol";
            }
        } else {
            return view.getString(R.string.overview_hb1ac_error_no_data);
        }
    }

    public boolean isA1cAvailable(int depth) {
        return getGlucoseReadingsMonth().size() > depth;
    }

    public ArrayList<A1cEstimate> getA1cEstimateList() {
        ArrayList<A1cEstimate> a1cEstimateList = new ArrayList<>();

        // We don't take this month because A1C is incomplete
        for (int i = 0; i < getGlucoseReadingsMonth().size() - 1; i++) {
            double value = GlucosioConverter.glucoseToA1C(getGlucoseReadingsMonth().get(i));
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
        return dB.getLastGlucoseReading().getReading() + "";
    }

    public String getLastDateTime() {
        java.text.DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

        return inputFormat.format(dB.getLastGlucoseReading().getCreated());
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
        for (int i = 0; i < glucoseGraphObjects.size(); i++) {
            glucoseReadings.add(glucoseGraphObjects.get(i).getReading());
        }

        return glucoseReadings;
    }

    public ArrayList<Double> getA1cReadings() {
        return dB.getHB1ACReadingAsArray();
    }

    public ArrayList<String> getA1cReadingsDateTime() {
        return dB.getHB1ACDateTimeAsArray();
    }

    public ArrayList<Double> getKetonesReadings() {
        final List<KetoneReading> ketoneReadings = dB.getKetoneReadings();
        ArrayList<Double> finalArrayList = new ArrayList<>();
        for (int i = 0; i < ketoneReadings.size(); i++) {
            finalArrayList.add(ketoneReadings.get(i).getReading());
        }
        return finalArrayList;
    }

    public ArrayList<String> getKetonesReadingsDateTime() {
        return dB.getKetoneDateTimeAsArray();
    }

    public ArrayList<Integer> getWeightReadings() {
        final List<WeightReading> weightReadings = dB.getWeightReadings();
        ArrayList<Integer> finalArrayList = new ArrayList<>();
        for (int i = 0; i < weightReadings.size(); i++) {
            finalArrayList.add(weightReadings.get(i).getReading());
        }
        return finalArrayList;
    }

    public ArrayList<String> getWeightReadingsDateTime() {
        return dB.getWeightReadingDateTimeAsArray();
    }

    public ArrayList<Integer> getMinPressureReadings() {
        final List<PressureReading> pressureReadings = dB.getPressureReadings();
        ArrayList<Integer> finalArrayList = new ArrayList<>();
        for (int i = 0; i < pressureReadings.size(); i++) {
            finalArrayList.add(pressureReadings.get(i).getMinReading());
        }
        return finalArrayList;
    }

    public ArrayList<Integer> getMaxPressureReadings() {
        final List<PressureReading> pressureReadings = dB.getPressureReadings();
        ArrayList<Integer> finalArrayList = new ArrayList<>();
        for (int i = 0; i < pressureReadings.size(); i++) {
            finalArrayList.add(pressureReadings.get(i).getMaxReading());
        }
        return finalArrayList;
    }

    public ArrayList<String> getPressureReadingsDateTime() {
        return dB.getPressureDateTimeAsArray();
    }

    public ArrayList<Integer> getCholesterolReadings() {
        final List<CholesterolReading> cholesterolReadings = dB.getCholesterolReadings();
        ArrayList<Integer> finalArrayList = new ArrayList<>();
        for (int i = 0; i < cholesterolReadings.size(); i++) {
            finalArrayList.add(cholesterolReadings.get(i).getTotalReading());
        }
        return finalArrayList;
    }

    public ArrayList<String> getCholesterolReadingsDateTime() {
        return dB.getCholesterolDateTimeAsArray();
    }

    private List<IntGraphObject> generateGlucoseGraphPoints(boolean isNewGraphEnabled) {
        final ArrayList<IntGraphObject> finalGraphObjects = new ArrayList<>();
        if (isNewGraphEnabled) {
            DateTime minDateTime = DateTime.now().minusMonths(1).minusDays(15);
            final List<GlucoseReading> glucoseReadings = dB.getLastMonthGlucoseReadings();

            Collections.sort(glucoseReadings, new Comparator<GlucoseReading>() {
                public int compare(GlucoseReading o1, GlucoseReading o2) {
                    return o1.getCreated().compareTo(o2.getCreated());
                }
            });

            DateTime startDate = glucoseReadings.size() > 0 ?
                    minDateTime : DateTime.now();
            // Transfer values from database to ArrayList as GlucoseGraphObjects
            for (int i = 0; i < glucoseReadings.size(); i++) {
                final GlucoseReading reading = glucoseReadings.get(i);
                final DateTime createdDate = new DateTime(reading.getCreated());
                //add zero values between current value and last added value
                addZeroReadings(finalGraphObjects, startDate, createdDate);
                //add new value
                finalGraphObjects.add(
                        new IntGraphObject(createdDate, reading.getReading())
                );
                //update start date
                startDate = createdDate;
            }
            //add last zeros till now
            addZeroReadings(finalGraphObjects, startDate, DateTime.now());
        } else {
            Collections.sort(glucoseReadings, new Comparator<GlucoseReading>() {
                public int compare(GlucoseReading o1, GlucoseReading o2) {
                    return o1.getCreated().compareTo(o2.getCreated());
                }
            });
            for (int i = 0; i < glucoseReadings.size(); i++) {
                GlucoseReading glucoseReading = glucoseReadings.get(i);
                finalGraphObjects.add(new IntGraphObject(new DateTime(glucoseReading.getCreated()), glucoseReading.getReading()));
            }
        }

        return finalGraphObjects;
    }

    private void addZeroReadings(final ArrayList<IntGraphObject> graphObjects,
                                 final DateTime firstDate,
                                 final DateTime lastDate) {
        int daysBetween = Days.daysBetween(firstDate, lastDate).getDays();
        for (int i = 1; i < daysBetween; i++) {
            graphObjects.add(new IntGraphObject(firstDate.plusDays(i), 0));
        }
    }

    public ArrayList<String> getGraphGlucoseDateTime() {
        ArrayList<String> glucoseDatetime = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

        for (int i = 0; i < glucoseGraphObjects.size(); i++) {
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
