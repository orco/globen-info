package com.orco.android;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SetPreferenceActivity extends PreferenceActivity
        implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

  //      if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            // Load the legacy preferences headers
            //addPreferencesFromResource(R.xml.preference_headers_legacy);
            addPreferencesFromResource(R.xml.preferences);
    //    }
    }

    // Called only on Honeycomb and later
//    @Override
//    public void onBuildHeaders(java.util.List<Header> target) {
//        loadHeadersFromResource(R.xml.preferences, target);
//    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("username") || key.equals("password")) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, "UNKNOWN"));
        }
    }

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment).commit();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(listener);
    }*/

    // Use instance field for listener
    // It will not be gc'd as long as this instance is kept referenced
 /*   OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
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
*/
    //registerOnSharedPreferenceChangeListener(listener);
}