package com.example.android.newsapp;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_settings);
        Preference number = findPreference(getString(R.string.settings_news_items_key));
        bindPreferenceSummaryToValue(number);
        Preference home = findPreference(getString(R.string.settings_home_key));
        bindPreferenceSummaryToValue(home);
    }

    @Override
    //Reset the summary when the preference value changes
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            CharSequence[] entries = listPreference.getEntries();
            int selected = listPreference.findIndexOfValue(newValue.toString());
            preference.setSummary(entries[selected]);
        } else
            preference.setSummary(newValue.toString());
        return true;
    }

    //Set the preference change listener to the preferences
    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
        String preferenceString = preferences.getString(preference.getKey(), "");
        onPreferenceChange(preference, preferenceString);
    }
}
