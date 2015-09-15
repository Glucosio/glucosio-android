package org.glucosio.android.presenter;

import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.db.User;
import org.glucosio.android.tools.ReadingTools;
import org.glucosio.android.tools.SplitDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainPresenter {

    MainActivity mainActivity;

    DatabaseHandler dB;
    User user;
    ReadingTools rTools;
    int age;

    private String readingYear;
    private String readingMonth;
    private String readingDay;
    private String readingHour;
    private String readingMinute;

    public MainPresenter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        dB = new DatabaseHandler(mainActivity);
        if (dB.getUser(1) == null){
            mainActivity.startHelloActivity();
        } else {
            user = dB.getUser(1);
            age = user.get_age();
            rTools = new ReadingTools();

        }
    }

    public void updateSpinnerTypeTime() {
        getCurrentTime();
        mainActivity.updateSpinnerTypeTime(timeToSpinnerType());
    }

    public void getCurrentTime(){
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formatted = inputFormat.format(Calendar.getInstance().getTime());
        SplitDateTime addSplitDateTime = new SplitDateTime(formatted, inputFormat);

        this.readingYear = addSplitDateTime.getYear();
        this.readingMonth = addSplitDateTime.getMonth();
        this.readingDay = addSplitDateTime.getDay();
        this.readingHour = addSplitDateTime.getHour();
        this.readingMinute = addSplitDateTime.getMinute();
    }

    public int timeToSpinnerType() {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formatted = inputFormat.format(Calendar.getInstance().getTime());
        SplitDateTime addSplitDateTime = new SplitDateTime(formatted, inputFormat);
        int hour = Integer.parseInt(addSplitDateTime.getHour());

        if (hour > 4 && hour <= 7 ){
            return 0;
        } else if (hour > 7 && hour <= 11){
            return 1;
        } else if (hour > 11 && hour <= 13) {
            return 2;
        } else if (hour > 13 && hour <= 17) {
            return 3;
        } else if (hour > 17 && hour <= 20) {
            return 4;
        } else if (hour > 20 && hour <= 4) {
            return 5;
        } else {
            return 0;
        }
    }

    public int hourToSpinnerType(int hour){
        return rTools.hourToSpinnerType(hour);
    }

    public String getGlucoseReadingReadingById(int id){
        return dB.getGlucoseReadingById(id).get_reading() + "";
    }

    public int getGlucoseReadingTypeById(int id){
        return dB.getGlucoseReadingById(id).get_reading_type();
    }

    public void getGlucoseReadingTimeById(int id){
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SplitDateTime splitDateTime = new SplitDateTime(dB.getGlucoseReadingById(id).get_created(), inputFormat);
        this.readingYear = splitDateTime.getYear();
        this.readingMonth = splitDateTime.getMonth();
        this.readingDay = splitDateTime.getDay();
        this.readingHour = splitDateTime.getHour();
        this.readingMinute = splitDateTime.getMinute();
    }

    public void dialogOnAddButtonPressed(String time, String date, String reading, int type){
        if (validateDate(date) && validateTime(time) && validateReading(reading)) {
            int finalReading = Integer.parseInt(reading);
            String finalDateTime = readingYear + "-" + readingMonth + "-" + readingDay + " " + readingHour + ":" + readingMinute;

            GlucoseReading gReading = new GlucoseReading(finalReading, type, finalDateTime);
            dB.addGlucoseReading(gReading);
            mainActivity.dismissAddDialog();
        } else {
            mainActivity.showErrorMessage();
        }
    }

    public void dialogOnEditButtonPressed(String time, String date, String reading, int type, int id){
        if (validateDate(date) && validateTime(time) && validateReading(reading)) {
            int finalReading = Integer.parseInt(reading);
            String finalDateTime = readingYear + "-" + readingMonth + "-" + readingDay + " " + readingHour + ":" + readingMinute;

            GlucoseReading gReadingToDelete = dB.getGlucoseReadingById(id);
            GlucoseReading gReading = new GlucoseReading(finalReading, type, finalDateTime);

            dB.deleteGlucoseReadings(gReadingToDelete);
            dB.addGlucoseReading(gReading);

            mainActivity.dismissAddDialog();
        } else {
            mainActivity.showErrorMessage();
        }
    }

    public void startGittyReporter(){

    }

    private boolean validateTime(String time){
        return !time.equals("");
    }
    private boolean validateDate(String date){
        return !date.equals("");
    }
    private boolean validateReading(String reading){
        return !reading.equals("");
    }

    // Getters and Setters
    public String getReadingYear() {
        return readingYear;
    }

    public String getReadingMonth() {
        return readingMonth;
    }

    public String getReadingDay() {
        return readingDay;
    }

    public String getReadingHour() {
        return readingHour;
    }

    public String getReadingMinute() {
        return readingMinute;
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
