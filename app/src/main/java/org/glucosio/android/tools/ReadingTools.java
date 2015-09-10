package org.glucosio.android.tools;

import android.content.Context;

import org.glucosio.android.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReadingTools {

    public ReadingTools(){
    }

    public String convertDate(String date) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateFormat outputFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm");
        Date parsed = null;
        try {
            parsed = inputFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return outputFormat.format(parsed);
    }

    public int hourToSpinnerType(int hour) {

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
}
