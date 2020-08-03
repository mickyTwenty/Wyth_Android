package com.seatus.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by rohail on 20-Jan-17.
 */

public class PreferencesManager {
    private static SharedPreferences mSharedPreferences;
    private static Gson mGson;

    private Context mContext;
    private String mName;

    /**
     * Initial the preferences manager.
     *
     * @param context The context of the application.
     */

    /**
     * Initate a static prefs object
     *
     * @param context
     * @param name
     */
    public static void createInstance(Context context, String name) {
        if (mSharedPreferences == null) {
            mSharedPreferences = new PreferencesManager(context).setName(name).init();
        }
    }

    private PreferencesManager(Context context) {
        mContext = context;
        mGson = new Gson();
    }

    /**
     * Set the message of the preferences.
     *
     * @param name The message of the preferences.
     */
    private PreferencesManager setName(String name) {
        mName = name;
        return this;
    }

    /**
     * Initial the instance of the preferences manager.
     */
    private SharedPreferences init() {
        if (mContext == null) {
            return null;
        }

        if (mName.isEmpty()) {
            mName = mContext.getPackageName();
        }

        mSharedPreferences = mContext.getSharedPreferences(mName, Activity.MODE_PRIVATE);

        return mSharedPreferences;

    }

    /**
     * Put a String value in the preferences editor.
     *
     * @param key   The message of the preference to modify.
     * @param value The new value for the preference.
     */
    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Retrieval a String value from the preferences.
     *
     * @param key The message of the preference to retrieve.
     * @return Returns the preference values if they exist, or defValues.
     */
    public static String getString(String key) {
        if (mSharedPreferences == null) {
            return "";
        }
        return mSharedPreferences.getString(key, "");
    }

    public static String getString(String key, String defaultVal) {
        if (mSharedPreferences == null) {
            return "";
        }
        return mSharedPreferences.getString(key, defaultVal);
    }

    /**
     * Put an int value in the preferences editor.
     *
     * @param key   The message of the preference to modify.
     * @param value The new value for the preference.
     */
    public static void putInt(String key, int value) {
        if (mSharedPreferences == null) {
            value = 0;
        }

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Retrieval an int value from the preferences.
     *
     * @param key The message of the preference to retrieve.
     * @return Returns the preference values if they exist, or defValues.
     */
    public static int getInt(String key) {
        if (mSharedPreferences == null) {
            return 0;
        }
        return mSharedPreferences.getInt(key, 0);
    }

    /**
     * Put a float value in the preferences editor.
     *
     * @param key   The message of the preference to modify.
     * @param value The new value for the preference.
     */
    public static void putFloat(String key, float value) {
        if (mSharedPreferences == null) {
            value = 0.0f;
        }

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    /**
     * Retrieval a float value from the preferences.
     *
     * @param key The message of the preference to retrieve.
     * @return Returns the preference values if they exist, or defValues.
     */
    public static float getFloat(String key) {
        if (mSharedPreferences == null) {
            return 0;
        }
        return mSharedPreferences.getFloat(key, 0);
    }

    /**
     * Put a long value in the preferences editor.
     *
     * @param key   The message of the preference to modify.
     * @param value The new value for the preference.
     */
    public static void putLong(String key, long value) {
        if (mSharedPreferences == null) {
            value = 0l;
        }

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * Retrieval a long value from the preferences.
     *
     * @param key The message of the preference to retrieve.
     * @return Returns the preference values if they exist, or defValues.
     */
    public static long getLong(String key) {
        if (mSharedPreferences == null) {
            return 0;
        }
        return mSharedPreferences.getLong(key, 0l);
    }

    /**
     * Put a boolean value in the preferences editor.
     *
     * @param key   The message of the preference to modify.
     * @param value The new value for the preference.
     */
    public static void putBoolean(String key, boolean value) {
        if (mSharedPreferences == null) {
            return;
        }

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Retrieval a boolean value from the preferences.
     *
     * @param key The message of the preference to retrieve.
     * @return Returns the preference values if they exist, or defValues.
     */
    public static boolean getBoolean(String key) {
        if (mSharedPreferences == null) {
            return false;
        }
        return mSharedPreferences.getBoolean(key, false);
    }

    /**
     * Put a object in the preferences editor.
     *
     * @param key   The message of the preference to modify.
     * @param value The new value for the preference.
     */
    public static void putObject(String key, Object value) {
        if (mGson == null) {
            return;
        }

        putString(key, mGson.toJson(value));
    }

    /**
     * Retrieval a object from the preferences.
     *
     * @param key  The message of the preference to retrieve.
     * @param type The class of the preference to retrieve.
     * @return Returns the preference values if they exist, or defValues.
     */
    public static <T> T getObject(String key, Class<T> type) {
        try {
            if (mSharedPreferences == null || mGson == null) {
                return null;
            }
            return mGson.fromJson(getString(key), type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

