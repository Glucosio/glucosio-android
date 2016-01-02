package org.glucosio.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.glucosio.android.R;
import org.glucosio.android.presenter.AddKetonePresenter;
import org.glucosio.android.presenter.AddWeightPresenter;
import org.glucosio.android.tools.FormatDateTime;

import java.text.DecimalFormat;
import java.util.Calendar;

public class AddKetoneActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private FloatingActionButton doneFAB;
    private TextView addTimeTextView;
    private TextView addDateTextView;
    private TextView readingTextView;

    AddKetonePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ketone);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(2);
        }

        presenter = new AddKetonePresenter(this);
        presenter.getCurrentTime();

        doneFAB = (FloatingActionButton) findViewById(R.id.done_fab);
        addTimeTextView = (TextView) findViewById(R.id.dialog_add_time);
        addDateTextView = (TextView) findViewById(R.id.dialog_add_date);
        readingTextView = (TextView) findViewById(R.id.ketone_add_value);

        FormatDateTime formatDateTime = new FormatDateTime(getApplicationContext());
        addDateTextView.setText(presenter.getReadingDay() + "/" + presenter.getReadingMonth() + "/" + presenter.getReadingYear());
        addTimeTextView.setText(formatDateTime.getCurrentTime());
        addDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddKetoneActivity.this,
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
                    TimePickerDialog tpd = TimePickerDialog.newInstance(AddKetoneActivity.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
                    tpd.show(getFragmentManager(), "Timepickerdialog");
                } else {
                    TimePickerDialog tpd = TimePickerDialog.newInstance(AddKetoneActivity.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
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
    }

    private void dialogOnAddButtonPressed() {
            presenter.dialogOnAddButtonPressed(addTimeTextView.getText().toString(),
                    addDateTextView.getText().toString(), readingTextView.getText().toString());
    }

    public void showErrorMessage() {
        Toast.makeText(getApplicationContext(), getString(R.string.dialog_error2), Toast.LENGTH_SHORT).show();
    }

    public void finishActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        TextView addTime = (TextView) findViewById(R.id.dialog_add_time);
        DecimalFormat df = new DecimalFormat("00");

        presenter.setReadingHour(df.format(hourOfDay));
        presenter.setReadingMinute(df.format(minute));

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        FormatDateTime formatDateTime = new FormatDateTime(getApplicationContext());
        addTime.setText(formatDateTime.getTime(cal));
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        TextView addDate = (TextView) findViewById(R.id.dialog_add_date);
        DecimalFormat df = new DecimalFormat("00");

        presenter.setReadingYear(year + "");
        presenter.setReadingMonth(df.format(monthOfYear + 1));
        presenter.setReadingDay(df.format(dayOfMonth));

        String date = +dayOfMonth + "/" + presenter.getReadingMonth() + "/" + presenter.getReadingYear();
        addDate.setText(date);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
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
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }
}
