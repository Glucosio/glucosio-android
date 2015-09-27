package org.glucosio.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="glucosio_db";
    private static final String TABLE_USER="User";
    private static final String TABLE_GLUCOSE_READING="glucose_readings";

    // columns
    private static final String KEY_ID="id";
    private static final String KEY_NAME="name";
    private static final String KEY_PREF_LANG="preferred_language";
    private static final String KEY_PREF_COUNTRY="country";
    private static final String KEY_AGE="age";
    private static final String KEY_GENDER="gender";
    private static final String KEY_CREATED_AT="created_at";
    private static final String KEY_PREFERRED_UNIT="preferred_unit";
    private static final String KEY_DIABETES_TYPE="diabetes_type";

    //glucose reading keys
    private static final String KEY_READING="reading";
    private static final String KEY_NOTES="notes";
    private static final String KEY_READING_TYPE="reading_type";
    private static final String KEY_USER_ID="user_id";

    private static DatabaseHandler sInstance;

    public static synchronized DatabaseHandler getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    public void createTable(SQLiteDatabase db)
    {
        String CREATE_USER_TABLE="CREATE TABLE "+TABLE_USER+" ("
                +KEY_ID+" INTEGER PRIMARY KEY,"+KEY_NAME+" TEXT,"
                +KEY_PREF_LANG+" TEXT,"+KEY_PREF_COUNTRY+" TEXT,"+KEY_AGE+" TEXT,"+KEY_GENDER+" TEXT," +
                KEY_PREFERRED_UNIT+" TEXT," +
                KEY_DIABETES_TYPE+" INTEGER )";
        String CREATE_GLUCOSE_READING_TABLE="CREATE TABLE "+TABLE_GLUCOSE_READING+" ("
                +KEY_ID+" INTEGER PRIMARY KEY,"+KEY_READING+" TEXT, "+
                KEY_READING_TYPE+" TEXT, "+
                KEY_CREATED_AT+" TIMESTAMP DEFAULT (datetime('now','localtime') ),"
                +KEY_USER_ID+" INTEGER DEFAULT 1," +
                KEY_NOTES+" TEXT DEFAULT NULL )";
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_GLUCOSE_READING_TABLE);
    }
    public void dropTable(SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GLUCOSE_READING);
    }

    public void resetTable()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        dropTable(db);
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            dropTable(db);
            onCreate(db);
        }
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
            db.setForeignKeyConstraintsEnabled(true);
    }

    public void addUser(User user) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            values.put(KEY_NAME, user.get_name());
            values.put(KEY_PREF_LANG,user.get_preferredLanguage());
            values.put(KEY_PREF_COUNTRY,user.get_country());
            values.put(KEY_AGE,user.get_age());
            values.put(KEY_GENDER, user.get_gender());
            values.put(KEY_DIABETES_TYPE, user.get_d_type());
            values.put(KEY_PREFERRED_UNIT, user.get_preferred_unit());
            db.insert(TABLE_USER, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("Database", "Error while trying to delete user");
        } finally {
            db.endTransaction();
        }
    }
    public User getUser(int id) {
        SQLiteDatabase db=this.getReadableDatabase();
        User user = new User();

        db.beginTransaction();
        try {
            Cursor cursor=db.query(TABLE_USER, new String[]{KEY_ID, KEY_NAME, KEY_PREF_LANG,
                            KEY_PREF_COUNTRY, KEY_AGE, KEY_GENDER,
                            KEY_DIABETES_TYPE, KEY_PREFERRED_UNIT}, KEY_ID + "=?",
                    new String[]{String.valueOf(id)}, null, null, null);
            try {

                    if(cursor!=null) {
                        if (cursor.moveToFirst()){
                            user=new User(Integer.parseInt(cursor.getString(0)),
                                    cursor.getString(1),
                                    cursor.getString(2),
                                    cursor.getString(3),
                                    Integer.parseInt(cursor.getString(4)),
                                    cursor.getString(5),
                                    Integer.parseInt(cursor.getString(6)),
                                    cursor.getString(7));

                            db.setTransactionSuccessful();
                            return user;
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }

            } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }

        } catch (Exception e) {
            Log.d("Database", "Error while trying to get user");
        } finally {
            db.endTransaction();
        }

        return user;
    }

    public List<User> getUsers() {
        SQLiteDatabase db=this.getReadableDatabase();
        List<User> userLists=new ArrayList<User>();
        String selectQuery="SELECT * FROM "+TABLE_USER;

        db.beginTransaction();
        try {
            Cursor cursor=db.rawQuery(selectQuery, null);
            try {
                if(cursor.moveToFirst()){
                    do{
                        User user=new User();
                        user.set_id(Integer.parseInt(cursor.getString(0)));
                        user.set_name(cursor.getString(1));
                        user.set_preferredLanguage(cursor.getString(2));
                        user.set_country(cursor.getString(3));
                        user.set_age(Integer.parseInt(cursor.getString(4)));
                        user.set_gender(cursor.getString(5));
                        user.set_d_type(Integer.parseInt(cursor.getString(6)));
                        user.set_preferred_unit(cursor.getString(7));
                        userLists.add(user);
                    } while(cursor.moveToNext());
                }
                db.setTransactionSuccessful();
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

        } catch (Exception e) {
            Log.d("Database", "Error while trying to get users");
        } finally {
            db.endTransaction();
        }
        return userLists;
    }

    public int getTotalUsers()
    {
        int usersNumber = -1;
        String countQuery=" SELECT * from "+TABLE_USER;
        SQLiteDatabase db=this.getReadableDatabase();

        db.beginTransaction();
        try {
            Cursor cursor=db.rawQuery(countQuery, null);
            try {
                usersNumber = cursor.getCount();
                db.setTransactionSuccessful();
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

        } catch (Exception e) {
            Log.d("Database", "Error while trying to get total users");
        } finally {
            db.endTransaction();
        }
        return usersNumber;
    }

    public int  updateUser(User user) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        db.beginTransaction();
        try {
            values.put(KEY_NAME,user.get_name());
            values.put(KEY_PREF_LANG,user.get_preferredLanguage());
            values.put(KEY_PREF_COUNTRY,user.get_country());
            values.put(KEY_AGE, user.get_age());
            values.put(KEY_GENDER, user.get_gender());
            values.put(KEY_DIABETES_TYPE, user.get_d_type());
            values.put(KEY_PREFERRED_UNIT, user.get_preferred_unit());
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("Database", "Error while trying to update user");
        } finally {
            db.endTransaction();
        }
        return db.update(TABLE_USER,values,KEY_ID+" =? ",new String[]{ String.valueOf(user.get_id()) });
    }
    public void deleteUser(User user)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_USER, KEY_ID + " =? ", new String[]{String.valueOf(user.get_id())});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("Database", "Error while trying to delete user");
        } finally {
            db.endTransaction();
        }
    }

    public void addGlucoseReading(GlucoseReading reading)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();

        db.beginTransaction();
        try {
            values.put(KEY_READING,reading.get_reading());
            values.put(KEY_READING_TYPE, reading.get_reading_type());
            values.put(KEY_CREATED_AT, reading.get_created());
            values.put(KEY_NOTES, reading.get_notes());
            db.insert(TABLE_GLUCOSE_READING, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("Database", "Error while trying to add reading");
        } finally {
            db.endTransaction();
        }
    }

    public void deleteGlucoseReadings(GlucoseReading reading)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_GLUCOSE_READING, KEY_ID + " =? ", new String[]{String.valueOf(reading.get_id())});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("Database", "Error while trying to delete user");
        } finally {
            db.endTransaction();
        }
    }
    public List<GlucoseReading> getGlucoseReadings()
    {
        String selectQuery="select * from glucose_readings order by "+KEY_CREATED_AT+" desc";
        return getGlucoseReadingsRecords(selectQuery);
    }
    public List<GlucoseReading> getGlucoseReadings(String where)
    {
        String selectQuery="select * from glucose_readings where "+where+" order by "+KEY_CREATED_AT+" desc";
        return getGlucoseReadingsRecords(selectQuery);
    }
    public List<GlucoseReading> getGlucoseReadingsRecords(String selectQuery)
    {
        List<GlucoseReading> readings=new ArrayList<GlucoseReading>();
        SQLiteDatabase db=this.getReadableDatabase();

        db.beginTransaction();
        try {
            Cursor cursor=db.rawQuery(selectQuery,null);
            try {
                if(cursor.moveToFirst()){
                    do{
                        GlucoseReading reading=new GlucoseReading();
                        reading.set_id(Integer.parseInt(cursor.getString(0)));
                        reading.set_reading(Integer.parseInt(cursor.getString(1)));
                        reading.set_reading_type(cursor.getString(2));
                        reading.set_created(cursor.getString(3));
                        reading.set_user_id(Integer.parseInt(cursor.getString(4)));
                        reading.set_notes(cursor.getString(5));
                        readings.add(reading);
                    }while(cursor.moveToNext());
                }
                db.setTransactionSuccessful();
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

        } catch (Exception e) {
            Log.d("Database", "Error while trying to get glucose reading");
        } finally {
            db.endTransaction();
        }
        return readings;
    }
    public ArrayList<Integer> getGlucoseIdAsArray(){
        List<GlucoseReading> glucoseReading = getGlucoseReadings();
        ArrayList<Integer> idArray = new ArrayList<Integer>();
        int i;

        for (i = 0; i < glucoseReading.size(); i++){
            int id;
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
    private ArrayList<Integer> getGlucoseReadingsForLastMonthAsArray(){

        SQLiteDatabase db=this.getReadableDatabase();
        Calendar calendar = Calendar.getInstance();
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String now = inputFormat.format(calendar.getTime());
        calendar.add(Calendar.MONTH, -1);
        String nowString = now.toString();
        String oneMonthAgo = inputFormat.format(calendar.getTime());


        String[] parameters = new String[] { oneMonthAgo, now } ;
        String[] columns = new String[] { "reading" };
        String whereString = "created_at between ? and ?";


        ArrayList<Integer> readings = new ArrayList<Integer>();

        db.beginTransaction();
        try {
            Cursor cursor = db.query(false, "glucose_readings", columns,whereString, parameters, null, null, null, null);
            try {
                if(cursor.moveToFirst()){
                    do{
                        readings.add(cursor.getInt(0));
                    }while(cursor.moveToNext());
                }
                db.setTransactionSuccessful();
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

        } catch (Exception e) {
            Log.d("Database", "Error while trying to get glucose readings for last month");
        } finally {
            db.endTransaction();
        }

        return readings;
    }

    public Integer getAverageGlucoseReadingForLastMonth() {
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

    private ArrayList<Integer> getAverageGlucoseReadingsByWeek(){

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
    }


    private ArrayList<Integer> getAverageGlucoseReadingsByMonth(){

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
    }


}
