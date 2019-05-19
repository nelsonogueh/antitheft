package com.dulceprime.antitheft.other_components;

/**
 * Created by Nelson on 8/10/2018.
 */

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Nelson 12/12/2017.
 */
public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "anti_theft";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_PROTECTED = "IsProtected";
    private static final String IS_PERMISSION_GRANTED = "IsPermissionGranted";
    private static final String IS_REPORT_SENT = "IsReportSent";
    private static final String IS_FRESH_REQUEST = "IsFreshRequest";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setIsProtected(boolean isProtected) {
        editor.putBoolean(IS_PROTECTED, isProtected);
        editor.commit();
    }

    public boolean isProtected() {
        return pref.getBoolean(IS_PROTECTED, false);
    }


    public void setIsPermissionGranted(boolean permissionGranted) {
        editor.putBoolean(IS_PERMISSION_GRANTED, permissionGranted);
        editor.commit();
    }

    public boolean isPermissionGranted() {
        return pref.getBoolean(IS_PERMISSION_GRANTED, false);
    }


    public void setIsReportSent(boolean isReportSent) {
        editor.putBoolean(IS_REPORT_SENT, isReportSent);
        editor.commit();
    }

    public boolean isReportSent() {
        return pref.getBoolean(IS_REPORT_SENT, true);
    }


    public void setIsFreshRequest(boolean isFreshRequest) {
        editor.putBoolean(IS_FRESH_REQUEST, isFreshRequest);
        editor.commit();
    }

    public boolean isFreshRequest() {
        return pref.getBoolean(IS_FRESH_REQUEST, false);
    }

}