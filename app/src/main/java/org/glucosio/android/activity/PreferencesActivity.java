package org.glucosio.android.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.glucosio.android.R;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.User;
import org.glucosio.android.tools.InputFilterMinMax;

import java.util.ArrayList;
import java.util.Locale;

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

        Dialog termsDialog;
        DatabaseHandler dB;
        User user;
        ListPreference countryPref;
        ListPreference genderPref;
        ListPreference diabetesTypePref;
        ListPreference unitPref;
        EditText ageEditText;
        EditTextPreference agePref;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            dB = DatabaseHandler.getInstance(super.getActivity().getApplicationContext());
            user = dB.getUser(1);

            agePref = (EditTextPreference) findPreference("pref_age");
            countryPref = (ListPreference) findPreference("pref_country");
            genderPref = (ListPreference) findPreference("pref_gender");
            diabetesTypePref = (ListPreference) findPreference("pref_diabetes_type");
            unitPref = (ListPreference) findPreference("pref_unit");
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

            ageEditText = agePref.getEditText();
            ageEditText.setFilters(new InputFilter[]{new InputFilterMinMax(1, 110)});


            // Get countries list from locale
            ArrayList<String> countriesArray = new ArrayList<String>();
            Locale[] locales = Locale.getAvailableLocales();

            for (Locale locale : locales) {
                String country = locale.getDisplayCountry();
                if (country.trim().length()>0 && !countriesArray.contains(country)) {
                    countriesArray.add(country);
                }
            }
            CharSequence[] countries = countriesArray.toArray(new CharSequence[countriesArray.size()]);

            countryPref.setEntryValues(countries);
            countryPref.setEntries(countries);
            updateDB();

            termsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    termsDialog = new Dialog(getActivity(), R.style.GlucosioTheme);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(termsDialog.getWindow().getAttributes());
                    termsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    termsDialog.setContentView(R.layout.dialog_licence);
                    termsDialog.getWindow().setAttributes(lp);
                    termsDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    termsDialog.getWindow().setDimAmount(0.5f);
                    termsDialog.setCanceledOnTouchOutside(true);
                    termsDialog.show();

                    TextView dialogOk = (TextView) termsDialog.findViewById(R.id.dialog_terms_ok);
                    dialogOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            termsDialog.dismiss();
                        }
                    });

                    return false;
                }
            });
        }

        private void updateDB(){
            dB.updateUser(user);
            agePref.setSummary(user.get_age() + "");
            genderPref.setSummary(user.get_gender() + "");
            diabetesTypePref.setSummary(getResources().getString(R.string.glucose_reading_type) + " " + user.get_d_type());
            unitPref.setSummary(user.get_preferred_unit() + "");
            countryPref.setSummary(user.get_country());
        }
    }


    @Override
    public void onBackPressed() {
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