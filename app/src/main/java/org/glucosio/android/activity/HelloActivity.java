package org.glucosio.android.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
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
    View firstView;
    View EULAView;
    CheckBox EULACheckbox;
    Button startButton;
    TextView ageTextView;
    TextView termsTextView;
    Button nextButton;
    HelloPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        presenter = new HelloPresenter(this);
        presenter.loadDatabase();

        firstView = (ScrollView) findViewById(R.id.helloactivity_mainframe);
        EULAView = (ScrollView) findViewById(R.id.helloactivity_eulaframe);
        EULACheckbox = (CheckBox) findViewById(R.id.helloactivity_checkbox_eula);
        languageSpinner = (LabelledSpinner) findViewById(R.id.helloactivity_spinner_language);
        genderSpinner = (LabelledSpinner) findViewById(R.id.helloactivity_spinner_gender);
        typeSpinner = (LabelledSpinner) findViewById(R.id.helloactivity_spinner_diabetes_type);
        unitSpinner = (LabelledSpinner) findViewById(R.id.helloactivity_spinner_preferred_unit);
        startButton = (Button) findViewById(R.id.helloactivity_start);

        termsTextView = (TextView) findViewById(R.id.helloactivity_textview_terms);

        ageTextView = (TextView) findViewById(R.id.helloactivity_age);
        nextButton = (Button) findViewById(R.id.helloactivity_next);

        // Populate Spinners with array
        languageSpinner.setItemsArray(R.array.helloactivity_language_list);
        genderSpinner.setItemsArray(R.array.helloactivity_gender_list);
        unitSpinner.setItemsArray(R.array.helloactivity_preferred_unit);
        typeSpinner.setItemsArray(R.array.helloactivity_diabetes_type);

        termsTextView.setMovementMethod(new ScrollingMovementMethod());
        EULACheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                        if (isChecked) {
                                                            startButton.setEnabled(true);
                                                        } else {
                                                            startButton.setEnabled(false);
                                                        }
                                                    }
                                                }
        );

        //TODO: add Preferred Unit and Diabetes Type in dB
    }

    public void onNextClicked(View v){
        presenter.onNextClicked(ageTextView.getText().toString(),
                genderSpinner.getSpinner().getSelectedItemPosition(), languageSpinner.getSpinner().getSelectedItem().toString());
    }

    public void showEULA(){
        // Prepare the View for the animation
        firstView.animate()
                .alpha(0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        firstView.setVisibility(View.GONE);
                        showEULAAnimation();
                    }
                });
    }

    private void showEULAAnimation() {
        // Prepare the View for the animation
        EULAView.setVisibility(View.VISIBLE);
        EULAView.setAlpha(0.0f);

        EULAView.animate()
                .alpha(1f);
        firstView.setVisibility(View.GONE);
    }


    public void onStartClicked(View v){
        presenter.saveToDatabase();
    }

    public void displayErrorMessage(){
        Toast.makeText(getApplicationContext(), getString(R.string.helloactivity_age_invalid), Toast.LENGTH_SHORT).show();
    }

    public void closeHelloActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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
