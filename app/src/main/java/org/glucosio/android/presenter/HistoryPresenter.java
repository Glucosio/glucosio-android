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

import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.fragment.HistoryFragment;

import java.util.ArrayList;
import java.util.List;

public class HistoryPresenter {

    private DatabaseHandler dB;
    private HistoryFragment fragment;

    public HistoryPresenter(HistoryFragment historyFragment) {
        this.fragment = historyFragment;
        dB = new DatabaseHandler(historyFragment.getContext());
    }

    public boolean isdbEmpty() {
        return dB.getGlucoseReadings().size() == 0;
    }


    public String convertDate(String date) {
        return fragment.convertDate(date);
    }

    public void onDeleteClicked(long idToDelete, int metricID) {
        switch (metricID) {
            // Glucose
            case 0:
                dB.deleteGlucoseReading(dB.getGlucoseReadingById(idToDelete));
                fragment.reloadFragmentAdapter();
                break;
            // HB1AC
            case 1:
                dB.deleteHB1ACReading(dB.getHB1ACReadingById(idToDelete));
                fragment.reloadFragmentAdapter();
                break;
            // Cholesterol
            case 2:
                dB.deleteCholesterolReading(dB.getCholesterolReading(idToDelete));
                fragment.reloadFragmentAdapter();
                break;
            // Pressure
            case 3:
                dB.deletePressureReading(dB.getPressureReading(idToDelete));
                fragment.reloadFragmentAdapter();
                break;
            //Ketones
            case 4:
                dB.deleteKetoneReading(dB.getKetoneReadingById(idToDelete));
                fragment.reloadFragmentAdapter();
                break;
            // Weight
            case 5:
                dB.deleteWeightReading(dB.getWeightReadingById(idToDelete));
                fragment.reloadFragmentAdapter();
                break;
            default:
                break;
        }
        fragment.notifyAdapter();
        fragment.updateToolbarBehaviour();
    }

    // Getters
    public String getUnitMeasuerement() {
        return dB.getUser(1).getPreferred_unit();
    }

    public String getWeightUnitMeasurement() {
        return dB.getUser(1).getPreferred_unit_weight();
    }

    public String getA1cUnitMeasurement() {
        return dB.getUser(1).getPreferred_unit_a1c();
    }

    public List<Long> getGlucoseId() {
        return dB.getGlucoseIdAsList();
    }

    public List<String> getGlucoseReadingType() {
        return dB.getGlucoseTypeAsList();
    }

    public List<String> getGlucoseNotes() {
        return dB.getGlucoseNotesAsList();
    }

    public List<Double> getGlucoseReading() {
        return dB.getGlucoseReadingAsList();
    }

    public List<String> getGlucoseDateTime() {
        return dB.getGlucoseDateTimeAsList();
    }

    public int getGlucoseReadingsNumber() {
        return dB.getGlucoseReadingAsList().size();
    }

    public List<Double> getKetoneReading() {
        return dB.getKetoneReadingAsArray();
    }

    public ArrayList<String> getKetoneDateTime() {
        return dB.getKetoneDateTimeAsArray();
    }

    public ArrayList<Long> getKetoneId() {
        return dB.getKetoneIdAsArray();
    }

    public int getKetoneReadingsNumber() {
        return dB.getKetoneDateTimeAsArray().size();
    }

    public ArrayList<Long> getCholesterolId() {
        return dB.getCholesterolIdAsArray();
    }

    public ArrayList<String> getCholesterolDateTime() {
        return dB.getCholesterolDateTimeAsArray();
    }

    public List<Double> getHDLCholesterolReading() {
        return dB.getHDLCholesterolReadingAsArray();
    }

    public List<Double> getLDLCholesterolReading() {
        return dB.getLDLCholesterolReadingAsArray();
    }

    public List<Double> getTotalCholesterolReading() {
        return dB.getTotalCholesterolReadingAsArray();
    }

    public int getCholesterolReadingsNumber() {
        return dB.getCholesterolIdAsArray().size();
    }

    public ArrayList<Long> getHB1ACId() {
        return dB.getHB1ACIdAsArray();
    }

    public ArrayList<String> getHB1ACDateTime() {
        return dB.getHB1ACDateTimeAsArray();
    }

    public ArrayList<Double> getHB1ACReading() {
        return dB.getHB1ACReadingAsArray();
    }

    public int getHB1ACReadingsNumber() {
        return dB.getHB1ACReadingAsArray().size();
    }

    public int getPressureReadings() {
        return dB.getPressureReadings().size();
    }

    public ArrayList<String> getPressureDateTime() {
        return dB.getPressureDateTimeAsArray();
    }

    public ArrayList<Long> getPressureId() {
        return dB.getPressureIdAsArray();
    }

    public List<Double> getMinPressureReading() {
        return dB.getMinPressureReadingAsArray();
    }

    public List<Double> getMaxPressureReading() {
        return dB.getMaxPressureReadingAsArray();
    }

    public int getPressureReadingsNumber() {
        return dB.getPressureIdAsArray().size();
    }

    public List<Double> getWeightReadings() {
        return dB.getWeightReadingAsArray();
    }

    public ArrayList<String> getWeightDateTime() {
        return dB.getWeightReadingDateTimeAsArray();
    }

    public ArrayList<Long> getWeightId() {
        return dB.getWeightIdAsArray();
    }

    public int getWeightReadingsNumber() {
        return dB.getWeightIdAsArray().size();
    }
}
