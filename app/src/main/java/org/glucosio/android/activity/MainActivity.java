package org.glucosio.android.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.glucosio.android.R;
import org.glucosio.android.adapter.HomePagerAdapter;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.db.User;
import org.glucosio.android.tools.LabelledSpinner;

import java.text.DecimalFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    DatabaseHandler db;
    LabelledSpinner spinnerReadingType;
    Dialog addDialog;
    User user;
    int age;

    String readingYear;
    String readingMonth;
    String readingDay;
    String readingHour;
    String readingMinute;
    String finalDateTime;

    TextView dialogCancelButton;
    TextView dialogAddButton;
    TextView dialogAddTime;
    TextView dialogAddDate;
    TextView dialogReading;
    TextView dialogType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setElevation(0);
        }

        viewPager.setAdapter(new HomePagerAdapter(getSupportFragmentManager(), getApplicationContext()));
        tabLayout.setupWithViewPager(viewPager);

        db = new DatabaseHandler(this);



        // TODO: Check if we have all users information from database;
       loadDatabase();
    }

    private void loadDatabase(){
        user = db.getUser(1);

        if (user == null){
            startHelloActivity();
            finish();
        } else {
            age = user.get_age();
        }

        // databaseTestings();
    }

    private void databaseTestings()
    {
        db.resetTable();
        // db.addGlucoseReading(new GlucoseReading(1.2, 1));
        Log.i("filter::", "called hee");

        for (GlucoseReading reading : db.getGlucoseReadings()) {
            Log.i("dbreturn::",String.valueOf(reading.get_user_id()));
        }
    }

    public DatabaseHandler getDatabase(){
        return db;
    }

    private void startHelloActivity() {
        Intent intent = new Intent(this, HelloActivity.class);
        startActivity(intent);
        finish();
    }

    public void onFabClicked(View v){
        addDialog = new Dialog(MainActivity.this, R.style.AppTheme);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(addDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addDialog.setContentView(R.layout.dialog_add);
        addDialog.show();
        addDialog.getWindow().setAttributes(lp);
        addDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        addDialog.getWindow().setDimAmount(0.5f);

        spinnerReadingType = (LabelledSpinner) addDialog.findViewById(R.id.dialog_add_reading_type);
        spinnerReadingType.setItemsArray(R.array.dialog_add_measured_list);

        dialogCancelButton = (TextView) addDialog.findViewById(R.id.dialog_add_cancel);
        dialogAddButton = (TextView) addDialog.findViewById(R.id.dialog_add_add);
        dialogAddTime = (TextView) addDialog.findViewById(R.id.dialog_add_time);
        dialogAddDate = (TextView) addDialog.findViewById(R.id.dialog_add_date);
        dialogReading = (TextView) addDialog.findViewById(R.id.dialog_add_concentration);

        dialogAddDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        MainActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        dialogAddTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd =  TimePickerDialog.newInstance(MainActivity.this, now.get(Calendar.HOUR_OF_DAY) ,now.get(Calendar.MINUTE), true);
                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });
        dialogCancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                addDialog.dismiss();
            }
        });
        dialogAddButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialogOnAddButtonPressed();
            }
        });
    }

    private void dialogOnAddButtonPressed(){

        if (validateDate() && validateTime() && validateReading()) {
            Double finalReading = Double.parseDouble(dialogReading.getText().toString());
            int finalType = typeToInt();
            finalDateTime = readingYear + "-" + readingMonth + "-" + readingDay + " " + readingHour + ":" + readingMinute;

            GlucoseReading gReading = new GlucoseReading(finalReading, finalType, finalDateTime);
            db.addGlucoseReading(gReading);

            addDialog.dismiss();
        } else {
            Toast.makeText(getApplicationContext(),getString(R.string.dialog_error), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateTime(){
        return !dialogAddTime.getText().toString().equals("");
    }
    private boolean validateDate(){
        return !dialogAddDate.getText().toString().equals("");
    }
    private boolean validateReading(){
        return !dialogReading.getText().toString().equals("");
    }

    private int typeToInt(){
        String typeString = spinnerReadingType.getSpinner().getSelectedItem().toString();
        int typeInt;
        if (typeString.equals(getString(R.string.dialog_add_type_1))) {
            typeInt = 1;

        } else if (typeString.equals(getString(R.string.dialog_add_type_2))) {
            typeInt = 2;

        } else if (typeString.equals(getString(R.string.dialog_add_type_3))) {
            typeInt = 3;

        } else if (typeString.equals(getString(R.string.dialog_add_type_4))) {
            typeInt = 4;

        } else if (typeString.equals(getString(R.string.dialog_add_type_5))) {
            typeInt = 5;

        } else if (typeString.equals(getString(R.string.dialog_add_type_6))) {
            typeInt = 6;
        } else {
            typeInt = 0;
        }

        return  typeInt;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        TextView addTime = (TextView) addDialog.findViewById(R.id.dialog_add_time);
        DecimalFormat df = new DecimalFormat("00");

        this.readingHour = df.format(hourOfDay);
        this.readingMinute = df.format(minute);

        String time = +hourOfDay+":"+readingMinute;
        addTime.setText(time);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        TextView addDate = (TextView) addDialog.findViewById(R.id.dialog_add_date);
        DecimalFormat df = new DecimalFormat("00");

        this.readingYear = year+"";
        this.readingMonth = df.format(monthOfYear+1);
        this.readingDay = df.format(dayOfMonth);

        String date = +dayOfMonth+"/"+readingMonth+"/"+readingYear;
        addDate.setText(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
