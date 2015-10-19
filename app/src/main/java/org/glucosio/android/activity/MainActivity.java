package org.glucosio.android.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.glucosio.android.GlucosioApplication;
import org.glucosio.android.R;
import org.glucosio.android.adapter.HomePagerAdapter;
import org.glucosio.android.presenter.MainPresenter;
import org.glucosio.android.tools.LabelledSpinner;
import org.glucosio.android.tools.LabelledSpinner.OnItemChosenListener;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{

    private LabelledSpinner spinnerReadingType;
    private Dialog addDialog;

    private TextView dialogCancelButton;
    private TextView dialogAddButton;
    private TextView dialogAddTime;
    private TextView dialogAddDate;
    private TextView dialogReading;
    private EditText dialogTypeCustom;
    private HomePagerAdapter homePagerAdapter;
    private boolean isCustomType;

    private MainPresenter presenter;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainPresenter(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setLogo(R.drawable.ic_logo);
        }

        homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), getApplicationContext());

        viewPager.setAdapter(homePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
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
                    LinearLayout emptyLayout = (LinearLayout) findViewById(R.id.mainactivity_empty_layout);
                    ViewPager pager = (ViewPager) findViewById(R.id.pager);
                    if (pager.getVisibility() == View.GONE) {
                        pager.setVisibility(View.VISIBLE);
                        emptyLayout.setVisibility(View.INVISIBLE);
                    }
                } else {
                    showFabAnimation();
                    checkIfEmptyLayout();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        checkIfEmptyLayout();

        // Obtain the Analytics shared Tracker instance.
        GlucosioApplication application = (GlucosioApplication) getApplication();
        mTracker = application.getDefaultTracker();
        Log.i("MainActivity", "Setting screen name: " + "main");
        mTracker.setScreenName("Main Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void startHelloActivity() {
        Intent intent = new Intent(this, HelloActivity.class);
        startActivity(intent);
        finish();
    }

    public void startGittyReporter() {
        Intent intent = new Intent(this, GittyActivity.class);
        startActivity(intent);
    }

    public void openPreferences() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
        finish();
    }

    public void onFabClicked(View v) {
        showAddDialog();
    }

    public void showAddDialog(){
        addDialog = new Dialog(MainActivity.this, R.style.GlucosioTheme);

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
        dialogTypeCustom = (EditText) addDialog.findViewById(R.id.dialog_type_custom);

        presenter.updateSpinnerTypeTime();
        this.isCustomType = false;

        spinnerReadingType.setOnItemChosenListener(new OnItemChosenListener() {
            @Override
            public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
                // If other is selected
                if (position == 9) {
                    dialogTypeCustom.setVisibility(View.VISIBLE);
                    isCustomType = true;
                } else {
                    if (dialogTypeCustom.getVisibility() == View.VISIBLE) {
                        dialogTypeCustom.setVisibility(View.GONE);
                        isCustomType = false;
                    }
                }
            }

            @Override
            public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {

            }
        });


        dialogAddTime.setText(presenter.getReadingHour() + ":" + presenter.getReadingMinute());
        dialogAddDate.setText(presenter.getReadingDay() + "/" + presenter.getReadingMonth() + "/" + presenter.getReadingYear());

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
                dpd.setMaxDate(now);
            }
        });

        dialogAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(MainActivity.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
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
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("GlucoseDialog")
                        .setAction("Add")
                        .build());
            }
        });

        TextView unitM = (TextView) addDialog.findViewById(R.id.dialog_add_unit_measurement);

        if (presenter.getUnitMeasuerement().equals("mg/dL")){
            unitM.setText("mg/dL");
        } else {
            unitM.setText("mmol/L");
        }

        // Workaround for ActionBarContextView bug.
        android.view.ActionMode.Callback workaroundCallback = new android.view.ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {

            }
        };

        dialogReading.setCustomSelectionActionModeCallback(workaroundCallback);

        dialogTypeCustom.setCustomSelectionActionModeCallback(workaroundCallback);
    }

    public void showEditDialog(final int id){
        //only included for debug
        // printGlucoseReadingTableDetails();

        final int readingId = id;
        addDialog = new Dialog(MainActivity.this, R.style.GlucosioTheme);

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
        dialogReading.setText(presenter.getGlucoseReadingReadingById(readingId));

        dialogReading = (TextView) addDialog.findViewById(R.id.dialog_add_concentration);
        dialogTypeCustom = (EditText) addDialog.findViewById(R.id.dialog_type_custom);

        presenter.updateSpinnerTypeTime();
        this.isCustomType = false;

        spinnerReadingType.setSelection(typeStringToInt(presenter.getGlucoseReadingTypeById(readingId)));

        spinnerReadingType.setOnItemChosenListener(new OnItemChosenListener() {
            @Override
            public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
                // If other is selected
                if (position == 9) {
                    dialogTypeCustom.setVisibility(View.VISIBLE);
                    dialogTypeCustom.setText(presenter.getGlucoseReadingTypeById(readingId));
                    isCustomType = true;
                } else {
                    if (dialogTypeCustom.getVisibility() == View.VISIBLE) {
                        dialogTypeCustom.setVisibility(View.GONE);
                        dialogTypeCustom.setText("");
                        isCustomType = false;
                    }
                }
            }

            @Override
            public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {

            }
        });

        presenter.getGlucoseReadingTimeById(id);

        dialogAddTime.setText(presenter.getReadingHour() + ":" + presenter.getReadingMinute());
        dialogAddDate.setText(presenter.getReadingDay() + "/" + presenter.getReadingMonth() + "/" + presenter.getReadingYear());

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
                dpd.setMaxDate(now);
            }
        });

        dialogAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(MainActivity.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
                tpd.show(getFragmentManager(), "Timepickerdialog");
                spinnerReadingType.setSelection(presenter.timeToSpinnerType());
            }
        });
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
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

        TextView unitM = (TextView) addDialog.findViewById(R.id.dialog_add_unit_measurement);
        if (presenter.getUnitMeasuerement().equals("mg/dl")){
            unitM.setText("mg/dl");
        } else {
            unitM.setText("mmol/L");
        }

        // Workaround for ActionBarContextView bug.
        android.view.ActionMode.Callback workaroundCallback = new android.view.ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {

            }
        };

        dialogReading.setCustomSelectionActionModeCallback(workaroundCallback);

        dialogTypeCustom.setCustomSelectionActionModeCallback(workaroundCallback);
    }

    private void dialogOnAddButtonPressed() {
        if (isCustomType) {
            presenter.dialogOnAddButtonPressed(dialogAddTime.getText().toString(),
                    dialogAddDate.getText().toString(), dialogReading.getText().toString(),
                    dialogTypeCustom.getText().toString());
        } else {
            presenter.dialogOnAddButtonPressed(dialogAddTime.getText().toString(),
                    dialogAddDate.getText().toString(), dialogReading.getText().toString(),
                    spinnerReadingType.getSpinner().getSelectedItem().toString());
        }
    }

    public void dismissAddDialog(){
        addDialog.dismiss();
        homePagerAdapter.notifyDataSetChanged();
        checkIfEmptyLayout();
    }

    public void showErrorMessage(){
        Toast.makeText(getApplicationContext(),getString(R.string.dialog_error2), Toast.LENGTH_SHORT).show();
    }

    private void dialogOnEditButtonPressed(int id){
        if (isCustomType) {
            presenter.dialogOnEditButtonPressed(dialogAddTime.getText().toString(),
                    dialogAddDate.getText().toString(), dialogReading.getText().toString(),
                    dialogTypeCustom.getText().toString(), id);
        } else {
            presenter.dialogOnEditButtonPressed(dialogAddTime.getText().toString(),
                    dialogAddDate.getText().toString(), dialogReading.getText().toString(),
                    spinnerReadingType.getSpinner().getSelectedItem().toString(), id);
        }
    }

    public void updateSpinnerTypeTime(int selection){
        spinnerReadingType.setSelection(selection);
    }

    private void updateSpinnerTypeHour(int hour){
        spinnerReadingType.setSelection(presenter.hourToSpinnerType(hour));
    }

    public CoordinatorLayout getFabView() {
        return (CoordinatorLayout) findViewById(R.id.coordinator_layout);
    }

    public void reloadFragmentAdapter(){
        homePagerAdapter.notifyDataSetChanged();
    }

    public void turnOffToolbarScrolling() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);

        //turn off scrolling
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(0);
        mToolbar.setLayoutParams(toolbarLayoutParams);

        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
        appBarLayout.setLayoutParams(appBarLayoutParams);
    }

    public void turnOnToolbarScrolling() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);

        //turn on scrolling
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        mToolbar.setLayoutParams(toolbarLayoutParams);

        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
        appBarLayout.setLayoutParams(appBarLayoutParams);
    }

    public Toolbar getToolbar(){
        return (Toolbar) findViewById(R.id.toolbar);
    }

    private void hideFabAnimation(){
       final View fab = (View) findViewById(R.id.main_fab);
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
        final View fab = (View) findViewById(R.id.main_fab);
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

    public void checkIfEmptyLayout(){
        LinearLayout emptyLayout = (LinearLayout) findViewById(R.id.mainactivity_empty_layout);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        if (presenter.isdbEmpty()) {
            pager.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (getResources().getConfiguration().orientation == 1) {
                    // If Portrait choose vertical curved line
                    ImageView arrow = (ImageView) findViewById(R.id.mainactivity_arrow);
                    arrow.setBackground(getResources().getDrawable(R.drawable.curved_line_vertical));
                } else {
                    // Else choose horizontal one
                    ImageView arrow = (ImageView) findViewById(R.id.mainactivity_arrow);
                    arrow.setBackground((getResources().getDrawable(R.drawable.curved_line_horizontal)));
                }
            }
        } else {
            pager.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.INVISIBLE);
        }
    }

    public int typeStringToInt(String typeString) {
        //TODO refactor this ugly mess
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
        } else if (typeString.equals(getString(R.string.dialog_add_type_7))) {
            typeInt = 6;
        } else if (typeString.equals(getString(R.string.dialog_add_type_8))) {
            typeInt = 7;
        } else if (typeString.equals(getString(R.string.dialog_add_type_9))) {
            typeInt = 8;
        } else {
            typeInt = 9;
        }

        return  typeInt;
}

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        TextView addTime = (TextView) addDialog.findViewById(R.id.dialog_add_time);
        DecimalFormat df = new DecimalFormat("00");

        presenter.setReadingHour(df.format(hourOfDay));
        presenter.setReadingMinute(df.format(minute));

        String time = +hourOfDay+":"+presenter.getReadingMinute();
        addTime.setText(time);
        updateSpinnerTypeHour(Integer.parseInt(df.format(hourOfDay)));
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        TextView addDate = (TextView) addDialog.findViewById(R.id.dialog_add_date);
        DecimalFormat df = new DecimalFormat("00");

        presenter.setReadingYear(year+"");
        presenter.setReadingMonth(df.format(monthOfYear+1));
        presenter.setReadingDay(df.format(dayOfMonth));

        String date = +dayOfMonth+"/"+presenter.getReadingMonth()+"/"+presenter.getReadingYear();
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
            openPreferences();
            return true;
        } else if (id == R.id.action_feedback) {
            startGittyReporter();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
