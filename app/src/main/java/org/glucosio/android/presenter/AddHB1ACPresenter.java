package org.glucosio.android.presenter;

import org.glucosio.android.activity.AddHB1ACActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.HB1ACReading;
import org.glucosio.android.tools.SplitDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddHB1ACPresenter {
    private DatabaseHandler dB;
    private AddHB1ACActivity activity;
    private String readingYear;
    private String readingMonth;
    private String readingDay;
    private String readingHour;
    private String readingMinute;


    public AddHB1ACPresenter(AddHB1ACActivity addHB1ACActivity) {
        this.activity= addHB1ACActivity;
        dB = new DatabaseHandler(addHB1ACActivity.getApplicationContext());
    }


    public void getCurrentTime(){
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date formatted = Calendar.getInstance().getTime();

        SplitDateTime addSplitDateTime = new SplitDateTime(formatted, inputFormat);

        this.readingYear = addSplitDateTime.getYear();
        this.readingMonth = addSplitDateTime.getMonth();
        this.readingDay = addSplitDateTime.getDay();
        this.readingHour = addSplitDateTime.getHour();
        this.readingMinute = addSplitDateTime.getMinute();
    }

    public void dialogOnAddButtonPressed(String time, String date, String reading){
        if (validateEmpty(date) && validateEmpty(time) && validateEmpty(reading)) {
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(readingYear), Integer.parseInt(readingMonth)-1, Integer.parseInt(readingDay), Integer.parseInt(readingHour), Integer.parseInt(readingMinute));
            Date finalDateTime = cal.getTime();
            double finalReading = Double.parseDouble(reading);
            HB1ACReading hReading = new HB1ACReading(finalReading, finalDateTime);

            dB.addHB1ACReading(hReading);
            activity.finishActivity();
        } else {
            activity.showErrorMessage();
        }
    }

    private boolean validateEmpty(String time){
        return !"".equals(time);
    }

    // Getters and Setters

    public String getUnitMeasuerement(){
        return dB.getUser(1).getPreferred_unit();
    }

    public String getReadingYear() {
        return readingYear;
    }

    public String getReadingMonth() {
        return readingMonth;
    }

    public void setReadingYear(String readingYear) {
        this.readingYear = readingYear;
    }

    public void setReadingMonth(String readingMonth) {
        this.readingMonth = readingMonth;
    }

    public void setReadingDay(String readingDay) {
        this.readingDay = readingDay;
    }

    public void setReadingHour(String readingHour) {
        this.readingHour = readingHour;
    }

    public void setReadingMinute(String readingMinute) {
        this.readingMinute = readingMinute;
    }

}
