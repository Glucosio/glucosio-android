package org.glucosio.android.presenter;

import org.glucosio.android.activity.AddPressureActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.PressureReading;
import org.glucosio.android.tools.SplitDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddPressurePresenter {
    private DatabaseHandler dB;
    private AddPressureActivity activity;
    private String readingYear;
    private String readingMonth;
    private String readingDay;
    private String readingHour;
    private String readingMinute;


    public AddPressurePresenter(AddPressureActivity addPressureActivity) {
        this.activity = addPressureActivity;
        dB = new DatabaseHandler(addPressureActivity.getApplicationContext());
    }


    public void getCurrentTime() {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date formatted = Calendar.getInstance().getTime();

        SplitDateTime addSplitDateTime = new SplitDateTime(formatted, inputFormat);

        this.readingYear = addSplitDateTime.getYear();
        this.readingMonth = addSplitDateTime.getMonth();
        this.readingDay = addSplitDateTime.getDay();
        this.readingHour = addSplitDateTime.getHour();
        this.readingMinute = addSplitDateTime.getMinute();
    }

    public void dialogOnAddButtonPressed(String time, String date, String minReading, String maxReading) {
        if (validateEmpty(date) && validateEmpty(time) && validateEmpty(minReading) && validateEmpty(maxReading)) {
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(readingYear), Integer.parseInt(readingMonth) - 1, Integer.parseInt(readingDay), Integer.parseInt(readingHour), Integer.parseInt(readingMinute));
            Date finalDateTime = cal.getTime();
            int minFinalReading = Integer.parseInt(minReading);
            int maxFinalReading = Integer.parseInt(maxReading);
            PressureReading pReading = new PressureReading(minFinalReading, maxFinalReading, finalDateTime);

            dB.addPressureReading(pReading);
            activity.finishActivity();
        } else {
            activity.showErrorMessage();
        }
    }

    private boolean validateEmpty(String time) {
        return !time.equals("");
    }

    // Getters and Setters

    public String getUnitMeasuerement() {
        return dB.getUser(1).getPreferred_unit();
    }

    public String getReadingYear() {
        return readingYear;
    }

    public void setReadingYear(String readingYear) {
        this.readingYear = readingYear;
    }

    public String getReadingMonth() {
        return readingMonth;
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
