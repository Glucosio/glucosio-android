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
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
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
import org.glucosio.android.tools.SplitDateTime;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddGlucoseActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private AddGlucosePresenter presenter;
    private FloatingActionButton doneFAB;
    private TextView addTimeTextView;
    private TextView addDateTextView;
    private TextView readingTextView;
    private EditText typeCustomEditText;
    private EditText notesEditText;
    private TextInputLayout readingInputLayout;
    private LabelledSpinner readingTypeSpinner;
    private int pagerPosition = 0;
    private long editId = 0;
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


        Bundle b = getIntent().getExtras();
        if (b!=null) {
            pagerPosition = b.getInt("pager");
            editId = b.getLong("edit_id");
        }

        presenter = new AddGlucosePresenter(this);

        readingTypeSpinner = (LabelledSpinner) findViewById(R.id.glucose_add_reading_type);
        readingTypeSpinner.setItemsArray(R.array.dialog_add_measured_list);

        doneFAB = (FloatingActionButton) findViewById(R.id.done_fab);
        addTimeTextView = (TextView) findViewById(R.id.glucose_add_time);
        addDateTextView = (TextView) findViewById(R.id.glucose_add_date);
        readingTextView = (TextView) findViewById(R.id.glucose_add_concentration);
        typeCustomEditText = (EditText) findViewById(R.id.glucose_type_custom);
        readingInputLayout = (TextInputLayout) findViewById(R.id.glucose_add_concentration_layout);
        notesEditText = (EditText) findViewById(R.id.glucose_add_notes);

        presenter.updateSpinnerTypeTime();
        this.isCustomType = false;

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
        if (editId != 0){
            FormatDateTime dateTime = new FormatDateTime(getApplicationContext());
            setTitle(R.string.title_activity_add_glucose_edit);
            GlucoseReading readingToEdit = presenter.getGlucoseReadingById(editId);
            readingTextView.setText(readingToEdit.getReading()+"");
            notesEditText.setText(readingToEdit.getNotes());
            Calendar cal = Calendar.getInstance();
            cal.setTime(readingToEdit.getCreated());
            addDateTextView.setText(dateTime.getDate(cal));
            addTimeTextView.setText(dateTime.getTime(cal));
            SplitDateTime splitDateTime = new SplitDateTime(readingToEdit.getCreated(), new SimpleDateFormat("yyyy-MM-dd"));
            presenter.setReadingDay(splitDateTime.getDay());
            presenter.setReadingHour(splitDateTime.getHour());
            presenter.setReadingMinute(splitDateTime.getMinute());
            presenter.setReadingYear(splitDateTime.getYear());
            presenter.setReadingMonth(splitDateTime.getMonth());
        }


        doneFAB.post(new Runnable() {
            @Override
            public void run() {
                AnimationTools.startCircularReveal(doneFAB);
            }
        });
    }

    private void addAnalyticsEvent() {
        Analytics analytics = ((GlucosioApplication) getApplication()).getAnalytics();
        analytics.reportAction("FreeStyle Libre", "New reading added");
    }

    private void dialogOnAddButtonPressed() {
        boolean isEdited = editId!=0;
        if (isCustomType) {
            if (isEdited) {
                presenter.dialogOnAddButtonPressed(addTimeTextView.getText().toString(),
                        addDateTextView.getText().toString(), readingTextView.getText().toString(),
                        typeCustomEditText.getText().toString(), notesEditText.getText().toString(), editId);
            } else {
                presenter.dialogOnAddButtonPressed(addTimeTextView.getText().toString(),
                        addDateTextView.getText().toString(), readingTextView.getText().toString(),
                        typeCustomEditText.getText().toString(), notesEditText.getText().toString());
            }
        } else {
            if (isEdited) {
                presenter.dialogOnAddButtonPressed(addTimeTextView.getText().toString(),
                        addDateTextView.getText().toString(), readingTextView.getText().toString(),
                        readingTypeSpinner.getSpinner().getSelectedItem().toString(), notesEditText.getText().toString(), editId);
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
        readingTypeSpinner.setSelection(presenter.hourToSpinnerType(hour));
    }

    public void finishActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        // Pass pager position to open it again later
        Bundle b = new Bundle();
        b.putInt("pager", pagerPosition);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int seconds) {
        TextView addTime = (TextView) findViewById(R.id.glucose_add_time);
        DecimalFormat df = new DecimalFormat("00");

        presenter.setReadingHour(df.format(hourOfDay));
        presenter.setReadingMinute(df.format(minute));

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        FormatDateTime formatDateTime = new FormatDateTime(getApplicationContext());
        addTime.setText(formatDateTime.getTime(cal));
        updateSpinnerTypeHour(Integer.parseInt(df.format(hourOfDay)));
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        TextView addDate = (TextView) findViewById(R.id.glucose_add_date);
        DecimalFormat df = new DecimalFormat("00");

        presenter.setReadingYear(year + "");
        presenter.setReadingMonth(df.format(monthOfYear + 1));
        presenter.setReadingDay(df.format(dayOfMonth));

        FormatDateTime formatDateTime = new FormatDateTime(getApplicationContext());
        String date = presenter.getReadingYear() + "-" + presenter.getReadingMonth() + "-" + dayOfMonth;
        addDate.setText(formatDateTime.convertDate(date));
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
}
