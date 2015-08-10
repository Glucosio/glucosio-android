package org.glucosio.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmar on 10/8/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="glucosio_db";
    private static final String TABLE_USER="User";

//    columns
    private static final String KEY_ID="id";
    private static final String KEY_NAME="name";
    private static final String KEY_PREF_LANG="preferred_language";
    private static final String KEY_AGE="age";
    private static final String KEY_GENDER="gender";

    public DatabaseHandler(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE="CREATE TABLE "+TABLE_USER
                +KEY_ID+" INTEGER PRIMARY KEY,"+KEY_NAME+" TEXT,"
                +KEY_PREF_LANG+" TEXT,"+KEY_AGE+" TEXT,"+KEY_GENDER+" INTEGER";
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER);
        onCreate(db);
    }

    public void addUser(User user)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_NAME,user.get_name());
        values.put(KEY_PREF_LANG,user.get_preferredLanguage());
        values.put(KEY_AGE,user.get_age());
        values.put(KEY_GENDER, user.is_gender());
        db.insert(TABLE_USER, null, values);

    }
    public User getUser(int id)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_USER,new String[] { KEY_ID,KEY_NAME,KEY_PREF_LANG,KEY_AGE,KEY_GENDER},KEY_ID+"=?",null,null,null,null);
        if(cursor!=null) {
            cursor.moveToFirst();
        }
            User user=new User(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2)
                                ,Integer.parseInt(cursor.getString(3)),Boolean.parseBoolean(cursor.getString(3)));

        return user;
    }

    public List<User> getUsers()
    {
        List<User> userLists=new ArrayList<User>();
        String selectQuery="SELECT * FROM "+TABLE_USER;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do{
                User user=new User();
                user.set_id(Integer.parseInt(cursor.getString(0)));
                user.set_name(cursor.getString(1));
                user.set_preferredLanguage(cursor.getString(2));
                user.set_age(Integer.parseInt(cursor.getString(3)));
                user.set_gender(Boolean.parseBoolean(cursor.getString(4)));
                userLists.add(user);
            }while(cursor.moveToNext());
        }
        return userLists;
    }
    public int getTotalUsers()
    {
        String countQuery=" SELECT * from "+TABLE_USER;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(countQuery,null);
        cursor.close();
        return cursor.getCount();
    }
    public int  updateUser(User user)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_NAME,user.get_name());
        values.put(KEY_PREF_LANG,user.get_preferredLanguage());
        values.put(KEY_AGE,user.get_age());
        values.put(KEY_GENDER, user.is_gender());
        return db.update(TABLE_USER,values,KEY_ID+" =? ",new String[]{ String.valueOf(user.get_id()) });

    }
    public void deleteUser(User user)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_USER,KEY_ID+" =? ",new String[]{ String.valueOf(user.get_id()) });
        db.close();
    }

}
