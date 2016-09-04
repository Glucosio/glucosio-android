package org.glucosio.android.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.glucosio.android.R;
import org.glucosio.android.presenter.RemindersPresenter;
import org.glucosio.android.tools.FormatDateTime;

import java.util.Calendar;

public class RemindersActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    FloatingActionButton addFab;
    RemindersPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        presenter = new RemindersPresenter(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(2);
        }

        addFab = (FloatingActionButton) findViewById(R.id.activity_reminders_fab_add);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Time Picker on FAB click
                boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(getApplicationContext());
                Calendar cal = presenter.getCalendar();

                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        RemindersActivity.this,
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        is24HourFormat);
                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int seconds) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        FormatDateTime formatDateTime = new FormatDateTime(getApplicationContext());
    }
}
