package org.glucosio.android.db;

import android.content.Context;
import android.opengl.GLU;

import com.activeandroid.query.Select;

import org.glucosio.android.tools.GlucoseConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class DatabaseHandler {

    Context mContext;
    Realm realm;

    public DatabaseHandler(Context context) {
        this.mContext = context;
        this.realm=Realm.getInstance(mContext);
    }

    public void addUser(User user) {
        realm.beginTransaction();
        realm.copyToRealm(user);
        realm.commitTransaction();
    }

    public User getUser(long id) {
        return realm.where(User.class)
                .equalTo("id", id)
                .findFirst();
    }

    public void updateUser(User user) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(user);
        realm.commitTransaction();
    }

    public void addGlucoseReading(GlucoseReading reading) {
        realm.beginTransaction();
        reading.setId(getNextKey());
        realm.copyToRealm(reading);
        realm.commitTransaction();
    }

    public void deleteGlucoseReadings(GlucoseReading reading) {
        realm.beginTransaction();
        reading.removeFromRealm();
        realm.commitTransaction();
    }

    public RealmResults<GlucoseReading> getGlucoseReadings() {
        return realm.where(GlucoseReading.class)
                .findAllSorted("created", false);
    }

    public GlucoseReading getGlucoseReading(long id) {
        return realm.where(GlucoseReading.class)
                .equalTo("id", id)
                .findFirst();
    }

    public ArrayList<Long> getGlucoseIdAsArray(){
        List<GlucoseReading> glucoseReading = getGlucoseReadings();
        ArrayList<Long> idArray = new ArrayList<Long>();
        int i;

        for (i = 0; i < glucoseReading.size(); i++){
            long id;
            GlucoseReading singleReading= glucoseReading.get(i);
            id = singleReading.getId();
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
            reading = singleReading.getReading();
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
            reading = singleReading.getReading_type();
            typeArray.add(reading);
        }

        return typeArray;
    }

    public ArrayList<String> getGlucoseDateTimeAsArray(){
        List<GlucoseReading> glucoseReading = getGlucoseReadings();
        ArrayList<String> datetimeArray = new ArrayList<String>();
        int i;
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (i = 0; i < glucoseReading.size(); i++){
            String reading;
            GlucoseReading singleReading= glucoseReading.get(i);
            reading = inputFormat.format(singleReading.getCreated());
            datetimeArray.add(reading);
        }

        return datetimeArray;
    }

    public GlucoseReading getGlucoseReadingById(long id){
        return getGlucoseReading(id);
    }


/*    private ArrayList<Integer> getGlucoseReadingsForLastMonthAsArray(){
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

        gReadings = GlucoseReading.getGlucoseReadings(whereString);
        int i;
        for (i=0; i < gReadings.size(); i++){
            readings.add(gReadings.get(i).getReading());
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
    }

    public List<GlucoseReading> getAverageGlucoseReadingsByWeek(){
        String[] columns = new String[] { "reading", "strftime('%Y%W', created) AS week" };
        return GlucoseReading.getGlucoseReadingsByGroup(columns, "week");
    }

    public List<GlucoseReading> getAverageGlucoseReadingsByMonth() {
        String[] columns = new String[] { "reading", "strftime('%Y%m', created) AS month" };
        return GlucoseReading.getGlucoseReadingsByGroup(columns, "month");
    }*/

    public long getNextKey() {
        Number maxId = realm.where(GlucoseReading.class)
                .max("id");
        if (maxId == null){
            return 0;
        } else {
            return Long.parseLong(maxId.toString())+1;
        }
    }
}
