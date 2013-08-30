package com.orco.android;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SetPreferenceActivity extends PreferenceActivity
        implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("username") || key.equals("password")) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            if(connectionPref != null) {
                connectionPref.setSummary(sharedPreferences.getString(key, "UNKNOWN"));
            }
        }
    }

}