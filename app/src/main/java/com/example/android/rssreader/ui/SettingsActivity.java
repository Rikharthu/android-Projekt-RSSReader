package com.example.android.rssreader.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.android.rssreader.R;

import java.util.List;


public class SettingsActivity extends PreferenceActivity {

    interface SettingsChangedListener{
        void onSettingsChanged(int code);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preferences_headers, target);
    }

    // must be implemented
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return SettingsFragment.class.getName().equals(fragmentName);
    }
}
