package org.glucosio.android.tools;

import java.util.Calendar;

/**Singleton Class to intiate start time and calculate the time elapsed
 * Created by Viney Ugave on 10/25/16.
 */

public class TimeElapsed {

    private static TimeElapsed _instance = null;
    private static long startTime;
    private static long endTime;

    protected TimeElapsed() {
        // Exists only to defeat instantiation.
    }

    public static TimeElapsed getInstance(){
        if(_instance == null){
            _instance = new TimeElapsed();
        }
        return _instance;
    }

    public static long getStartTime() {
        return startTime;
    }

    public static void setStartTime() {
        TimeElapsed.startTime = Calendar.getInstance().getTimeInMillis();
    }

    public static void resetTime(){
        TimeElapsed.startTime=0;
        TimeElapsed.endTime=0;
    }
    public static long getEndTime() {
        return endTime;
    }

    public static void setEndTime(long endTime) {
        TimeElapsed.endTime = endTime;
    }
}
