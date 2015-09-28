package org.glucosio.android.presenter;

import android.text.TextUtils;
import android.widget.HeaderViewListAdapter;

import org.glucosio.android.R;
import org.glucosio.android.activity.HelloActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.User;
import org.w3c.dom.Text;


public class HelloPresenter {
    DatabaseHandler dB;
    HelloActivity helloActivity;
    int id;
    int age;
    String name;
    String country;
    String gender;
    int diabetesType;
    String unitMeasurement;
    String language;

    public HelloPresenter(HelloActivity helloActivity) {
        this.helloActivity = helloActivity;
        dB = new DatabaseHandler();
    }

    public void loadDatabase(){
        id = 1; // Id is always 1. We don't support multi-user (for now :D).
        name = "Test Account"; //TODO: add input for name in Tips;
    }

    public void onNextClicked(String age, String gender, String language, String country, int type, String unit){
        if (validateAge(age)){
            this.age = Integer.parseInt(age);
            this.gender = gender;
            this.language = language;
            this.country = country;
            this.diabetesType = type;
            this.unitMeasurement = unit;
            showEULA();
        } else {
            helloActivity.displayErrorMessage();
        }
    }

    private boolean validateAge(String age) {
        if (TextUtils.isEmpty(age)){
            return false;
        } else if (!TextUtils.isDigitsOnly(age)){
            return false;
        } else {
            int finalAge = Integer.parseInt(age);
            return finalAge > 0 && finalAge < 120;
        }
    }

    private void showEULA(){
        helloActivity.showEULA();
    }

    public void saveToDatabase(){
        dB.addUser(new User(id, name, language, country, age, gender, diabetesType, unitMeasurement));
        helloActivity.closeHelloActivity();
    }
}
