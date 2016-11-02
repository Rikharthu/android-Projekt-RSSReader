package com.example.android.rssreader.utils;


import java.text.SimpleDateFormat;
import java.util.Locale;

public abstract class RSSUtils {

    public static final SimpleDateFormat DATE_OUT_FORMAT =
            new SimpleDateFormat("EEEE h:mm a (MMM d)", Locale.ENGLISH);

    public static final SimpleDateFormat DATE_IN_FORMAT =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

}
