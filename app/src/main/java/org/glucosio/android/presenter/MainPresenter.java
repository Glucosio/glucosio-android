package org.glucosio.android.presenter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.glucosio.android.R;
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
        dB = DatabaseHandler.getInstance(mainActivity);
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

        return hourToSpinnerType(hour);
    }

    public int hourToSpinnerType(int hour){
        return rTools.hourToSpinnerType(hour);
    }

    public String getGlucoseReadingReadingById(int id){
        return dB.getGlucoseReadingById(id).get_reading() + "";
    }

    public String getGlucoseReadingTypeById(int id){
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

    public void dialogOnAddButtonPressed(String time, String date, String reading, String type){
        if (validateDate(date) && validateTime(time) && validateReading(reading) && validateType(type)) {
            int finalReading = Integer.parseInt(reading);
            String finalDateTime = readingYear + "-" + readingMonth + "-" + readingDay + " " + readingHour + ":" + readingMinute;

            GlucoseReading gReading = new GlucoseReading(finalReading, type, finalDateTime,"");
            dB.addGlucoseReading(gReading);
            mainActivity.dismissAddDialog();
        } else {
            mainActivity.showErrorMessage();
        }
    }

    public void dialogOnEditButtonPressed(String time, String date, String reading, String type, int id){
        if (validateDate(date) && validateTime(time) && validateReading(reading)) {
            int finalReading = Integer.parseInt(reading);
            String finalDateTime = readingYear + "-" + readingMonth + "-" + readingDay + " " + readingHour + ":" + readingMinute;

            GlucoseReading gReadingToDelete = dB.getGlucoseReadingById(id);
            GlucoseReading gReading = new GlucoseReading(finalReading, type, finalDateTime,"");

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
    private boolean validateType(String type){
        return !type.equals("");
    }

    public static int VALIDATE_READING_MM_MIN = 1;
    public static int VALIDATE_READING_MM_MAX = 40;
    public static int VALIDATE_READING_MG_MIN = 19;
    public static int VALIDATE_READING_MG_MAX = 601;
    public static int VALIDATE_READING_MIN = VALIDATE_READING_MG_MIN;
    public static int VALIDATE_READING_MAX = VALIDATE_READING_MG_MAX;

    private void prepareValidateReadingRage(){
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        // TODO: Move to strings reading
        String currValue = appPreferences.getString("pref_unit",mainActivity.getResources().getString(R.string.helloactivity_spinner_preferred_unit_1));
        if(currValue.equals(mainActivity.getResources().getString(R.string.helloactivity_spinner_preferred_unit_2))) {
            VALIDATE_READING_MIN = VALIDATE_READING_MM_MIN;
            VALIDATE_READING_MAX = VALIDATE_READING_MM_MAX;
        }
    }

    private boolean validateReading(String reading) {
        // TODO: Must be calling once
        prepareValidateReadingRage();
        try {
            Integer readingValue = Integer.parseInt(reading);
            if (readingValue > VALIDATE_READING_MIN && readingValue < VALIDATE_READING_MM_MAX) { //valid range is 20-600
                // TODO: Convert range in mmol/L
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
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
