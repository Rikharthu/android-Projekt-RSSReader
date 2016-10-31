package com.example.android.rssreader.model;


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
    private String pubDate = null;
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

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPubDateFormatted() {
        String pubDateFormatted = "";
        try {
            Date date = dateInFormat.parse(pubDate.trim());
            pubDateFormatted = dateOutFormat.format(date);
            return pubDateFormatted;
        }
        catch (ParseException e) {
            return pubDate;
        }
    }
}
