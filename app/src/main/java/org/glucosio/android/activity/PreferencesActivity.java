/*
 * Copyright (C) 2016 Glucosio Foundation
 *
 * This file is part of Glucosio.
 *
 * Glucosio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Glucosio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Glucosio.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

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
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import org.glucosio.android.Constants;
import org.glucosio.android.GlucosioApplication;
import org.glucosio.android.R;
import org.glucosio.android.analytics.Analytics;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.User;
import org.glucosio.android.tools.GlucoseRanges;
import org.glucosio.android.tools.GlucosioConverter;
import org.glucosio.android.tools.InputFilterMinMax;
import org.glucosio.android.tools.LocaleHelper;
import org.glucosio.android.tools.ReadingTools;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PreferencesActivity extends AppCompatActivity {

    @NonNull
    private static String[] getEntryValues(List<String> list) {
        String[] result = new String[list.size()];
        return list.toArray(result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        getFragmentManager().beginTransaction()
                .replace(R.id.preferencesFrame, new MyPreferenceFragment()).commit();

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(getString(R.string.action_settings));
        }

        // Obtain the Analytics shared Tracker instance.
        GlucosioApplication application = (GlucosioApplication) getApplication();
        Analytics analytics = application.getAnalytics();
        Log.i("PreferencesActivity", "Setting screen name: preferences");
        analytics.reportScreen("Preferences");
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

    public static class MyPreferenceFragment extends PreferenceFragment {
        private DatabaseHandler dB;
        private User user;
        private ListPreference languagePref;
        private ListPreference countryPref;
        private ListPreference genderPref;
        private ListPreference diabetesTypePref;
        private ListPreference unitPrefGlucose;
        private ListPreference unitPrefA1c;
        private ListPreference unitPrefWeight;
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
        private LocaleHelper localeHelper;


        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            final GlucosioApplication app = (GlucosioApplication) getActivity().getApplicationContext();
            dB = app.getDBHandler();
            localeHelper = app.getLocaleHelper();
            user = dB.getUser(1);
            updatedUser = new User(user);
            agePref = (EditTextPreference) findPreference("pref_age");
            countryPref = (ListPreference) findPreference("pref_country");
            languagePref = (ListPreference) findPreference("pref_language");
            genderPref = (ListPreference) findPreference("pref_gender");
            diabetesTypePref = (ListPreference) findPreference("pref_diabetes_type");
            unitPrefGlucose = (ListPreference) findPreference("pref_unit_glucose");
            unitPrefA1c = (ListPreference) findPreference("pref_unit_a1c");
            unitPrefWeight = (ListPreference) findPreference("pref_unit_weight");
            rangePref = (ListPreference) findPreference("pref_range");
            minRangePref = (EditTextPreference) findPreference("pref_range_min");
            maxRangePref = (EditTextPreference) findPreference("pref_range_max");
            dyslexiaModePref = (SwitchPreference) findPreference("pref_font_dyslexia");
            freestyleLibrePref = (SwitchPreference) findPreference("pref_freestyle_libre");

            agePref.setDefaultValue(user.getAge());
            countryPref.setValue(user.getCountry());
            genderPref.setValue(user.getGender());
            diabetesTypePref.setValue(String.valueOf(user.getD_type()));
            unitPrefGlucose.setValue(getGlucoseUnitValue(user.getPreferred_unit()));
            unitPrefA1c.setValue(getA1CUnitValue(user.getPreferred_unit_a1c()));
            unitPrefWeight.setValue(getUnitWeight(user.getPreferred_unit_weight()));
            rangePref.setValue(user.getPreferred_range());

            if (Constants.Units.MG_DL.equals(user.getPreferred_unit())) {
                maxRangePref.setDefaultValue(user.getCustom_range_max());
                minRangePref.setDefaultValue(user.getCustom_range_min());
            } else {
                maxRangePref.setDefaultValue(GlucosioConverter.glucoseToMmolL(user.getCustom_range_max()));
                minRangePref.setDefaultValue(GlucosioConverter.glucoseToMmolL(user.getCustom_range_min()));
            }

            if (!"custom".equals(rangePref.getValue())) {
                minRangePref.setEnabled(false);
                maxRangePref.setEnabled(false);
            } else {
                minRangePref.setEnabled(true);
                maxRangePref.setEnabled(true);
            }

            final Preference aboutPref = findPreference("about_settings");
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
                    String[] typesArray = getResources().getStringArray(R.array.helloactivity_diabetes_type);
                    String selectedType = newValue.toString();

                    if (selectedType.equals(typesArray[0])) {
                        updatedUser.setD_type(1);
                        updateDB();
                    } else if (selectedType.equals(typesArray[1])) {
                        updatedUser.setD_type(2);
                        updateDB();
                    } else if (selectedType.equals(typesArray[2])) {
                        updatedUser.setD_type(3);
                        updateDB();
                    } else {
                        updatedUser.setD_type(4);
                        updateDB();
                    }

                    return true;
                }
            });
            unitPrefGlucose.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (getResources().getString(R.string.helloactivity_spinner_preferred_glucose_unit_1).equals(newValue.toString())) {
                        updatedUser.setPreferred_unit(Constants.Units.MG_DL);
                    } else {
                        updatedUser.setPreferred_unit("mmol/L");
                    }
                    updateDB();
                    return true;
                }
            });
            unitPrefA1c.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().equals(getResources().getString(R.string.preferences_spinner_preferred_a1c_unit_1))) {
                        updatedUser.setPreferred_unit_a1c("percentage");
                    } else {
                        updatedUser.setPreferred_unit_a1c("mmol/mol");
                    }
                    updateDB();
                    return true;
                }
            });
            unitPrefWeight.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().equals(getResources().getString(R.string.preferences_spinner_preferred_weight_unit_1))) {
                        updatedUser.setPreferred_unit_weight("kilograms");
                    } else {
                        updatedUser.setPreferred_unit_weight("pounds");
                    }
                    updateDB();
                    return true;
                }
            });
            rangePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String selectedPreset = newValue.toString();
                    updatedUser.setPreferred_range(selectedPreset);

                    // look up the min/max values of the selected preset
                    if (!selectedPreset.equals("Custom range")) {
                        int rangeMin = GlucoseRanges.getPresetMin(selectedPreset);
                        int rangeMax = GlucoseRanges.getPresetMax(selectedPreset);
                        // min/max ranges are stored in mg/dl format
                        updatedUser.setCustom_range_min(rangeMin);
                        updatedUser.setCustom_range_max(rangeMax);
                    }

                    updateDB();
                    return true;
                }
            });

            minRangePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    minEditText.setText("");
                    return false;
                }
            });

            minRangePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (TextUtils.isEmpty(newValue.toString().trim())) {
                        return false;
                    }
                    double glucoseDouble = ReadingTools.safeParseDouble(newValue.toString());
                    if (user.getPreferred_unit().equals(Constants.Units.MG_DL)) {
                        updatedUser.setCustom_range_min(glucoseDouble);
                    } else {
                        updatedUser.setCustom_range_min(GlucosioConverter.glucoseToMgDl(glucoseDouble));
                    }
                    updateDB();
                    return true;
                }
            });

            maxRangePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    maxEditText.setText("");
                    return false;
                }
            });
            maxRangePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (TextUtils.isEmpty(newValue.toString().trim())) {
                        return false;
                    }
                    if (user.getPreferred_unit().equals(Constants.Units.MG_DL)) {
                        updatedUser.setCustom_range_max(ReadingTools.safeParseDouble(newValue.toString()));
                    } else {
                        updatedUser.setCustom_range_max(GlucosioConverter.glucoseToMgDl(ReadingTools.safeParseDouble(newValue.toString())));
                    }
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
                    }
                    return true;
                }
            });

            ageEditText = agePref.getEditText();
            minEditText = minRangePref.getEditText();
            maxEditText = maxRangePref.getEditText();

            ageEditText.setFilters(new InputFilter[]{new InputFilterMinMax(1, 110)});

            // Get countries list from locale
            ArrayList<String> countriesArray = new ArrayList<>();
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

            initLanguagePreference();

            updateDB();

            aboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent aboutActivity = new Intent(getActivity(), AboutActivity.class);
                    getActivity().startActivity(aboutActivity);
                    return false;
                }
            });
        }

        private String getA1CUnitValue(final String a1CUnit) {
            @StringRes int unitResId = "percentage".equals(a1CUnit) ?
                    R.string.preferences_spinner_preferred_a1c_unit_1 :
                    R.string.preferences_spinner_preferred_a1c_unit_2;
            return getResources().getString(unitResId);
        }

        private String getGlucoseUnitValue(final String glucoseUnit) {
            @StringRes int unitResId = Constants.Units.MG_DL.equals(glucoseUnit) ?
                    R.string.helloactivity_spinner_preferred_glucose_unit_1 :
                    R.string.helloactivity_spinner_preferred_glucose_unit_2;
            return getResources().getString(unitResId);
        }

        private String getUnitWeight(final String unit_weight) {
            @StringRes int unitResId = "kilograms".equals(unit_weight) ?
                    R.string.preferences_spinner_preferred_weight_unit_1 :
                    R.string.preferences_spinner_preferred_weight_unit_2;
            return getResources().getString(unitResId);
        }

        private void initLanguagePreference() {
            List<String> valuesLanguages = localeHelper.getLocalesWithTranslation(getResources());

            List<String> displayLanguages = new ArrayList<>(valuesLanguages.size());
            for (String language : valuesLanguages) {
                if (language.length() > 0) {
                    displayLanguages.add(localeHelper.getDisplayLanguage(language));
                }
            }

            languagePref.setEntryValues(getEntryValues(valuesLanguages));
            languagePref.setEntries(getEntryValues(displayLanguages));

            String languageValue = user.getPreferred_language();
            if (languageValue != null) {
                languagePref.setValue(languageValue);
                String displayLanguage = localeHelper.getDisplayLanguage(languageValue);
                languagePref.setSummary(displayLanguage);
            }

            languagePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    String language = (String) newValue;
                    updatedUser.setPreferred_language(language);
                    languagePref.setSummary(localeHelper.getDisplayLanguage(language));
                    languagePref.setValue(language);

                    updateDB();

                    localeHelper.updateLanguage(getActivity(), language);
                    getActivity().recreate();

                    return true;
                }
            });
        }

        private void updateDB() {
            dB.updateUser(updatedUser);

            countryPref.setSummary(user.getCountry());
            agePref.setSummary(String.valueOf(user.getAge()));
            genderPref.setSummary(String.valueOf(user.getGender()));
            diabetesTypePref.setSummary(getResources().getStringArray(R.array.helloactivity_diabetes_type)[user.getD_type() - 1]);
            unitPrefGlucose.setSummary(getGlucoseUnitValue(user.getPreferred_unit()));
            unitPrefA1c.setSummary(getA1CUnitValue(user.getPreferred_unit_a1c()));
            unitPrefWeight.setSummary(getUnitWeight(user.getPreferred_unit_weight()));
            rangePref.setSummary(user.getPreferred_range());

            if (Constants.Units.MG_DL.equals(user.getPreferred_unit())) {
                minRangePref.setSummary(String.valueOf(user.getCustom_range_min()));
                maxRangePref.setSummary(String.valueOf(user.getCustom_range_max()));
            } else {
                minRangePref.setSummary(String.valueOf(GlucosioConverter.glucoseToMmolL(user.getCustom_range_min())));
                maxRangePref.setSummary(String.valueOf(GlucosioConverter.glucoseToMmolL(user.getCustom_range_max())));
            }

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
}
