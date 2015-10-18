package org.glucosio.android.presenter;

import android.util.Log;

import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.db.User;
import org.glucosio.android.tools.GlucoseConverter;
import org.glucosio.android.tools.ReadingTools;
import org.glucosio.android.tools.SplitDateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainPresenter {

    private MainActivity mainActivity;

    private DatabaseHandler dB;
    private User user;
    private ReadingTools rTools;
    private GlucoseConverter converter;
    private int age;

    private String readingYear;
    private String readingMonth;
    private String readingDay;
    private String readingHour;
    private String readingMinute;

    public MainPresenter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        dB = new DatabaseHandler(mainActivity.getApplicationContext());
        Log.i("msg::","initiated db object");
        if (dB.getUser(1) == null){
            // if user exists start hello activity
            mainActivity.startHelloActivity();
        } else {
            //creating  a nrw user
            user = dB.getUser(1);
            age = user.getAge();
            rTools = new ReadingTools();
            converter = new GlucoseConverter();
        }
    }

    public boolean isdbEmpty(){
        return dB.getGlucoseReadings().size() == 0;
    }

    public void updateSpinnerTypeTime() {
        getCurrentTime();
        mainActivity.updateSpinnerTypeTime(timeToSpinnerType());
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

    public int timeToSpinnerType() {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date formatted = Calendar.getInstance().getTime();

        SplitDateTime addSplitDateTime = new SplitDateTime(formatted, inputFormat);
        int hour = Integer.parseInt(addSplitDateTime.getHour());

        return hourToSpinnerType(hour);
    }

    public int hourToSpinnerType(int hour){
        return rTools.hourToSpinnerType(hour);
    }

    public String getGlucoseReadingReadingById(int id){
        return dB.getGlucoseReadingById(id).getReading() + "";
    }

    public String getGlucoseReadingTypeById(int id){
        return dB.getGlucoseReadingById(id).getReading_type();
    }

    public void getGlucoseReadingTimeById(int id){
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SplitDateTime splitDateTime = new SplitDateTime(dB.getGlucoseReadingById(id).getCreated(), inputFormat);
        this.readingYear = splitDateTime.getYear();
        this.readingMonth = splitDateTime.getMonth();
        this.readingDay = splitDateTime.getDay();
        this.readingHour = splitDateTime.getHour();
        this.readingMinute = splitDateTime.getMinute();
    }

    public void dialogOnAddButtonPressed(String time, String date, String reading, String type){
        if (validateDate(date) && validateTime(time) && validateReading(reading) && validateType(type)) {

            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(readingYear), Integer.parseInt(readingMonth)-1, Integer.parseInt(readingDay), Integer.parseInt(readingHour), Integer.parseInt(readingMinute));
            Date finalDateTime = cal.getTime();

            if (getUnitMeasuerement().equals("mg/dL")) {
                int finalReading = Integer.parseInt(reading);
                GlucoseReading gReading = new GlucoseReading(finalReading, type, finalDateTime, "");
                dB.addGlucoseReading(gReading);
            } else {
                int convertedReading = converter.toMgDl(Double.parseDouble(reading));
                GlucoseReading gReading = new GlucoseReading(convertedReading, type, finalDateTime, "");
                dB.addGlucoseReading(gReading);
            }
            mainActivity.dismissAddDialog();
        } else {
            mainActivity.showErrorMessage();
        }
    }

    public void dialogOnEditButtonPressed(String time, String date, String reading, String type, int id){
        if (validateDate(date) && validateTime(time) && validateReading(reading)) {
            int finalReading = Integer.parseInt(reading);
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(readingYear),Integer.parseInt(readingMonth)-1,Integer.parseInt(readingDay),Integer.parseInt(readingHour),Integer.parseInt(readingMinute));
            Date finalDateTime = cal.getTime();

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

    private boolean validateReading(String reading) {
        if (getUnitMeasuerement().equals("mg/dL")) {
            // We store data in db in mg/dl
            try {
                Integer readingValue = Integer.parseInt(reading);
                if (readingValue > 19 && readingValue < 601) {
                //TODO: Add custom ranges
                    // TODO: Convert range in mmol/L
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        } else {
/*            try {
                //TODO: Add custom ranges for mmol/L
                Integer readingValue = Integer.parseInt(reading);
                if (readingValue > 19 && readingValue < 601) {
                    // TODO: Convert range in mmol/L
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }*/
            // TODO: return always true: we don't have ranges yet.
            return true;
        }
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
