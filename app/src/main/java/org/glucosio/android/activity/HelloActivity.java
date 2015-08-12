package org.glucosio.android.activity;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.glucosio.android.R;
import org.glucosio.android.tools.LabelledSpinner;

public class HelloActivity extends AppCompatActivity {

    LabelledSpinner languageSpinner;
    LabelledSpinner genderSpinner;
    TextView ageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        languageSpinner = (LabelledSpinner) findViewById(R.id.helloactivity_spinner_language);
        genderSpinner = (LabelledSpinner) findViewById(R.id.helloactivity_spinner_gender);
        ageTextView = (TextView) findViewById(R.id.helloactivity_age);

        // Populate Spinner with languages list
        languageSpinner.setItemsArray(R.array.helloactivity_language_list);

        // Populate Spinner with gender list
        genderSpinner.setItemsArray(R.array.helloactivity_gender_list);

    }

    private boolean validateAge(){
        if (TextUtils.isEmpty(ageTextView.getText())){
            setError(ageTextView, getString(R.string.helloactivity_age_invalid));
            return false;
        } else if (!TextUtils.isDigitsOnly(ageTextView.getText())){
            setError(ageTextView, getString(R.string.helloactivity_age_invalid));
            return false;
        } else {
            int age = Integer.parseInt(ageTextView.getText().toString());
            if (age > 0 && age < 120) {
                return true;
            } else {
                setError(ageTextView, getString(R.string.helloactivity_age_invalid));
                return false;
            }
        }
    }

    private void setError(TextView view, String text) {
        TextInputLayout parent = (TextInputLayout) view.getParent();
        parent.setError(text);
    }

    private void removeError(TextView view) {
        TextInputLayout parent = (TextInputLayout) view.getParent();
        parent.setError(null);
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
