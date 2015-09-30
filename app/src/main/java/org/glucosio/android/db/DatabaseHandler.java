package org.glucosio.android.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class DatabaseHandler {

    public DatabaseHandler() {
    }

    public void addUser(User user) {
        User newUser = user;
        newUser.save();
    }

    public User getUser(int id) {
        return User.getUser(id);
    }

    public void updateUser(User user) {
        User newUser = user;
        newUser.save();
    }

    public void addGlucoseReading(GlucoseReading reading) {
        GlucoseReading newGlucoseReading = reading;
        newGlucoseReading.save();
    }

    public void deleteGlucoseReadings(GlucoseReading reading) {
        GlucoseReading glucoseReading = reading;
        reading.delete();
    }

    public List<GlucoseReading> getGlucoseReadings() {
        return GlucoseReading.getAllGlucoseReading();
    }

    public List<GlucoseReading> getGlucoseReadings(String where) {
        return GlucoseReading.getGlucoseReadings(where);
    }

    public ArrayList<Long> getGlucoseIdAsArray(){
        List<GlucoseReading> glucoseReading = getGlucoseReadings();
        ArrayList<Long> idArray = new ArrayList<Long>();
        int i;

        for (i = 0; i < glucoseReading.size(); i++){
            long id;
            GlucoseReading singleReading= glucoseReading.get(i);
            id = singleReading.get_id();
            idArray.add(id);
        }

        return idArray;
    }

    public ArrayList<Integer> getGlucoseReadingAsArray(){
        List<GlucoseReading> glucoseReading = getGlucoseReadings();
        ArrayList<Integer> readingArray = new ArrayList<Integer>();
        int i;

        for (i = 0; i < glucoseReading.size(); i++){
            int reading;
            GlucoseReading singleReading= glucoseReading.get(i);
            reading = singleReading.get_reading();
            readingArray.add(reading);
        }

        return readingArray;
    }

    public ArrayList<String> getGlucoseTypeAsArray(){
        List<GlucoseReading> glucoseReading = getGlucoseReadings();
        ArrayList<String> typeArray = new ArrayList<String>();
        int i;

        for (i = 0; i < glucoseReading.size(); i++){
            String reading;
            GlucoseReading singleReading= glucoseReading.get(i);
            reading = singleReading.get_reading_type();
            typeArray.add(reading);
        }

        return typeArray;
    }

    public ArrayList<String> getGlucoseDateTimeAsArray(){
        List<GlucoseReading> glucoseReading = getGlucoseReadings();
        ArrayList<String> datetimeArray = new ArrayList<String>();
        int i;

        for (i = 0; i < glucoseReading.size(); i++){
            String reading;
            GlucoseReading singleReading= glucoseReading.get(i);
            reading = singleReading.get_created();
            datetimeArray.add(reading);
        }

        return datetimeArray;
    }

    public GlucoseReading getGlucoseReadingById(int id){
        return getGlucoseReadings("id = " + id).get(0);
    }
    public List<GlucoseReading> getGlucoseReadingsByMonth(int month){

        String m=Integer.toString(month);
        m=String.format("%02d",m);
       return getGlucoseReadings(" strftime('%m',created)='"+m+"'");
    }

    /*private ArrayList<Integer> getGlucoseReadingsForLastMonthAsArray(){
        Calendar calendar = Calendar.getInstance();
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String now = inputFormat.format(calendar.getTime());
        calendar.add(Calendar.MONTH, -1);
        String oneMonthAgo = inputFormat.format(calendar.getTime());


        String[] parameters = new String[] { oneMonthAgo, now } ;
        String[] columns = new String[] { "reading" };
        String whereString = "created_at between ? and ?";

        List<GlucoseReading> gReadings;
        ArrayList<Integer> readings = new ArrayList<Integer>();

        gReadings = GlucoseReading.getAllGlucoseReading(whereString);
        int i;
        for (i=0; i < gReadings.size(); i++){
            readings.add(gReadings.get(i).get_reading());
        }

        return readings;
    }*/

/*    public Integer getAverageGlucoseReadingForLastMonth() {
        ArrayList<Integer> readings = getGlucoseReadingsForLastMonthAsArray();
        int sum = 0;
        int numberOfReadings = readings.size();
        for (int i=0; i < numberOfReadings; i++) {
            sum += readings.get(i);
        }
        if (numberOfReadings > 0){
            return Math.round(sum / numberOfReadings);
        } else {
            return 0;
        }
    }*/

/*    private ArrayList<Integer> getAverageGlucoseReadingsByWeek(){

        SQLiteDatabase db=this.getReadableDatabase();
        String[] columns = new String[] { "reading", "strftime('%Y%W', created_at) AS week" };

        Cursor cursor = db.query(false, "glucose_readings", columns,null, null, "week", null, null, null);

        ArrayList<Integer> readings = new ArrayList<Integer>();

        if(cursor.moveToFirst()){
            do{
                readings.add(cursor.getInt(0));
            }while(cursor.moveToNext());
        }
        return readings;
    }*/


/*    private ArrayList<Integer> getAverageGlucoseReadingsByMonth(){

        SQLiteDatabase db=this.getReadableDatabase();
        String[] columns = new String[] { "reading", "strftime('%Y%m', created_at) AS month" };

        Cursor cursor = db.query(false, "glucose_readings", columns,null, null, "month", null, null, null);

        ArrayList<Integer> readings = new ArrayList<Integer>();

        if(cursor.moveToFirst()){
            do{
                readings.add(cursor.getInt(0));
            }while(cursor.moveToNext());
        }
        return readings;
    }*/


}
