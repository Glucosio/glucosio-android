package org.glucosio.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.glucosio.android.R;
import org.glucosio.android.presenter.ExportPresenter;

import java.text.DecimalFormat;
import java.util.Calendar;

public class ExportActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    ExportPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(2);
        }

        presenter = new ExportPresenter(this);

        TextView dialogDateFrom = (TextView) findViewById(R.id.dialog_export_date_from);
        TextView dialogDateTo = (TextView) findViewById(R.id.dialog_export_date_to);
        FloatingActionButton fabExport = (FloatingActionButton) findViewById(R.id.export_fab);

        dialogDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        ExportActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "fromDateDialog");
                dpd.setMaxDate(now);
            }
        });

        dialogDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        ExportActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "toDateDialog");
                dpd.setMaxDate(now);
            }
        });

        fabExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFabClicked();
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if (view.getTag().equals("fromDateDialog")){
            TextView dateTextView = (TextView) findViewById(R.id.dialog_export_date_from);
            DecimalFormat df = new DecimalFormat("00");

            presenter.setFromYear(year);
            presenter.setFromMonth(monthOfYear);
            presenter.setFromDay(dayOfMonth);

            int monthToShow = monthOfYear +1;
            String date = +dayOfMonth+"/"+monthToShow+"/"+year;
            dateTextView.setText(date);
        } else {
            TextView dateTextView = (TextView) findViewById(R.id.dialog_export_date_to);
            DecimalFormat df = new DecimalFormat("00");

            presenter.setToYear(year);
            presenter.setToMonth(monthOfYear);
            presenter.setToDay(dayOfMonth);

            int monthToShow = monthOfYear +1;
            String date = +dayOfMonth+"/"+monthToShow+"/"+year;
            dateTextView.setText(date);
        }
    }

    public void showShareDialog(String finalJson) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.share_subject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, finalJson);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
    }
}
