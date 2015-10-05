package org.glucosio.android.presenter;

import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.fragment.OverviewFragment;
import org.glucosio.android.tools.ReadingTools;
import org.glucosio.android.tools.TipsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OverviewPresenter {

    private DatabaseHandler dB;
    private ArrayList<Integer> reading;
    private ArrayList <String> type;
    private ArrayList<String> datetime;
    private List<GlucoseReading> readingsWeek;
    private List<GlucoseReading> readingsMonth;
    private OverviewFragment fragment;


    public OverviewPresenter(OverviewFragment overviewFragment) {
        dB = new DatabaseHandler();
        this.fragment = overviewFragment;
    }

    public boolean isdbEmpty(){
        return dB.getGlucoseReadings().size() == 0;
    }

    public void loadDatabase(){
        this.reading = dB.getGlucoseReadingAsArray();
        this.readingsMonth = dB.getAverageGlucoseReadingsByMonth();
        this.readingsWeek = dB.getAverageGlucoseReadingsByWeek();
        this.type = dB.getGlucoseTypeAsArray();
        this.datetime = dB.getGlucoseDateTimeAsArray();
    }

    public String convertDate(String date) {
        ReadingTools rTools = new ReadingTools();
        return fragment.convertDate(date);
    }

/*    public int getGlucoseTrend(){
        return dB.getAverageGlucoseReadingForLastMonth();
    }*/

    public String getLastReading(){
        return getReading().get(getReading().size() - 1) + "";
    }

    public String getRandomTip(TipsManager manager){
        ArrayList<String> tips = manager.getTips();

        // Get random tip from array
        int randomNumber = new Random().nextInt(tips.size());
        return tips.get(randomNumber);
    }

    public String getUnitMeasuerement(){
        return dB.getUser(1).get_preferred_unit();
    }

    public int getUserAge(){
        return dB.getUser(1).get_age();
    }

    public ArrayList<Integer> getReading() {
        return reading;
    }

    public ArrayList<String> getType() {
        return type;
    }

    public ArrayList<String> getDatetime() {
        return datetime;
    }

    public List<GlucoseReading> getReadingsWeek() {
        return readingsWeek;
    }

    public List<GlucoseReading> getReadingsMonth() {
        return readingsMonth;
    }
}
