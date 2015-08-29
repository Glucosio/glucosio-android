package org.glucosio.android.activity;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.glucosio.android.R;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.User;
import org.glucosio.android.tools.LabelledSpinner;

public class HelloActivity extends AppCompatActivity {

    int id;
    int age;
    String name;
    String country;
    int gender;
    String language;

    LabelledSpinner languageSpinner;
    LabelledSpinner genderSpinner;
    LabelledSpinner typeSpinner;
    LabelledSpinner unitSpinner;
    TextView ageTextView;
    Button nextButton;

    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        db = new DatabaseHandler(this);
        id = 1; // Id is always 1. We don't support multi-user (for now :D).
        name = "Test Account"; //TODO: add input for name in Tips;

        languageSpinner = (LabelledSpinner) findViewById(R.id.helloactivity_spinner_language);
        genderSpinner = (LabelledSpinner) findViewById(R.id.helloactivity_spinner_gender);
        typeSpinner = (LabelledSpinner) findViewById(R.id.helloactivity_spinner_diabetes_type);
        unitSpinner = (LabelledSpinner) findViewById(R.id.helloactivity_spinner_preferred_unit);
        ageTextView = (TextView) findViewById(R.id.helloactivity_age);
        nextButton = (Button) findViewById(R.id.helloactivity_next);

        // Populate Spinners with array
        languageSpinner.setItemsArray(R.array.helloactivity_language_list);
        genderSpinner.setItemsArray(R.array.helloactivity_gender_list);
        unitSpinner.setItemsArray(R.array.helloactivity_preferred_unit);
        typeSpinner.setItemsArray(R.array.helloactivity_diabetes_type);

        //TODO: add Preferred Unit and Diabetes Type in dB
    }

    public void onNextClicked(View v){
        if (validateAge()){
            this.age = Integer.parseInt(ageTextView.getText().toString());
            this.gender = genderToInt();
            this.language = languageSpinner.getSpinner().getSelectedItem().toString();

            saveToDatabase();
        } else {
            //TODO: find out why setError doesn't work :(
            Toast.makeText(getApplicationContext(), getString(R.string.helloactivity_age_invalid), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToDatabase(){
        db.addUser(new User(id, name, language, country, age, gender));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validateAge(){
        if (TextUtils.isEmpty(ageTextView.getText())){
            return false;
        } else if (!TextUtils.isDigitsOnly(ageTextView.getText())){
            return false;
        } else {
            int age = Integer.parseInt(ageTextView.getText().toString());
            return age > 0 && age < 120;
        }
    }

    private int genderToInt(){
        String genderString = genderSpinner.getSpinner().getSelectedItem().toString();
        int genderInt;
        if (genderString.equals(getString(R.string.helloactivity_gender_list_1))) {
            genderInt = 1;
        } else if (genderString.equals(getString(R.string.helloactivity_gender_list_2))) {
            genderInt = 2;
        } else {
            genderInt = 3;
        }
        return  genderInt;
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
