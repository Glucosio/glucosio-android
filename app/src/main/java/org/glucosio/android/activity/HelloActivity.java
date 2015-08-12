package org.glucosio.android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.glucosio.android.R;
import org.glucosio.android.tools.LabelledSpinner;

public class HelloActivity extends AppCompatActivity implements LabelledSpinner.OnItemChosenListener {

    LabelledSpinner languageSpinner;
    LabelledSpinner genderSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        languageSpinner = (LabelledSpinner) findViewById(R.id.helloactivity_spinner_language);
        genderSpinner = (LabelledSpinner) findViewById(R.id.helloactivity_spinner_gender);

        // Populate Spinner with languages list
        populateLanguageSpinner();

        // Populate Spinner with gender list
        populateGenderSpinner();

    }

    private void populateLanguageSpinner() {
        languageSpinner.setItemsArray(R.array.helloactivity_language_list);
        languageSpinner.setOnItemChosenListener(this);
    }

    private void populateGenderSpinner() {
        genderSpinner.setItemsArray(R.array.helloactivity_gender_list);
        genderSpinner.setOnItemChosenListener(this);
    }


    @Override
    public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
        String selectedText = adapterView.getItemAtPosition(position).toString();
        switch (labelledSpinner.getId()) {
            case R.id.helloactivity_spinner_language:
                // Do something here
                break;
            // If you have multiple LabelledSpinners, you can add more cases here
        }
    }

    @Override
    public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {
        // Do something here
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hello, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
