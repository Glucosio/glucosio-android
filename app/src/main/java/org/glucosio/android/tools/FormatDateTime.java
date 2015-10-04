package org.glucosio.android.tools;

import android.content.Context;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatDateTime {

    Context context;
    public FormatDateTime(Context mContext){
        this.context= mContext;
    }

    public String convertDate(String date) {
        java.text.DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        String localPattern  = ((SimpleDateFormat)formatter).toLocalizedPattern();
        java.text.DateFormat finalDataFormat = new SimpleDateFormat(localPattern);
        java.text.DateFormat finalTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
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
}
