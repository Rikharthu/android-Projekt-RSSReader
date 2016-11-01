package com.example.android.rssreader.model;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RSSFeed {

    // << required >>
    private String title = null;
    private String link=null;
    private String description=null;

    // << optional >>
    private long lastBuildDate = System.currentTimeMillis();
    private String imageUri=null;

    // items
    private ArrayList<RSSItem> items;

    private SimpleDateFormat dateInFormat =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

    private SimpleDateFormat dateOutFormat =
            new SimpleDateFormat("EEEE h:mm a (MMM d)");

    public RSSFeed() {
        items = new ArrayList<RSSItem>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setLastBuildDate(String lastBuildDate) {
        Date date = new Date(0);
        try {
            date = dateInFormat.parse(lastBuildDate.trim());
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
        long dateMillis = date.getTime();
        this.lastBuildDate=dateMillis;
    }

    public long getLastBuildDate() {
        return lastBuildDate;
    }

    public int addItem(RSSItem item) {
        items.add(item);
        return items.size();
    }

    public RSSItem getItem(int index) {
        return items.get(index);
    }

    public ArrayList<RSSItem> getAllItems() {
        return items;
    }

    public int getItemCount() {
        return items.size();
    }

    public String getLastBuildDateFormatted(){
        Date date = new Date(lastBuildDate);
        return dateOutFormat.format(date);
    }

}
