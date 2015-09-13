package org.glucosio.android.presenter;

import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.fragment.HistoryFragment;
import org.glucosio.android.fragment.OverviewFragment;
import org.glucosio.android.tools.ReadingTools;
import org.glucosio.android.tools.TipsManager;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by paolo on 10/09/15.
 */
public class OverviewPresenter {

    DatabaseHandler dB;
    private ArrayList<Integer> reading;
    private ArrayList <Integer> type;
    private ArrayList<String> datetime;

    public OverviewPresenter(OverviewFragment overviewFragment) {
        dB = new DatabaseHandler(overviewFragment.getActivity());
    }

    public boolean isdbEmpty(){
        return dB.getGlucoseReadings().size() == 0;
    }

    public void loadDatabase(){
        this.reading = dB.getGlucoseReadingAsArray();
        this.type = dB.getGlucoseTypeAsArray();
        this.datetime = dB.getGlucoseDateTimeAsArray();
    }

    public String convertDate(String date) {
        ReadingTools rTools = new ReadingTools();
        return rTools.convertDate(date);
    }

    public int getGlucoseTrend(){
        return dB.getAverageGlucoseReadingForLastMonth();
    }

    public String getLastReading(){
        return getReading().get(getReading().size() - 1) + "";
    }

    public String getRandomTip(TipsManager manager){
        ArrayList<String> tips = manager.getTips();

        // Get random tip from array
        int randomNumber = new Random().nextInt(tips.size());
        return tips.get(randomNumber);
    }

    public int getUserAge(){
        return dB.getUser(1).get_age();
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
