package com.example.android.sunshine.app;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;

import java.util.List;

/**
 * Created by dougl on 1/8/2018.
 */

// https://developer.android.com/reference/android/preference/PreferenceActivity.html#EXTRA_SHOW_FRAGMENT

public class SettingsActivity extends PreferenceActivity {
    private final String LOG_TAG = SettingsActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //        if (hasHeaders()) {
 //           Button button = new Button(this);
 //           button.setText("Some action");
 //           setListFooter(button);
 //       }
        Log.e(LOG_TAG,"onCreate");
    }
    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        Log.e(LOG_TAG,"onBuildHeaders");
        loadHeadersFromResource(R.xml.pref_settings_activity,target);
    }

    protected boolean isValidFragment(String fragmentName) {
        return true ;
        // return SettingsFragment.class.getName().equals(fragmentName) ;
        //return StockPreferenceFragment.class.getName().equals(fragmentName);
    }
}
