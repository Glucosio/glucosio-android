package org.glucosio.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.glucosio.android.R;
import org.glucosio.android.presenter.A1CCalculatorPresenter;

public class A1Calculator extends AppCompatActivity {

    A1CCalculatorPresenter presenter;
    TextView glucoseUnit;
    double convertedA1C;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a1_calculator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(2);
        }

        presenter = new A1CCalculatorPresenter(this);

        EditText glucoseEditText = (EditText) findViewById(R.id.activity_converter_a1c_glucose);
        glucoseUnit = (TextView) findViewById(R.id.activity_converter_a1c_glucose_unit);
        final TextView A1CTextView = (TextView) findViewById(R.id.activity_converter_a1c_a1c);

        presenter.checkUnit();
        glucoseEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    convertedA1C = presenter.calculateA1C(s.toString());
                    A1CTextView.setText(convertedA1C+"");
                }
            }
        });

        glucoseEditText.setOnEditorActionListener(
            new TextView.OnEditorActionListener() {

                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // your additional processing...
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        glucoseEditText.setFocusable(true);
    }

    public void setMmol(){
        glucoseUnit.setText("mmol/L");
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput
                    (InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput
                    (InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        return super.onKeyDown(keyCode, event);
    }
}
