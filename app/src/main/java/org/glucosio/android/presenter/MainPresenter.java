package org.glucosio.android.presenter;

import android.util.Log;

import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.User;
import org.glucosio.android.tools.GlucoseConverter;
import org.glucosio.android.tools.ReadingTools;

public class MainPresenter {

    private MainActivity mainActivity;

    private DatabaseHandler dB;
    private User user;
    private ReadingTools rTools;
    private GlucoseConverter converter;
    private int age;

    private String readingYear;
    private String readingMonth;
    private String readingDay;
    private String readingHour;
    private String readingMinute;

    public MainPresenter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        dB = new DatabaseHandler(mainActivity.getApplicationContext());
        Log.i("msg::","initiated db object");
        if (dB.getUser(1) == null){
            // if user exists start hello activity
            mainActivity.startHelloActivity();
        } else {
            //creating  a nrw user
            user = dB.getUser(1);
            age = user.getAge();
            rTools = new ReadingTools();
            converter = new GlucoseConverter();
        }
    }

    public boolean isdbEmpty(){
        return dB.getGlucoseReadings().size() == 0;
    }
}
