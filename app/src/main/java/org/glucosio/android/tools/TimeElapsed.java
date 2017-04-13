package org.glucosio.android.tools;


/**Singleton Class to initiate start time and calculate the time elapsed
 * Created on 10/25/16
 * @author Viney Ugave (viney@vinzzz.com)
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
        TimeElapsed.startTime = System.currentTimeMillis();
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
