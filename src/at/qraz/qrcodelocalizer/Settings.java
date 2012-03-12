package at.qraz.qrcodelocalizer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

    private static final String PREFS_NAME = "QRAZ_SETTINGS";
    private static final String QRAZ_URL = "QRAZ_URL";
    private static final String UPDATE_URL = "UPDATE_URL";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";

    private static SharedPreferences sPrefs;

    public static void update(String qrazUrl, String updateUrl, String username, String password) {
        sPrefs.edit()
            .putString(QRAZ_URL, qrazUrl)
            .putString(UPDATE_URL, updateUrl)
            .putString(USERNAME, username)
            .putString(PASSWORD, password)
            .commit();
    }

    public static String getQrazUrl() {
        return sPrefs.getString(QRAZ_URL, "");
    }

    public static String getUpdateUrl() {
        return sPrefs.getString(UPDATE_URL, "");
    }

    public static String getUserName() {
        return sPrefs.getString(USERNAME, "");
    }

    public static String getPassword() {
        return sPrefs.getString(PASSWORD, "");
    }

    public static void initialize(Context c) {

        if (sPrefs == null) {
            System.out.println("Test");
            
            sPrefs = c.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
            
            System.out.println("Prefs " +  sPrefs == null);
        }
    }
}
