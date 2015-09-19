package org.glucosio.android.activity;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.text.InputFilter;
import android.widget.EditText;

import org.glucosio.android.R;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.tools.InputFilterMinMax;

import java.util.List;

public class PreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
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
            diabetesTypePref.setSummary(dB.getUser(1).get_d_type() + "");
            unitPref.setSummary(dB.getUser(1).get_preferred_unit() + "");
        }
    }

}