package org.glucosio.android.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import org.glucosio.android.GlucosioApplication;
import org.glucosio.android.R;
import org.glucosio.android.analytics.Analytics;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.User;
import org.glucosio.android.tools.InputFilterMinMax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

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

        // Obtain the Analytics shared Tracker instance.
        GlucosioApplication application = (GlucosioApplication) getApplication();
        Analytics analytics = application.getAnalytics();
        Log.i("MainActivity", "Setting screen name: " + "main");
        analytics.reportScreen("Preferences");
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        private DatabaseHandler dB;
        private User user;
        private ListPreference languagePref;
        /*
                private Preference backupPref;
        */
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
        private SwitchPreference dyslexiaModePref;
        private SwitchPreference freestyleLibrePref;
        private User updatedUser;


        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            dB = new DatabaseHandler(getActivity().getApplicationContext());
            user = dB.getUser(1);
            updatedUser = new User(user.getId(), user.getName(), user.getPreferred_language(), user.getCountry(), user.getAge(), user.getGender(), user.getD_type(), user.getPreferred_unit(), user.getPreferred_range(), user.getCustom_range_min(), user.getCustom_range_max());
            agePref = (EditTextPreference) findPreference("pref_age");
            countryPref = (ListPreference) findPreference("pref_country");
            // languagePref = (ListPreference) findPreference("pref_language");
            // backupPref = (Preference) findPreference("backup_settings");
            genderPref = (ListPreference) findPreference("pref_gender");
            diabetesTypePref = (ListPreference) findPreference("pref_diabetes_type");
            unitPref = (ListPreference) findPreference("pref_unit");
            rangePref = (ListPreference) findPreference("pref_range");
            minRangePref = (EditTextPreference) findPreference("pref_range_min");
            maxRangePref = (EditTextPreference) findPreference("pref_range_max");
            dyslexiaModePref = (SwitchPreference) findPreference("pref_font_dyslexia");
            freestyleLibrePref = (SwitchPreference) findPreference("pref_freestyle_libre");

            agePref.setDefaultValue(user.getAge());
            countryPref.setValue(user.getCountry());
            // languagePref.setValue(user.getPreferred_language());
            genderPref.setValue(user.getGender());
            diabetesTypePref.setValue(user.getD_type() + "");
            unitPref.setValue(user.getPreferred_unit());
            rangePref.setValue(user.getPreferred_range());

            minRangePref.setDefaultValue(user.getCustom_range_min() + "");
            maxRangePref.setDefaultValue(user.getCustom_range_max() + "");
            minRangePref.setDefaultValue(user.getCustom_range_min() + "");
            maxRangePref.setDefaultValue(user.getCustom_range_max() + "");

            if (!rangePref.equals("custom")) {
                minRangePref.setEnabled(false);
                maxRangePref.setEnabled(false);
            } else {
                minRangePref.setEnabled(true);
                maxRangePref.setEnabled(true);
            }

