package org.glucosio.android.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.instabug.library.Instabug;

import org.glucosio.android.R;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_about);

        getFragmentManager().beginTransaction()
                .replace(R.id.aboutPreferencesFrame, new MyPreferenceFragment()).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.preferences_about_glucosio));
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.about_preference);

            final Preference licencesPref = (Preference) findPreference("preference_licences");
            final Preference ratePref = (Preference) findPreference("preference_rate");
            final Preference feedbackPref = (Preference) findPreference("preference_feedback");
            final Preference privacyPref = (Preference) findPreference("preference_privacy");
            final Preference termsPref = (Preference) findPreference("preference_terms");
            final Preference versionPref = (Preference) findPreference("preference_version");

            termsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), LicenceActivity.class);
                    Bundle bundle= new Bundle();
                    bundle.putString("key", "terms");
                    intent.putExtras(bundle);
                    startActivity(intent);

                    return false;
                }
            });

            licencesPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), LicenceActivity.class);
                    Bundle bundle= new Bundle();
                    bundle.putString("key", "open_source");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    return false;
                }
            });

            ratePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=org.glucosio.android"));
                    startActivity(intent);

                    return false;
                }
            });

            feedbackPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Instabug.invoke();

                    return false;
                }
            });

            privacyPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), LicenceActivity.class);
                    Bundle bundle= new Bundle();
                    bundle.putString("key", "privacy");
                    intent.putExtras(bundle);
                    startActivity(intent);

                    return false;
                }
            });

            versionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                int easterEggCount;

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (easterEggCount == 6) {
                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 40.794010, 17.124583);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        getActivity().startActivity(intent);
                        easterEggCount = 0;
                    } else {
                        this.easterEggCount = easterEggCount + 1;
                    }
                    return false;
                }
            });

        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
