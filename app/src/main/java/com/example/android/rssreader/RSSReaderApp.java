package com.example.android.rssreader;


import android.app.Application;
import android.util.Log;

public class RSSReaderApp extends Application {

    // these milliseconds correspond with the publication date for the current RSS feed for the app
    private long feedMillis = -1;

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

        // Moved
//        Intent service = new Intent(this,NewsReaderService.class);
//        startService(service);
    }

}