/*
            final Preference backupPref = (Preference) findPreference("backup_settings");
*/
            final Preference aboutPref = (Preference) findPreference("about_settings");
            /*languagePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    int position = languagePref.findIndexOfValue(newValue.toString());
                    String lang_code = getResources().getStringArray(R.array.array_languages_code)[position];

                    updatedUser.setPreferred_language(lang_code);
                    updateDB();
                    return true;
                }
            });*/
            countryPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updatedUser.setCountry(newValue.toString());

                    updateDB();
                    return false;
                }
            });
            agePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().trim().equals("")) {
                        return false;
                    }
                    updatedUser.setAge(Integer.parseInt(newValue.toString()));
                    updateDB();
                    return true;
                }
            });
            genderPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updatedUser.setGender(newValue.toString());
                    updateDB();
                    return true;
                }
            });
            diabetesTypePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().equals(getResources().getString(R.string.helloactivity_spinner_diabetes_type_1))) {
                        updatedUser.setD_type(1);
                        updateDB();
                    } else {
                        updatedUser.setD_type(2);
                        updateDB();
                    }
                    return true;
                }
            });
            unitPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updatedUser.setPreferred_unit(newValue.toString());
                    updateDB();
                    return true;
                }
            });
            rangePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updatedUser.setPreferred_range(newValue.toString());
                    updateDB();
                    return true;
                }
            });
            minRangePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().trim().equals("")) {
                        return false;
                    }
                    updatedUser.setCustom_range_min(Integer.parseInt(newValue.toString()));
                    updateDB();
                    return true;
                }
            });
            maxRangePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().trim().equals("")) {
                        return false;
                    }
                    updatedUser.setCustom_range_max(Integer.parseInt(newValue.toString()));
                    updateDB();
                    return true;
                }
            });
            dyslexiaModePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // EXPERIMENTAL PREFERENCE
                    // Display Alert
                    showExperimentalDialog(true);
                    return true;
                }
            });
            freestyleLibrePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (!((SwitchPreference) preference).isChecked()) {
                        // EXPERIMENTAL PREFERENCE
                        // Display Alert
                        showExperimentalDialog(false);
                        return true;
                    }
                    return true;
                }
            });
/*            backupPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent backupActivity = new Intent(getActivity(), BackupActivity.class);
                    getActivity().startActivity(backupActivity);
                    return false;
                }
            });*/


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
                if (country.trim().length() > 0 && !countriesArray.contains(country)) {
                    countriesArray.add(country);
                }
            }
            Collections.sort(countriesArray);

            CharSequence[] countries = countriesArray.toArray(new CharSequence[countriesArray.size()]);
            countryPref.setEntryValues(countries);
            countryPref.setEntries(countries);

/*            CharSequence[] languages = getActivity().getResources().getStringArray(R.array.array_languages_name);
            languagePref.setEntryValues(languages);
            languagePref.setEntries(languages);*/
            updateDB();

/*            backupPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent backupActivity = new Intent(getActivity(), BackupActivity.class);
                    getActivity().startActivity(backupActivity);
                    return false;
                }
            });*/

            aboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent aboutActivity = new Intent(getActivity(), AboutActivity.class);
                    getActivity().startActivity(aboutActivity);
                    return false;
                }
            });
        }

        private void updateDB() {
            dB.updateUser(updatedUser);
            agePref.setSummary(user.getAge() + "");
            genderPref.setSummary(user.getGender() + "");
            diabetesTypePref.setSummary(getResources().getString(R.string.glucose_reading_type) + " " + user.getD_type());
            unitPref.setSummary(user.getPreferred_unit() + "");
            countryPref.setSummary(user.getCountry());
/*
            languagePref.setSummary(user.getPreferred_language());
*/
            rangePref.setSummary(user.getPreferred_range() + "");
            minRangePref.setSummary(user.getCustom_range_min() + "");
            maxRangePref.setSummary(user.getCustom_range_max() + "");

            countryPref.setValue(user.getCountry());
/*
            languagePref.setValue(user.getPreferred_language());
*/
            genderPref.setValue(user.getGender());
            diabetesTypePref.setValue(user.getD_type() + "");
            unitPref.setValue(user.getPreferred_unit());
            genderPref.setValue(user.getGender());
            unitPref.setValue(user.getPreferred_unit());
            rangePref.setValue(user.getPreferred_range());

            if (!user.getPreferred_range().equals("Custom range")) {
                minRangePref.setEnabled(false);
                maxRangePref.setEnabled(false);
            } else {
                minRangePref.setEnabled(true);
                maxRangePref.setEnabled(true);
            }
        }

        private void showExperimentalDialog(final boolean restartRequired) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.preferences_experimental_title))
                    .setMessage(R.string.preferences_experimental)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (restartRequired) {
                                rebootApp();
                            }
                        }
                    })
                    .show();
        }

        private void rebootApp() {
            Intent mStartActivity = new Intent(getActivity().getApplicationContext(), MainActivity.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) getActivity().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
            System.exit(0);
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
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}