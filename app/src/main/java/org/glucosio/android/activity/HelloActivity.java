package org.glucosio.android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.glucosio.android.R;

public class HelloActivity extends AppCompatActivity {

    Spinner languageSpinner;
    Spinner genderSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        languageSpinner = (Spinner) findViewById(R.id.helloactivity_spinner_language);
        genderSpinner = (Spinner) findViewById(R.id.helloactivity_spinner_gender);

        // Populate Spinner with languages list
        populateLanguageSpinner();

        // Populate Spinner with gender list
        populateGenderSpinner();

    }

    private void populateLanguageSpinner() {

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> languagesAdapter = ArrayAdapter
                .createFromResource(this, R.array.helloactivity_language_list,
                        android.R.layout.simple_spinner_item);

        languagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languagesAdapter);
    }

    private void populateGenderSpinner() {

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> gendersAdapter = ArrayAdapter
                .createFromResource(this, R.array.helloactivity_gender_list,
                        android.R.layout.simple_spinner_item);

        gendersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(gendersAdapter);
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
