package com.example.android.rssreader.utils;


import org.apache.commons.lang3.time.DateUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class RSSUtils {

    public static final SimpleDateFormat[] PARSE_OUT_PATTERS=
            {
                    new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH),// Wed, 02 Nov 2016 13:15:11 +0200
                    new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH) //02 Nov 2016 15:06:48 +0300
            };

    public static final SimpleDateFormat DATE_OUT_FORMAT =
            new SimpleDateFormat("EEEE h:mm a (MMM d)", Locale.ENGLISH);

    public static final SimpleDateFormat DATE_IN_FORMAT =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    public static String toUtf8(String text){
        try {
            return URLDecoder.decode(URLEncoder.encode(text, "iso8859-1"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long strDateToMillis(String dateStr){
        Date date=null;
        for (SimpleDateFormat format: PARSE_OUT_PATTERS) {
            try {
                date = DATE_IN_FORMAT.parse(dateStr.trim());
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date.getTime();
    }
}
