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

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import org.glucosio.android.R;
import org.glucosio.android.db.PressureReading;
import org.glucosio.android.presenter.AddPressurePresenter;
import org.glucosio.android.tools.FormatDateTime;

import java.util.Calendar;

public class AddPressureActivity extends AddReadingActivity {

    private TextView minPressureTextView;
    private TextView maxPressureTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pressure);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(2);
        }

        this.retrieveExtra();

        AddPressurePresenter presenter = new AddPressurePresenter(this);
        setPresenter(presenter);
        presenter.setReadingTimeNow();

        minPressureTextView = (TextView) findViewById(R.id.pressure_add_value_min);
        maxPressureTextView = (TextView) findViewById(R.id.pressure_add_value_max);

        this.createDateTimeViewAndListener();
        this.createFANViewAndListener();

        // Initialize value
        FormatDateTime formatDateTime = new FormatDateTime(getApplicationContext());
        if (this.isEditing()) {
            // set edit title
            setTitle(R.string.title_activity_add_pressure_edit);
            PressureReading readingToEdit = presenter.getPressureReadingById(this.getEditId());

            // set reading values
            minPressureTextView.setText(readingToEdit.getMinReading() + "");
            maxPressureTextView.setText(readingToEdit.getMaxReading() + "");

            // set reading time
            Calendar cal = Calendar.getInstance();
            cal.setTime(readingToEdit.getCreated());
            this.getAddDateTextView().setText(formatDateTime.getDate(cal));
            this.getAddTimeTextView().setText(formatDateTime.getTime(cal));
            presenter.updateReadingSplitDateTime(readingToEdit.getCreated());
        } else {
            this.getAddDateTextView().setText(formatDateTime.getCurrentDate());
            this.getAddTimeTextView().setText(formatDateTime.getCurrentTime());
        }

    }

    @Override
    protected void dialogOnAddButtonPressed() {
        AddPressurePresenter presenter = (AddPressurePresenter) getPresenter();
        // If an id is passed, open the activity in edit mode
        if (this.isEditing()) {
            presenter.dialogOnAddButtonPressed(this.getAddTimeTextView().getText().toString(),
                    this.getAddDateTextView().getText().toString(), minPressureTextView.getText().toString(), maxPressureTextView.getText().toString(), this.getEditId());
        } else {
            presenter.dialogOnAddButtonPressed(this.getAddTimeTextView().getText().toString(),
                    this.getAddDateTextView().getText().toString(), minPressureTextView.getText().toString(), maxPressureTextView.getText().toString());
        }
    }

    public void showErrorMessage() {
        Toast.makeText(getApplicationContext(), getString(R.string.dialog_error2), Toast.LENGTH_SHORT).show();
    }
}
