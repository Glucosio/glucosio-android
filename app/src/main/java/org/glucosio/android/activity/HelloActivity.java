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
import org.glucosio.android.presenter.HelloPresenter;
import org.glucosio.android.tools.LabelledSpinner;

public class HelloActivity extends AppCompatActivity {

    LabelledSpinner languageSpinner;
    LabelledSpinner genderSpinner;
    LabelledSpinner typeSpinner;
    LabelledSpinner unitSpinner;
    TextView ageTextView;
    Button nextButton;
    HelloPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        presenter = new HelloPresenter(this);
        presenter.loadDatabase();

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
            presenter.onNextClicked(ageTextView.getText().toString(),
                    genderSpinner.getSpinner().getSelectedItemPosition(), languageSpinner.getSpinner().getSelectedItem().toString());
    }

    public void displayErrorMessage(){
        Toast.makeText(getApplicationContext(), getString(R.string.helloactivity_age_invalid), Toast.LENGTH_SHORT).show();
    }

    public void closeHelloActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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
