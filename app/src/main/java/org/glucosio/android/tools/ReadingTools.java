package org.glucosio.android.tools;

import android.content.Context;

import org.glucosio.android.R;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReadingTools {

    public ReadingTools(){
    }

        public int hourToSpinnerType(int hour) {

            if (hour > 23) {
                return 8;  //night
            } else if (hour > 20) {
                return 5; //after dinner
            } else if (hour > 17) {
                return 4; // before dinner
            } else if (hour > 13) {
                return 3; // after lunch
            } else if (hour > 11) {
                return 2; // before lunch
            } else if (hour > 7) {
                return 1; //after breakfast
            } else if (hour > 4) {
                return 0; // before breakfast
            } else {
                return 8; // night time
            }
        }
}
