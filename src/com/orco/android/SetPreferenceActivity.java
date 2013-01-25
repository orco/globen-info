package com.orco.android;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SetPreferenceActivity extends PreferenceActivity {

    protected PrefsFragment fragment = new PrefsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment).commit();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(listener);

        /*
         * String str = PreferenceManager.getDefaultSharedPreferences(this)
         * .getString("username", "HOPPLA"); ((EditTextPreference)
         * fragment.findPreference("username")) .setSummary(str);
         */

        /*
         * EditTextPreference username = (EditTextPreference) fragment
         * .findPreference("username"); EditTextPreference password =
         * (EditTextPreference) fragment .findPreference("password");
         * username.setSummary
         * (PreferenceManager.getDefaultSharedPreferences(this)
         * .getString("username", "UNKNOWN"));
         * password.setSummary(PreferenceManager
         * .getDefaultSharedPreferences(this) .getString("password",
         * "UNKNOWN"));
         */
    }

    // Use instance field for listener
    // It will not be gc'd as long as this instance is kept referenced
    OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs,
                String key) {
            if (key.equals("username") || key.equals("password")) {
                EditTextPreference value = (EditTextPreference) fragment
                    .findPreference(key);
                value.setSummary(prefs.getString(key, "UNKNOWN"));
            }
        }
    };

    //registerOnSharedPreferenceChangeListener(listener);
}