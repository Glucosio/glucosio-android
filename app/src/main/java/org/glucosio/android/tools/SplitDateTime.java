package org.glucosio.android.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SplitDateTime {

    Date origDateTime; // Example "yyyy-MM-dd HH:mm"
    DateFormat inputFormat;

    public SplitDateTime(Date origDatetime, DateFormat origDateFormat) {
        this.origDateTime = origDatetime;
        this.inputFormat = origDateFormat;
    }

    public String getHour(){
        DateFormat finalFormat = new SimpleDateFormat("HH");
        return finalFormat.format(origDateTime);
    }

    public String getMinute(){
        DateFormat finalFormat = new SimpleDateFormat("mm");
        return finalFormat.format(origDateTime);
    }

    public String getYear(){
        DateFormat finalFormat = new SimpleDateFormat("yyyy");
        return finalFormat.format(origDateTime);
    }

    public String getMonth(){
        DateFormat finalFormat = new SimpleDateFormat("MM");
        return finalFormat.format(origDateTime);
    }

    public String getDay(){
        DateFormat finalFormat = new SimpleDateFormat("dd");
        return finalFormat.format(origDateTime);
    }
}
