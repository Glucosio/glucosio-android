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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.glucosio.android.GlucosioApplication;
import org.glucosio.android.R;
import org.glucosio.android.analytics.Analytics;
import org.glucosio.android.presenter.HelloPresenter;
import org.glucosio.android.tools.LabelledSpinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HelloActivity extends AppCompatActivity {

    @BindView(R.id.activity_hello_spinner_country)
    LabelledSpinner countrySpinner;

    @BindView(R.id.activity_hello_spinner_gender)
    LabelledSpinner genderSpinner;

    @BindView(R.id.activity_hello_spinner_diabetes_type)
    LabelledSpinner typeSpinner;

    @BindView(R.id.activity_hello_spinner_preferred_unit)
    LabelledSpinner unitSpinner;

    @BindView(R.id.activity_hello_button_start)
    Button startButton;

    @BindView(R.id.activity_hello_age)
    TextView ageTextView;

    @BindView(R.id.helloactivity_textview_terms)
    TextView termsTextView;

    private HelloPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        ButterKnife.bind(this);

        // Prevent SoftKeyboard to pop up on start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        GlucosioApplication application = (GlucosioApplication) getApplication();
        presenter = new HelloPresenter(this, application.getDBHandler());
        presenter.loadDatabase();

        initCountrySpinner();

        genderSpinner.setItemsArray(R.array.helloactivity_gender_list);
        unitSpinner.setItemsArray(R.array.helloactivity_preferred_glucose_unit);
        typeSpinner.setItemsArray(R.array.helloactivity_diabetes_type);

        final Drawable pinkArrow = getApplicationContext().getResources().getDrawable(R.drawable.ic_navigate_next_pink_24px);
        pinkArrow.setBounds(0, 0, 60, 60);
        startButton.setCompoundDrawables(null, null, pinkArrow, null);

        termsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelloActivity.this, LicenceActivity.class);
                startActivity(intent);
            }
        });

        Analytics analytics = application.getAnalytics();
        analytics.reportScreen("Hello Activity");
        Log.i("HelloActivity", "Setting screen name: hello");
    }

    private void initCountrySpinner() {
        // Get countries list from locale
        ArrayList<String> countries = new ArrayList<>();
        Locale[] locales = Locale.getAvailableLocales();

        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();

            if ((country.trim().length() > 0) && (!countries.contains(country))) {
                countries.add(country);
            }
        }

        Collections.sort(countries);

        // Populate Spinners with array
        countrySpinner.setItemsArray(countries);

        // Get locale country name and set the spinner
        String localCountry = getApplicationContext().getResources().getConfiguration().locale.getDisplayCountry();

        if (localCountry != null) {
            int position = ((ArrayAdapter) countrySpinner.getSpinner().getAdapter()).getPosition(localCountry);
            countrySpinner.setSelection(position);
        }
    }

    @OnClick(R.id.activity_hello_button_start)
    void onStartClicked() {
        presenter.onNextClicked(ageTextView.getText().toString(),
                genderSpinner.getSpinner().getSelectedItem().toString(),
                Locale.getDefault().getDisplayLanguage(),
                countrySpinner.getSpinner().getSelectedItem().toString(),
                typeSpinner.getSpinner().getSelectedItemPosition() + 1,
                unitSpinner.getSpinner().getSelectedItem().toString());
    }

    public void displayErrorMessage() {
        Toast.makeText(getApplicationContext(), getString(R.string.helloactivity_age_invalid), Toast.LENGTH_SHORT).show();
    }

    public void closeHelloActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
