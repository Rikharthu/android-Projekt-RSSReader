package com.example.android.rssreader;


import android.app.Application;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

public class RSSReaderApp extends Application {

    // these milliseconds correspond with the publication date for the current RSS feed for the app
    private long feedMillis = -1;
    private AccessibilityManager accessibilityManager;

    public void setFeedMillis(long feedMillis) {
        this.feedMillis = feedMillis;
    }

    public long getFeedMillis() {
        return feedMillis;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("RSS Reader", "App started");

        accessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        isTouchExplorationEnabled();
    }

    private boolean isTouchExplorationEnabled(){
        return accessibilityManager.isTouchExplorationEnabled();
    }

}
