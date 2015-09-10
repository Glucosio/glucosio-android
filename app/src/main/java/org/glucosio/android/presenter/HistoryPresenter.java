package org.glucosio.android.presenter;

import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.fragment.HistoryFragment;
import org.glucosio.android.tools.ReadingTools;
import java.util.ArrayList;

public class HistoryPresenter {

    DatabaseHandler dB;
    private ArrayList<Integer> id;
    private ArrayList<Integer> reading;
    private ArrayList <Integer> type;
    private ArrayList<String> datetime;
    GlucoseReading readingToRestore;
    HistoryFragment fragment;

    public HistoryPresenter(HistoryFragment historyFragment) {
        this.fragment = historyFragment;
        dB = new DatabaseHandler(historyFragment.getActivity());
    }

    public boolean isdbEmpty(){
        return dB.getGlucoseReadings().size() == 0;
    }

    public void loadDatabase(){
        this.id = dB.getGlucoseIdAsArray();
        this.reading = dB.getGlucoseReadingAsArray();
        this.type = dB.getGlucoseTypeAsArray();
        this.datetime = dB.getGlucoseDateTimeAsArray();
    }


    public String convertDate(String date) {
        ReadingTools rTools = new ReadingTools();
        return rTools.convertDate(date);
    }

    public void onDeleteClicked(int idToDelete){
        readingToRestore = dB.getGlucoseReadingById(idToDelete);
        removeReadingFromDb(dB.getGlucoseReadingById(idToDelete));
        fragment.notifyAdapter();
        dB.addGlucoseReading(readingToRestore);
    }

    public void deleteReading(int idToDelete) {
        removeReadingFromDb(dB.getGlucoseReadingById(idToDelete));
        fragment.notifyAdapter();
    }

    public void onUndoClicked(){
        fragment.notifyAdapter();
    }

    private void removeReadingFromDb(GlucoseReading gReading) {
        dB.deleteGlucoseReadings(gReading);
        fragment.reloadFragmentAdapter();
        loadDatabase();
    }

    // Getters
    public ArrayList<Integer> getId() {
        return id;
    }

    public ArrayList<Integer> getReading() {
        return reading;
    }

    public ArrayList<Integer> getType() {
        return type;
    }

    public ArrayList<String> getDatetime() {
        return datetime;
    }
}
