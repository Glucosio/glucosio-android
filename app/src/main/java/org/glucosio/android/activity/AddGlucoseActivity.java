/*
 * Copyright (C) 2016 Glucosio Foundation
 *
 * This file is part of Glucosio.
 *
 * Glucosio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Glucosio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Glucosio.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package org.glucosio.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.glucosio.android.GlucosioApplication;
import org.glucosio.android.R;
import org.glucosio.android.analytics.Analytics;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.presenter.AddGlucosePresenter;
import org.glucosio.android.tools.AnimationTools;
import org.glucosio.android.tools.FormatDateTime;
import org.glucosio.android.tools.LabelledSpinner;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddGlucoseActivity extends AddReadingActivity {

    private FloatingActionButton doneFAB;
    private TextView addTimeTextView;
    private TextView addDateTextView;
    private TextView readingTextView;
    private EditText typeCustomEditText;
    private EditText notesEditText;
    private LabelledSpinner readingTypeSpinner;
    private Runnable fabAnimationRunnable;
    private boolean isCustomType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_glucose);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(2);
        }

        this.retrieveExtra();

        AddGlucosePresenter presenter = new AddGlucosePresenter(this);
        setPresenter(presenter);
        presenter.setReadingTimeNow();

        readingTypeSpinner = (LabelledSpinner) findViewById(R.id.glucose_add_reading_type);
        readingTypeSpinner.setItemsArray(R.array.dialog_add_measured_list);

        doneFAB = (FloatingActionButton) findViewById(R.id.done_fab);
        addTimeTextView = (TextView) findViewById(R.id.dialog_add_time);
        addDateTextView = (TextView) findViewById(R.id.dialog_add_date);
        readingTextView = (TextView) findViewById(R.id.glucose_add_concentration);
        typeCustomEditText = (EditText) findViewById(R.id.glucose_type_custom);
        notesEditText = (EditText) findViewById(R.id.glucose_add_notes);

        readingTypeSpinner.setOnItemChosenListener(new LabelledSpinner.OnItemChosenListener() {
            @Override
            public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
                // If other is selected
                if (position == 11) {
                    typeCustomEditText.setVisibility(View.VISIBLE);
                    isCustomType = true;
                } else {
                    if (typeCustomEditText.getVisibility() == View.VISIBLE) {
                        typeCustomEditText.setVisibility(View.GONE);
                        isCustomType = false;
                    }
                }
            }

            @Override
            public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {

            }
        });

        FormatDateTime formatDateTime = new FormatDateTime(getApplicationContext());
        addDateTextView.setText(formatDateTime.getCurrentDate());
        addTimeTextView.setText(formatDateTime.getCurrentTime());
        addDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddGlucoseActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                dpd.setMaxDate(now);
            }
        });

        addTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                if (android.text.format.DateFormat.is24HourFormat(getApplicationContext())) {
                    TimePickerDialog tpd = TimePickerDialog.newInstance(AddGlucoseActivity.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
                    tpd.show(getFragmentManager(), "Timepickerdialog");
                } else {
                    TimePickerDialog tpd = TimePickerDialog.newInstance(AddGlucoseActivity.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
                    tpd.show(getFragmentManager(), "Timepickerdialog");
                }
            }
        });
        doneFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOnAddButtonPressed();
            }
        });

        TextView unitM = (TextView) findViewById(R.id.glucose_add_unit_measurement);

        if (presenter.getUnitMeasuerement().equals("mg/dL")) {
            unitM.setText("mg/dL");
        } else {
            unitM.setText("mmol/L");
        }

        // If an id is passed, open the activity in edit mode
        if (this.isEditing()){
            FormatDateTime dateTime = new FormatDateTime(getApplicationContext());
            setTitle(R.string.title_activity_add_glucose_edit);
            GlucoseReading readingToEdit = presenter.getGlucoseReadingById(this.getEditId());
            readingTextView.setText(readingToEdit.getReading()+"");
            notesEditText.setText(readingToEdit.getNotes());
            Calendar cal = Calendar.getInstance();
            cal.setTime(readingToEdit.getCreated());
            addDateTextView.setText(dateTime.getDate(cal));
            addTimeTextView.setText(dateTime.getTime(cal));
            presenter.updateReadingSplitDateTime(readingToEdit.getCreated());
            // retrive spinner reading to set the registered one
            String measuredTypeText = readingToEdit.getReading_type();
            int mesuredId = presenter.retriveSpinnerID(measuredTypeText, Arrays.asList(getResources().getStringArray(R.array.dialog_add_measured_list)));
            readingTypeSpinner.setSelection(mesuredId);
            this.isCustomType = mesuredId == 11; // if other, it a custom type
            if(this.isCustomType) {
                typeCustomEditText.setText(measuredTypeText);
            }
        } else {
            presenter.updateSpinnerTypeTime();
            this.isCustomType = false;
        }

        fabAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                AnimationTools.startCircularReveal(doneFAB);
            }
        };

        doneFAB.postDelayed(fabAnimationRunnable, 600);
    }

    private void addAnalyticsEvent() {
        Analytics analytics = ((GlucosioApplication) getApplication()).getAnalytics();
        analytics.reportAction("FreeStyle Libre", "New reading added");
    }

    private void dialogOnAddButtonPressed() {
        AddGlucosePresenter presenter = (AddGlucosePresenter) getPresenter();
        if (isCustomType) {
            if (this.isEditing()) {
                presenter.dialogOnAddButtonPressed(addTimeTextView.getText().toString(),
                        addDateTextView.getText().toString(), readingTextView.getText().toString(),
                        typeCustomEditText.getText().toString(), notesEditText.getText().toString(), this.getEditId());
            } else {
                presenter.dialogOnAddButtonPressed(addTimeTextView.getText().toString(),
                        addDateTextView.getText().toString(), readingTextView.getText().toString(),
                        typeCustomEditText.getText().toString(), notesEditText.getText().toString());
            }
        } else {
            if (this.isEditing()) {
                presenter.dialogOnAddButtonPressed(addTimeTextView.getText().toString(),
                        addDateTextView.getText().toString(), readingTextView.getText().toString(),
                        readingTypeSpinner.getSpinner().getSelectedItem().toString(), notesEditText.getText().toString(), this.getEditId());
            } else {
                presenter.dialogOnAddButtonPressed(addTimeTextView.getText().toString(),
                        addDateTextView.getText().toString(), readingTextView.getText().toString(),
                        readingTypeSpinner.getSpinner().getSelectedItem().toString(), notesEditText.getText().toString());
            }
        }
    }

    public void showErrorMessage() {
        View rootLayout = findViewById(android.R.id.content);
        Snackbar.make(rootLayout, getString(R.string.dialog_error2), Snackbar.LENGTH_SHORT).show();
    }


    public void showDuplicateErrorMessage() {
        View rootLayout = findViewById(android.R.id.content);
        Snackbar.make(rootLayout, getString(R.string.dialog_error_duplicate), Snackbar.LENGTH_SHORT).show();
    }

    public void updateSpinnerTypeTime(int selection) {
        readingTypeSpinner.setSelection(selection);
    }

    private void updateSpinnerTypeHour(int hour) {
        AddGlucosePresenter presenter = (AddGlucosePresenter) getPresenter();
        readingTypeSpinner.setSelection(presenter.hourToSpinnerType(hour));
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int seconds) {
        super.onTimeSet(view, hourOfDay, minute, seconds);
        DecimalFormat df = new DecimalFormat("00");
        updateSpinnerTypeHour(Integer.parseInt(df.format(hourOfDay)));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doneFAB.removeCallbacks(fabAnimationRunnable);
    }
}
