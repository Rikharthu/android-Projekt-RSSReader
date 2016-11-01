package com.example.android.rssreader;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/** Preferences Access layer */
public class RSSReaderApplicationSettings  {
    public static final String CROP_DESCRIPTION = "pref_crop_desc";
    SharedPreferences mSharedPreferences;

    public RSSReaderApplicationSettings(Context context) {
        // SharedPreferences settings = context.getSharedPreferences("Preferences Name", 0);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /** Returns preferred storage type.
     * By default use Internal storage */
    public String getStoragePreference() {
        // by default use Internal storage
//        return mSharedPreferences.getString("Storage", StorageType.INTERNAL);
        return null;
    }

    /** Set preferred storage type */
    public void setSharedPreference(String storageType) {
        mSharedPreferences
                .edit()
                .putString("Storage", storageType)
                .apply();
        // we could use .commit(), which is sycnrhonous (.apply() is asynchronous)
    }

    public boolean shouldCropDescription(){
        return mSharedPreferences.getBoolean(CROP_DESCRIPTION,true);
    }

    public int getMaxCropDescChars(){
        return Integer.parseInt(mSharedPreferences.getString("pref_crop_chars_count","60"));
    }

    public void setCropDescription(boolean shouldCrop){
        mSharedPreferences.edit().putBoolean(CROP_DESCRIPTION,shouldCrop).apply();
    }

}
