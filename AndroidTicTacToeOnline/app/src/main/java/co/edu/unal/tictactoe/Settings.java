package co.edu.unal.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
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
        final ListPreference difficultyLevelPref = (ListPreference) findPreference("difficulty_level");
        String difficulty = prefs.getString("difficulty_level", getResources().getString(R.string.difficulty_expert));

        difficultyLevelPref.setSummary((CharSequence) difficulty);


        difficultyLevelPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                difficultyLevelPref.setSummary((CharSequence) newValue);

                SharedPreferences.Editor ed = prefs.edit();
                ed.putString("difficulty_level", newValue.toString());
                ed.commit();
                return true;

            }
        });

        final EditTextPreference victoryMessagePref = (EditTextPreference) findPreference("victory_message");
        final String victoryMessage = prefs.getString("victory_message", getResources().getString(R.string.result_human_wins));
        victoryMessagePref.setSummary(victoryMessage);
        victoryMessagePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                victoryMessagePref.setSummary((CharSequence) o);
                SharedPreferences.Editor ed = prefs.edit();
                ed.putString("victory_message", o.toString());
                ed.commit();
                return true;
            }
        });

    }
}
