package com.example.android.rssreader.utils;


import java.text.SimpleDateFormat;

public abstract class RSSUtils {

    private SimpleDateFormat dateOutFormat =
            new SimpleDateFormat("EEEE h:mm a (MMM d)");

    private SimpleDateFormat dateInFormat =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

}
