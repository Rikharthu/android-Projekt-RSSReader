package com.example.android.rssreader.utils;


import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class StringUtils {

    private static DateFormat localDateFormat;

    static{
        localDateFormat = DateFormat.getDateTimeInstance(
                DateFormat.DEFAULT,
                DateFormat.SHORT,
                Locale.getDefault());
    }

    public static String getFormattedLocalDate(long timeMillis){
        return localDateFormat.format(timeMillis);
    }

    public static boolean isEmptyOrNull(String string){
        return string==null || string.isEmpty();
    }



}
