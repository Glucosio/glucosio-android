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
import org.glucosio.android.db.CholesterolReading;
import org.glucosio.android.presenter.AddCholesterolPresenter;
import org.glucosio.android.tools.FormatDateTime;

import java.util.Calendar;

public class AddCholesterolActivity extends AddReadingActivity {

    private TextView totalChoTextView;
    private TextView LDLChoTextView;
    private TextView HDLChoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cholesterol);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(2);
        }

        this.retrieveExtra();

        AddCholesterolPresenter presenter = new AddCholesterolPresenter(this);
        setPresenter(presenter);
        presenter.setReadingTimeNow();

        totalChoTextView = (TextView) findViewById(R.id.cholesterol_add_value_total);
        LDLChoTextView = (TextView) findViewById(R.id.cholesterol_add_value_ldl);
        HDLChoTextView = (TextView) findViewById(R.id.cholesterol_add_value_hdl);

        this.createDateTimeViewAndListener();
        this.createFANViewAndListener();


        // If an id is passed, open the activity in edit mode
        FormatDateTime formatDateTime = new FormatDateTime(getApplicationContext());
        if (this.isEditing()) {
            FormatDateTime dateTime = new FormatDateTime(getApplicationContext());
            setTitle(R.string.title_activity_add_cholesterol_edit);
            CholesterolReading readingToEdit = presenter.getCholesterolReadingById(this.getEditId());
            totalChoTextView.setText(readingToEdit.getTotalReading() + "");
            LDLChoTextView.setText(readingToEdit.getLDLReading() + "");
            HDLChoTextView.setText(readingToEdit.getHDLReading() + "");
            Calendar cal = Calendar.getInstance();
            cal.setTime(readingToEdit.getCreated());
            this.getAddDateTextView().setText(dateTime.getDate(cal));
            this.getAddTimeTextView().setText(dateTime.getTime(cal));
            presenter.updateReadingSplitDateTime(readingToEdit.getCreated());
        } else {
            this.getAddDateTextView().setText(formatDateTime.getCurrentDate());
            this.getAddTimeTextView().setText(formatDateTime.getCurrentTime());
        }

    }

    @Override
    protected void dialogOnAddButtonPressed() {
        AddCholesterolPresenter presenter = (AddCholesterolPresenter) getPresenter();
        if (this.isEditing()) {
            presenter.dialogOnAddButtonPressed(this.getAddTimeTextView().getText().toString(),
                    this.getAddDateTextView().getText().toString(), totalChoTextView.getText().toString(), LDLChoTextView.getText().toString(), HDLChoTextView.getText().toString(), this.getEditId());
        } else {
            presenter.dialogOnAddButtonPressed(this.getAddTimeTextView().getText().toString(),
                    this.getAddDateTextView().getText().toString(), totalChoTextView.getText().toString(), LDLChoTextView.getText().toString(), HDLChoTextView.getText().toString());
        }
    }

    public void showErrorMessage() {
        Toast.makeText(getApplicationContext(), getString(R.string.dialog_error2), Toast.LENGTH_SHORT).show();
    }
}