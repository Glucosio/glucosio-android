package org.glucosio.android.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SplitDateTime {

    String origDateTime; // Example "yyyy-MM-dd HH:mm"
    DateFormat inputFormat;

    public SplitDateTime(String origDatetime, DateFormat origDateFormat) {
        this.origDateTime = origDatetime;
        this.inputFormat = origDateFormat;
    }

    public String getHour(){
        DateFormat finalFormat = new SimpleDateFormat("HH");

        Date parsed = null;
        try {
            parsed = inputFormat.parse(origDateTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return finalFormat.format(parsed);
    }

    public String getMinute(){
        DateFormat finalFormat = new SimpleDateFormat("mm");

        Date parsed = null;
        try {
            parsed = inputFormat.parse(origDateTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return finalFormat.format(parsed);
    }

    public String getYear(){
        DateFormat finalFormat = new SimpleDateFormat("yyyy");

        Date parsed = null;
        try {
            parsed = inputFormat.parse(origDateTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return finalFormat.format(parsed);
    }

    public String getMonth(){
        DateFormat finalFormat = new SimpleDateFormat("MM");

        Date parsed = null;
        try {
            parsed = inputFormat.parse(origDateTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return finalFormat.format(parsed);
    }

    public String getDay(){
        DateFormat finalFormat = new SimpleDateFormat("dd");

        Date parsed = null;
        try {
            parsed = inputFormat.parse(origDateTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return finalFormat.format(parsed);
    }
}
