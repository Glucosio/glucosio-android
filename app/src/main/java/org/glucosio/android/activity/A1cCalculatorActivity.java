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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import org.glucosio.android.GlucosioApplication;
import org.glucosio.android.R;
import org.glucosio.android.presenter.A1CCalculatorPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class A1cCalculatorActivity extends AppCompatActivity {

    @BindView(R.id.activity_converter_a1c_glucose_unit)
    TextView glucoseUnit;

    @BindView(R.id.activity_converter_a1c_a1c)
    TextView A1CTextView;

    @BindView(R.id.activity_converter_a1c_a1c_unit)
    TextView A1cUnitTextView;

    private double convertedA1C = 0;
    private A1CCalculatorPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_a1_calculator);
        ButterKnife.bind(this);

        initActionBar();

        GlucosioApplication application = (GlucosioApplication) getApplication();
        presenter = application.createA1cCalculatorPresenter(this);

        if (!"percentage".equals(presenter.getA1cUnit())) {
            A1cUnitTextView.setText(getString(R.string.mmol_mol));
        }

        presenter.checkGlucoseUnit();
    }

    @OnTextChanged(value = R.id.activity_converter_a1c_glucose, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void glucoseValueChanged(@NonNull final Editable s) {
        convertedA1C = presenter.calculateA1C(s.toString());
        A1CTextView.setText(String.valueOf(convertedA1C));
    }

    @SuppressWarnings("UnusedParameters")
    @OnEditorAction(R.id.activity_converter_a1c_glucose)
    boolean editorAction(TextView view, int actionId, KeyEvent event) {
        return actionId == EditorInfo.IME_ACTION_DONE;
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(2);
        }
    }

    public void setMmol() {
        glucoseUnit.setText(getString(R.string.mmol_L));
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput
                    (InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput
                    (InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_converter_a1c, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_save:
                presenter.saveA1C(convertedA1C);
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
