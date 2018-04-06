package com.henrymulenga.androidsos.utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Henry Mulenga
 * Purpose: To store user preferences for Android SOS application on the device.
 */

public class PreferenceManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context appContext;

    // Shared Preference Mode
    int PRIVATE_MODE = 0;

    // Shared Preference Keys
    private static final String PREF_NAME = "android-sos";
    private static final String IS_FIRST_LAUNCH = "FirstLaunch";
    private static final String IS_GPS_STATUS = "GpStatus";
    private static final String IS_SETTINGS_CHANGED = "SettingsChangedStatus";

    public PreferenceManager(Context context) {
        this.appContext = context;
        pref = appContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstLaunch(boolean isFirstLaunch) {
        editor.putBoolean(IS_FIRST_LAUNCH, isFirstLaunch);
        editor.commit();
    }

    public boolean isFirstLaunch() {
        return pref.getBoolean(IS_FIRST_LAUNCH, true);
    }

    public void setSettingsChangedStatus(boolean active){
        editor.putBoolean(IS_SETTINGS_CHANGED, active);
        editor.commit();
    }

    public boolean isSettingsChangedStatus() {
        return pref.getBoolean(IS_SETTINGS_CHANGED, false);
    }


    public void setGpsStatus(boolean active){
        editor.putBoolean(IS_GPS_STATUS, active);
        editor.commit();
    }

    public boolean isGpsStatus() {
        return pref.getBoolean(IS_GPS_STATUS, false);
    }

}
