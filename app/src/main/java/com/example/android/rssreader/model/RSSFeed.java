package com.example.android.rssreader.model;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.rssreader.utils.RSSUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.android.rssreader.utils.RSSUtils.DATE_IN_FORMAT;
import static com.example.android.rssreader.utils.RSSUtils.DATE_OUT_FORMAT;

public class RSSFeed implements Parcelable{

    private long id;
    // << required >>
    private String title = null;
    private String link=null;
    private String description=null;

    // << optional >>
    private long lastBuildDate = System.currentTimeMillis();
    private String imageUri=null;
    private Bitmap logo=null;

    // items
    private ArrayList<RSSItem> items;

    public RSSFeed() {
        items = new ArrayList<RSSItem>();
    }

    protected RSSFeed(Parcel in) {
        id = in.readLong();
        title = in.readString();
        link = in.readString();
        description = in.readString();
        lastBuildDate = in.readLong();
        imageUri = in.readString();
        logo = in.readParcelable(Bitmap.class.getClassLoader());
        items = in.createTypedArrayList(RSSItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(description);
        dest.writeLong(lastBuildDate);
        dest.writeString(imageUri);
        dest.writeParcelable(logo, flags);
        dest.writeTypedList(items);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RSSFeed> CREATOR = new Creator<RSSFeed>() {
        @Override
        public RSSFeed createFromParcel(Parcel in) {
            return new RSSFeed(in);
        }

        @Override
        public RSSFeed[] newArray(int size) {
            return new RSSFeed[size];
        }
    };

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
        this.lastBuildDate=RSSUtils.strDateToMillis(lastBuildDate);
    }

    public void setLastBuildDate(long dateMillis){
        lastBuildDate=dateMillis;
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
        return DATE_OUT_FORMAT.format(date);
    }

    public Bitmap getLogo() {
        return logo;
    }

    public void setLogo(Bitmap logo) {
        this.logo = logo;
    }


}
