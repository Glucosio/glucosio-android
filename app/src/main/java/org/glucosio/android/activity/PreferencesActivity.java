package org.glucosio.android.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.glucosio.android.R;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.User;
import org.glucosio.android.tools.InputFilterMinMax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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

    public static class MyPreferenceFragment extends PreferenceFragment {

        private Dialog termsDialog;
        private DatabaseHandler dB;
        private User user;
        private ListPreference countryPref;
        private ListPreference genderPref;
        private ListPreference diabetesTypePref;
        private ListPreference unitPref;
        private ListPreference rangePref;
        private EditText ageEditText;
        private EditText minEditText;
        private EditText maxEditText;
        private EditTextPreference agePref;
        private EditTextPreference minRangePref;
        private EditTextPreference maxRangePref;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            dB = new DatabaseHandler();
            user = dB.getUser(1);

            agePref = (EditTextPreference) findPreference("pref_age");
            countryPref = (ListPreference) findPreference("pref_country");
            genderPref = (ListPreference) findPreference("pref_gender");
            diabetesTypePref = (ListPreference) findPreference("pref_diabetes_type");
            unitPref = (ListPreference) findPreference("pref_unit");
            rangePref = (ListPreference) findPreference("pref_range");
            minRangePref = (EditTextPreference) findPreference("pref_range_min");
            maxRangePref = (EditTextPreference) findPreference("pref_range_max");

            agePref.setDefaultValue(user.get_age());
            countryPref.setValue(user.get_country());
            genderPref.setValue(user.get_gender());
            diabetesTypePref.setValue(user.get_d_type() + "");
            unitPref.setValue(user.get_preferred_unit());
            agePref.setDefaultValue(user.get_age());
            countryPref.setValue(user.get_country());
            genderPref.setValue(user.get_gender());
            unitPref.setValue(user.get_preferred_unit());
            rangePref.setValue(user.get_preferred_range());
            minRangePref.setDefaultValue(user.get_custom_range_min() + "");
            maxRangePref.setDefaultValue(user.get_custom_range_max() + "");

            if (!rangePref.equals("custom")){
                minRangePref.setEnabled(false);
                maxRangePref.setEnabled(false);
            } else {
                minRangePref.setEnabled(true);
                maxRangePref.setEnabled(true);
            }

            final Preference termsPref = (Preference) findPreference("preferences_terms");

            countryPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    user.set_country(newValue.toString());

                    updateDB();
                    return false;
                }
            });
            agePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    user.set_age(Integer.parseInt(newValue.toString()));
                    updateDB();
                    return false;
                }
            });
            genderPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    user.set_gender(newValue.toString());
                    updateDB();
                    return false;
                }
            });
            diabetesTypePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().equals(getResources().getString(R.string.helloactivity_spinner_diabetes_type_1))) {
                        user.set_d_type(1);
                        updateDB();
                    } else {
                        user.set_d_type(2);
                        updateDB();
                    }
                    return false;
                }
            });
            unitPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    user.set_preferred_unit(newValue.toString());
                    updateDB();
                    return false;
                }
            });
            rangePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    user.set_preferred_range(newValue.toString());
                    updateDB();
                    return false;
                }
            });
            minRangePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    user.set_custom_range_min(Integer.parseInt(newValue.toString()));
                    updateDB();
                    return false;
                }
            });
            maxRangePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    user.set_custom_range_max(Integer.parseInt(newValue.toString()));
                    updateDB();
                    return false;
                }
            });

            ageEditText = agePref.getEditText();
            minEditText = minRangePref.getEditText();
            maxEditText = maxRangePref.getEditText();

            ageEditText.setFilters(new InputFilter[]{new InputFilterMinMax(1, 110)});
            minEditText.setFilters(new InputFilter[]{new InputFilterMinMax(1, 1500)});
            maxEditText.setFilters(new InputFilter[]{new InputFilterMinMax(1, 1500)});


            // Get countries list from locale
            ArrayList<String> countriesArray = new ArrayList<String>();
            Locale[] locales = Locale.getAvailableLocales();

            for (Locale locale : locales) {
                String country = locale.getDisplayCountry();
                if (country.trim().length()>0 && !countriesArray.contains(country)) {
                    countriesArray.add(country);
                }
            }
            Collections.sort(countriesArray);

            CharSequence[] countries = countriesArray.toArray(new CharSequence[countriesArray.size()]);
            countryPref.setEntryValues(countries);
            countryPref.setEntries(countries);
            updateDB();

            termsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), LicenceActivity.class);
                    startActivity(intent);

                    return false;
                }
            });
        }

        private void updateDB() {
            dB.updateUser(user);
            agePref.setSummary(user.get_age() + "");
            genderPref.setSummary(user.get_gender() + "");
            diabetesTypePref.setSummary(getResources().getString(R.string.glucose_reading_type) + " " + user.get_d_type());
            unitPref.setSummary(user.get_preferred_unit() + "");
            countryPref.setSummary(user.get_country());
            rangePref.setSummary(user.get_preferred_range() + "");
            minRangePref.setSummary(user.get_custom_range_min() + "");
            maxRangePref.setSummary(user.get_custom_range_max() + "");

            countryPref.setValue(user.get_country());
            genderPref.setValue(user.get_gender());
            diabetesTypePref.setValue(user.get_d_type() + "");
            unitPref.setValue(user.get_preferred_unit());
            countryPref.setValue(user.get_country());
            genderPref.setValue(user.get_gender());
            unitPref.setValue(user.get_preferred_unit());
            rangePref.setValue(user.get_preferred_range());

            if (!user.get_preferred_range().equals("Custom range")){
                minRangePref.setEnabled(false);
                maxRangePref.setEnabled(false);
            } else {
                minRangePref.setEnabled(true);
                maxRangePref.setEnabled(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() ==  android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

}