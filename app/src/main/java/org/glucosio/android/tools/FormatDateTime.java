package org.glucosio.android.tools;

import android.content.Context;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatDateTime {

    Context context;
    public FormatDateTime(Context mContext){
        this.context= mContext;
    }

    public String convertDate(String date) {
        java.text.DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parsed = null;
        try {
            parsed = inputFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return DateFormat.getDateTimeInstance().format(parsed);
    }
}
