package com.bignerdranch.android.initialtwittersyncadapter.model;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by SamMyxer on 4/6/16.
 */
public class PreferenceStore {
    private static final String GCM_TOKEN_KEY =
            "com.bignerdranch.android.initialtwittersyncadapter.GCM_TOKEN";

    private static PreferenceStore sPreferenceStore;
    private Context mContext;

    public static PreferenceStore get(Context context) {
        if(sPreferenceStore == null) {
            sPreferenceStore = new PreferenceStore(context);
        }
        return sPreferenceStore;
    }

    public PreferenceStore(Context context) {
        mContext = context;
    }

    public String getGcmToken() {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(GCM_TOKEN_KEY, null);
    }

    public void setGcmToken(String token) {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putString(GCM_TOKEN_KEY, token)
                .apply();
    }
}
