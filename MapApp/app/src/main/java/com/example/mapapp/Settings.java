package com.example.mapapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


import androidx.annotation.Nullable;

public class Settings extends PreferenceActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final EditTextPreference radioEditText = (EditTextPreference) findPreference("radio_search");

        int radio = prefs.getInt("radio", 50);
        radioEditText.setSummary(Integer.toString(radio));


        radioEditText.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                radioEditText.setSummary((CharSequence) newValue);
                int val = Integer.parseInt(newValue.toString());
                if (val < 50) val = 50;
                if (val > 2000) val = 2000;
                SharedPreferences.Editor ed = prefs.edit();
                ed.putInt("radio", val);
                ed.commit();
                return true;
            }
        });
    }
}
