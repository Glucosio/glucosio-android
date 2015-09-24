package org.glucosio.android.activity;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.MenuItem;
import android.widget.EditText;

import org.glucosio.android.R;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.tools.InputFilterMinMax;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        getFragmentManager().beginTransaction()
                .replace(R.id.preferencesFrame, new MyPreferenceFragment()).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.action_settings));
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            DatabaseHandler dB = new DatabaseHandler(super.getActivity().getApplicationContext());
            EditTextPreference agePref = (EditTextPreference) findPreference("pref_age");
            ListPreference genderPref = (ListPreference) findPreference("pref_gender");
            ListPreference diabetesTypePref = (ListPreference) findPreference("pref_diabetes_type");
            ListPreference unitPref = (ListPreference) findPreference("pref_unit");

            EditText ageEditText = agePref.getEditText();
            ageEditText.setFilters(new InputFilter[]{ new InputFilterMinMax(1, 110) });
            agePref.setSummary(dB.getUser(1).get_age() + "");
            genderPref.setSummary(dB.getUser(1).get_gender() + "");
            diabetesTypePref.setSummary(getResources().getString(R.string.glucose_reading_type) + " " + dB.getUser(1).get_d_type());
            unitPref.setSummary(dB.getUser(1).get_preferred_unit() + "");
        }
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() ==  android.R.id.home) {
            finish();
        }
        return true;
    }

}