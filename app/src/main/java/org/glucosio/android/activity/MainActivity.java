package org.glucosio.android.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
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
import org.glucosio.android.tools.SplitDateTime;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


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
    HomePagerAdapter homePagerAdapter;

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

        homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), getApplicationContext());

        viewPager.setAdapter(homePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        int position = tab.getPosition();

                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    hideFabAnimation();
                } else {
                    showFabAnimation();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        db = new DatabaseHandler(this);
        loadDatabase();

        // Set fonts
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/lato.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }


    private void loadDatabase(){
        user = db.getUser(1);

        if (user == null){
            startHelloActivity();
            finish();
        } else {
            age = user.get_age();
        }

    }

    private void databaseTestings()
    {
        db.resetTable();
        // db.addGlucoseReading(new GlucoseReading(1.2, 1));
        Log.i("filter::", "called hee");

        for (GlucoseReading reading : db.getGlucoseReadings()) {
            Log.i("dbreturn::", String.valueOf(reading.get_user_id()));
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
    public void printGlucoseReadingTableDetails()
    {
        for (GlucoseReading reading : db.getGlucoseReadings()) {
            String data="";
            data+=" id:     "+String.valueOf(reading.get_id());
            data+=" reading : "+String.valueOf(reading.get_reading());
            data+=" reading type : "+String.valueOf(reading.get_reading_type());
            data+=" created_at : "+String.valueOf(reading.get_created());
            Log.d("db::glucose_reading", data);
        }

    }

    public void onFabClicked(View v){
        //only included for debug
        // printGlucoseReadingTableDetails();

        addDialog = new Dialog(MainActivity.this, R.style.AppTheme);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(addDialog.getWindow().getAttributes());
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        addDialog.setContentView(R.layout.dialog_add);
        addDialog.getWindow().setAttributes(lp);
        addDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        addDialog.getWindow().setDimAmount(0.5f);
        addDialog.show();


        spinnerReadingType = (LabelledSpinner) addDialog.findViewById(R.id.dialog_add_reading_type);
        spinnerReadingType.setItemsArray(R.array.dialog_add_measured_list);

        dialogCancelButton = (TextView) addDialog.findViewById(R.id.dialog_add_cancel);
        dialogAddButton = (TextView) addDialog.findViewById(R.id.dialog_add_add);
        dialogAddTime = (TextView) addDialog.findViewById(R.id.dialog_add_time);
        dialogAddDate = (TextView) addDialog.findViewById(R.id.dialog_add_date);
        dialogReading = (TextView) addDialog.findViewById(R.id.dialog_add_concentration);


        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formatted = inputFormat.format(Calendar.getInstance().getTime());
        SplitDateTime addSplitDateTime = new SplitDateTime(formatted, inputFormat);

        this.readingYear = addSplitDateTime.getYear();
        this.readingMonth = addSplitDateTime.getMonth();
        this.readingDay = addSplitDateTime.getDay();
        this.readingHour = addSplitDateTime.getHour();
        this.readingMinute = addSplitDateTime.getMinute();

        dialogAddTime.setText(readingHour + ":" + readingMinute);
        dialogAddDate.setText(readingDay + "/" + readingMonth + "/" +readingYear);

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
        dialogAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOnAddButtonPressed();
            }
        });
    }

    public void showEditDialog(final Double id){
        //only included for debug
        // printGlucoseReadingTableDetails();

        addDialog = new Dialog(MainActivity.this, R.style.AppTheme);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(addDialog.getWindow().getAttributes());
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        addDialog.setContentView(R.layout.dialog_add);
        addDialog.getWindow().setAttributes(lp);
        addDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        addDialog.getWindow().setDimAmount(0.5f);
        addDialog.show();

        spinnerReadingType = (LabelledSpinner) addDialog.findViewById(R.id.dialog_add_reading_type);
        spinnerReadingType.setItemsArray(R.array.dialog_add_measured_list);

        dialogCancelButton = (TextView) addDialog.findViewById(R.id.dialog_add_cancel);
        dialogAddButton = (TextView) addDialog.findViewById(R.id.dialog_add_add);
        dialogAddTime = (TextView) addDialog.findViewById(R.id.dialog_add_time);
        dialogAddDate = (TextView) addDialog.findViewById(R.id.dialog_add_date);
        dialogReading = (TextView) addDialog.findViewById(R.id.dialog_add_concentration);
        dialogAddButton.setText(getString(R.string.dialog_edit).toUpperCase());
        dialogReading.setText(db.getGlucoseReadings("id = " + id).get(0).get_reading().toString());
        spinnerReadingType.setSelection(db.getGlucoseReadings("id = " + id).get(0).get_reading_type());

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SplitDateTime splitDateTime = new SplitDateTime(db.getGlucoseReadings("id = " + id).get(0).get_created(), inputFormat);
        this.readingYear = splitDateTime.getYear();
        this.readingMonth = splitDateTime.getMonth();
        this.readingDay = splitDateTime.getDay();
        this.readingHour = splitDateTime.getHour();
        this.readingMinute = splitDateTime.getMinute();

        dialogAddTime.setText(readingHour + ":" + readingMinute);
        dialogAddDate.setText(readingDay + "/" + readingMonth + "/" + readingYear);

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
                dialogOnEditButtonPressed(id);
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
            homePagerAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getApplicationContext(),getString(R.string.dialog_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void dialogOnEditButtonPressed(double id){
        if (validateDate() && validateTime() && validateReading()) {
            Double finalReading = Double.parseDouble(dialogReading.getText().toString());
            int finalType = typeToInt();
            finalDateTime = readingYear + "-" + readingMonth + "-" + readingDay + " " + readingHour + ":" + readingMinute;

            GlucoseReading gReadingToDelete = db.getGlucoseReadings("id = " +id).get(0);
            GlucoseReading gReading = new GlucoseReading(finalReading, finalType, finalDateTime);

            db.deleteGlucoseReadings(gReadingToDelete);
            db.addGlucoseReading(gReading);

            addDialog.dismiss();
            homePagerAdapter.notifyDataSetChanged();
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
            typeInt = 0;

        } else if (typeString.equals(getString(R.string.dialog_add_type_2))) {
            typeInt = 1;

        } else if (typeString.equals(getString(R.string.dialog_add_type_3))) {
            typeInt = 2;

        } else if (typeString.equals(getString(R.string.dialog_add_type_4))) {
            typeInt = 3;

        } else if (typeString.equals(getString(R.string.dialog_add_type_5))) {
            typeInt = 4;

        } else if (typeString.equals(getString(R.string.dialog_add_type_6))) {
            typeInt = 5;
        } else {
            typeInt = 6;
        }

        return  typeInt;
    }

    public CoordinatorLayout getFabView() {
        return (CoordinatorLayout) findViewById(R.id.coordinatorFab);
    }

    public void reloadFragmentAdapter(){
        homePagerAdapter.notifyDataSetChanged();
    }

    private void hideFabAnimation(){
       final View fab = (View) getFabView();
        fab.animate()
                .translationY(-5)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        fab.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void showFabAnimation(){
        final View fab = (View) getFabView();
        if (fab.getVisibility() == View.INVISIBLE) {
            // Prepare the View for the animation
            fab.setVisibility(View.VISIBLE);
            fab.setAlpha(0.0f);

            fab.animate()
                    .alpha(1f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            fab.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            // do nothing
            // probably swiping from OVERVIEW to HISTORY tab
        }
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
