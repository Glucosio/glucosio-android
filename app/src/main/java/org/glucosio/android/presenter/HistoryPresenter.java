package org.glucosio.android.presenter;

import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.fragment.HistoryFragment;

import java.util.ArrayList;

public class HistoryPresenter {

    private DatabaseHandler dB;
    private HistoryFragment fragment;

    public HistoryPresenter(HistoryFragment historyFragment) {
        this.fragment = historyFragment;
        dB = new DatabaseHandler(historyFragment.getContext());
    }

    public boolean isdbEmpty(){
        return dB.getGlucoseReadings().size() == 0;
    }


    public String convertDate(String date){
        return fragment.convertDate(date);
    }

    public void onDeleteClicked(long idToDelete, int metricID){
        switch (metricID) {
            // Glucose
            case 0:
                dB.deleteGlucoseReadings(dB.getGlucoseReading(idToDelete));
                fragment.reloadFragmentAdapter();
                break;
            // HB1AC
            case 1:
                dB.deleteHB1ACReadingReading(dB.getHB1ACReading(idToDelete));
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
                dB.deleteKetoneReading(dB.getKetoneReading(idToDelete));
                fragment.reloadFragmentAdapter();
                break;
            // Weight
            case 5:
                dB.deleteWeightReading(dB.getWeightReading(idToDelete));
                fragment.reloadFragmentAdapter();
                break;
            default:
                break;
        }
        fragment.notifyAdapter();
        fragment.updateToolbarBehaviour();
    }

    // Getters
    public String getUnitMeasuerement(){
        return dB.getUser(1).getPreferred_unit();
    }

    public ArrayList<Long> getGlucoseId() {
        return dB.getGlucoseIdAsArray();
    }

    public ArrayList<String> getGlucoseReadingType() {
        return dB.getGlucoseTypeAsArray();
    }

    public ArrayList<Integer> getGlucoseReading() {
        return dB.getGlucoseReadingAsArray();
    }

    public ArrayList<String> getGlucoseDateTime() {
        return dB.getGlucoseDateTimeAsArray();
    }

    public int getGlucoseReadingsNumber(){
        return dB.getGlucoseReadingAsArray().size();
    }

    public ArrayList<Double> getKetoneReading() {
        return dB.getKetoneReadingAsArray();
    }

    public ArrayList<String> getKetoneDateTime() {
        return dB.getKetoneDateTimeAsArray();
    }

    public ArrayList<Long> getKetoneId() {
        return dB.getKetoneIdAsArray();
    }

    public int getKetoneReadingsNumber(){
        return dB.getKetoneDateTimeAsArray().size();
    }

    public ArrayList<Long> getCholesterolId() {
        return dB.getCholesterolIdAsArray();
    }

    public ArrayList<String> getCholesterolDateTime() {
        return dB.getCholesterolDateTimeAsArray();
    }

    public ArrayList<Integer> getHDLCholesterolReading() {
        return dB.getHDLCholesterolReadingAsArray();
    }

    public ArrayList<Integer> getLDLCholesterolReading() {
        return dB.getLDLCholesterolReadingAsArray();
    }

    public ArrayList<Integer> getTotalCholesterolReading() {
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

    public int getHB1ACReadingsNumber(){
        return dB.getHB1ACReadingAsArray().size();
    }

    public int getPressureReadings(){
        return dB.getPressureReadings().size();
    }

    public ArrayList<String> getPressureDateTime() {
        return dB.getPressureDateTimeAsArray();
    }

    public ArrayList<Long> getPressureId() {
        return dB.getPressureIdAsArray();
    }

    public ArrayList<Integer> getMinPressureReading() {
        return dB.getMinPressureReadingAsArray();
    }

    public ArrayList<Integer> getMaxPressureReading() {
        return dB.getMaxPressureReadingAsArray();
    }

    public int getPressureReadingsNumber(){
        return dB.getPressureIdAsArray().size();
    }

    public ArrayList<Integer> getWeightReadings(){
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
