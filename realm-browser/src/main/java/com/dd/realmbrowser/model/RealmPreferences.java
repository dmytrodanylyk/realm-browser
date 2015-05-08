package com.dd.realmbrowser.model;

import android.content.Context;
import android.content.SharedPreferences;

public class RealmPreferences {

    private Context mContext;

    private static final String PREF_NAME = "pref.realm";
    private static final String WRAP_TEXT = "WRAP_TEXT";

    public RealmPreferences(Context context) {
        mContext = context;
    }

    private SharedPreferences preferences() {
        return mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setShouldWrapText(boolean value) {
        preferences().edit().putBoolean(WRAP_TEXT, value).commit();
    }

    public boolean shouldWrapText() {
        return preferences().getBoolean(WRAP_TEXT, false);
    }

}
