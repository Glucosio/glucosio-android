package org.glucosio.android.tools;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FormatDateTime {

    private Context context;

    public FormatDateTime(Context mContext) {
        this.context = mContext;
    }

    public String convertDateTime(String date) {
        java.text.DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        java.text.DateFormat finalDataFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        java.text.DateFormat finalTimeFormat;

        if (android.text.format.DateFormat.is24HourFormat(context)) {
            finalTimeFormat = new SimpleDateFormat("HH:mm");
        } else {
            finalTimeFormat = new SimpleDateFormat("hh:mm a");
        }

        Date parsed = null;
        try {
            parsed = inputFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String finalData = finalDataFormat.format(parsed);
        String finalTime = finalTimeFormat.format(parsed);
        return finalData + " " + finalTime;
    }

    public String convertDateToMonthOverview(String date) {
        java.text.DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        java.text.DateFormat finalDataFormat = new SimpleDateFormat("MMMM");

        Date parsed = null;
        try {
            parsed = inputFormat.parse(date);
            // Because database's average is the end of the month
            // we need to remove 1 month from final date
            Calendar cal = Calendar.getInstance();
            cal.setTime(parsed);
            cal.add(Calendar.MONTH, -1);
            parsed = cal.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String finalData = finalDataFormat.format(parsed);
        return finalData + " ";
    }


    public String convertDate(String datetime) {
        java.text.DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.text.DateFormat finalDataFormat = DateFormat.getDateInstance(DateFormat.SHORT);

        Date parsed = null;
        try {
            parsed = inputFormat.parse(datetime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String finalData = finalDataFormat.format(parsed);
        return finalData + "";
    }

    public String convertRawDate(String datetime) {
        java.text.DateFormat inputFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
        java.text.DateFormat finalDataFormat = DateFormat.getDateInstance(DateFormat.SHORT);

        Date parsed = null;
        try {
            parsed = inputFormat.parse(datetime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String finalData = finalDataFormat.format(parsed);
        return finalData + "";
    }

    public String convertRawTime(String datetime) {
        java.text.DateFormat inputFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
        java.text.DateFormat finalTimeFormat;

        if (android.text.format.DateFormat.is24HourFormat(context)) {
            finalTimeFormat = new SimpleDateFormat("HH:mm");
        } else {
            finalTimeFormat = new SimpleDateFormat("hh:mm a");
        }

        Date parsed = null;
        try {
            parsed = inputFormat.parse(datetime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String finalTime = finalTimeFormat.format(parsed);
        return finalTime + "";
    }

    public String getCurrentTime() {
        Calendar cal = Calendar.getInstance();

        java.text.DateFormat finalTimeFormat;

        finalTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);


        String finalTime = finalTimeFormat.format(cal.getTime());
        return finalTime + "";
    }

    public String getCurrentDate() {
        Calendar cal = Calendar.getInstance();

        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);

        String finalTime = dateFormat.format(cal.getTime());
        return finalTime + "";
    }

    public String getTime(Calendar cal) {
        java.text.DateFormat finalTimeFormat;

        finalTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);


        String finalTime = finalTimeFormat.format(cal.getTime());
        return finalTime + "";
    }
}
