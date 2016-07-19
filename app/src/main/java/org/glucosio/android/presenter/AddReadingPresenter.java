package org.glucosio.android.presenter;

import android.text.TextUtils;

import org.glucosio.android.tools.SplitDateTime;

import java.util.Calendar;
import java.util.Date;

public class AddReadingPresenter {

    private String readingYear;
    private String readingMonth;
    private String readingDay;
    private String readingHour;
    private String readingMinute;

    public void setReadingTimeNow() {
        Date formatted = Calendar.getInstance().getTime();
        SplitDateTime addSplitDateTime = new SplitDateTime(formatted);
        this.readingYear = addSplitDateTime.getYear();
        this.readingMonth = addSplitDateTime.getMonth();
        this.readingDay = addSplitDateTime.getDay();
        this.readingHour = addSplitDateTime.getHour();
        this.readingMinute = addSplitDateTime.getMinute();
    }

    public void updateReadingSplitDateTime(Date readingDate) {
        SplitDateTime splitDateTime = new SplitDateTime(readingDate);
        this.readingDay = splitDateTime.getDay();
        this.readingHour = splitDateTime.getHour();
        this.readingMinute = splitDateTime.getMinute();
        this.readingYear = splitDateTime.getYear();
        this.readingMonth = splitDateTime.getMonth();
    }

    public Date getReadingTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(readingYear), Integer.parseInt(readingMonth) - 1, Integer.parseInt(readingDay), Integer.parseInt(readingHour), Integer.parseInt(readingMinute));
        return cal.getTime();
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

    protected boolean validateText(String text) {
        return !TextUtils.isEmpty(text);
    }

    // Validator
    protected boolean validateTime(String time) {
        //TODO check if it can be empty or not valid in other way in different sdk
        return !TextUtils.isEmpty(time);
    }

    protected boolean validateDate(String date) {
        //TODO check if it can be empty or not valid in other way in different sdk
        return !TextUtils.isEmpty(date);
    }
}
