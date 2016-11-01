package com.example.android.rssreader.model;


import android.text.Html;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RSSItem {
    public static final String LOG_TAG=RSSItem.class.getSimpleName();

    // << required (at least one) >>
    private String title = null;
    private String description = null;

    // << optional >>
    private String link = null;
    private long pubDate = System.nanoTime(); // probably replace with -1 and check if it is known
    private String category = null;

    private SimpleDateFormat dateOutFormat =
            new SimpleDateFormat("EEEE h:mm a (MMM d)");

    private SimpleDateFormat dateInFormat =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

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
        return Html.fromHtml(getDescription()).toString();
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
            date = dateInFormat.parse(pubDate.trim());
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.pubDate=date.getTime();
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
        return  dateOutFormat.format(date);
    }
}
