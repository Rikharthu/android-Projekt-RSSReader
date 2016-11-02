package com.example.android.rssreader.model;


import android.text.Html;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.android.rssreader.utils.RSSUtils.DATE_IN_FORMAT;
import static com.example.android.rssreader.utils.RSSUtils.DATE_OUT_FORMAT;

public class RSSItem {
    public static final String LOG_TAG=RSSItem.class.getSimpleName();

    // << required (at least one) >>
    private String title = null;
    private String description = null;

    // << optional >>
    private String link = null;
    private long pubDate = System.nanoTime(); // probably replace with -1 and check if it is known
    private String category = null;

    public RSSItem() {}

    public void setTitle(String title)     {
        this.title = title;
    }

    public String getTitle()     {
        return title;
    }

    public void setDescription(String description)     {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /** Get description without HTML tags (description may contain HTML tags)*/
    public String getPlainTextDescription(){
        // TODO перенеси эту логику на сохраниние в БД
        String data= null ;
        try {
//            data = URLDecoder.decode(Html.fromHtml(getDescription()).toString(), "UTF-8");
            // "ISO-8859-1"
            String s = URLDecoder.decode(URLEncoder.encode(getDescription(), "iso8859-1"),"UTF-8");
            return Html.fromHtml(s).toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setPubDate(String pubDate) {
        Date date;
        try {
            date = DATE_IN_FORMAT.parse(pubDate.trim());
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.pubDate=date.getTime();
    }

    public void setPubDate(long millis) {
        pubDate=millis;
    }

    public long getPubDate() {
        return pubDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPubDateFormatted() {
        Date date = new Date(pubDate);
        return  DATE_OUT_FORMAT.format(date);
    }
}
