package com.example.android.rssreader.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;

import com.example.android.rssreader.utils.RSSUtils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.android.rssreader.utils.RSSUtils.DATE_IN_FORMAT;
import static com.example.android.rssreader.utils.RSSUtils.DATE_OUT_FORMAT;

public class RSSItem implements Parcelable {
    public static final String LOG_TAG=RSSItem.class.getSimpleName();

    // << required (at least one) >>
    private String title = null;
    private String description = null;

    // << optional >>
    private String link = null;
    private long pubDate = System.nanoTime(); // probably replace with -1 and check if it is known
    private String category = null;
    //
    private List<String > imageUrls;

    public RSSItem() {}

    protected RSSItem(Parcel in) {
        title = in.readString();
        description = in.readString();
        link = in.readString();
        pubDate = in.readLong();
//        category = in.readString();
        imageUrls = in.createStringArrayList();
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public static final Creator<RSSItem> CREATOR = new Creator<RSSItem>() {
        @Override
        public RSSItem createFromParcel(Parcel in) {
            return new RSSItem(in);
        }

        @Override
        public RSSItem[] newArray(int size) {
            return new RSSItem[size];
        }
    };

    public void addImageUrl(String url){
        if(imageUrls==null){
            imageUrls=new ArrayList<>();
        }
        imageUrls.add(url);
    }

    public void setTitle(String title)     {
        this.title = title;
    }

    public String getTitle()     {
        return title;
    }

    public void setDescription(String description)     {
        // remove javascript and any non-text nodes
        this.description = Jsoup.clean(description, Whitelist.basic());;
    }

    public String getDescription() {
        return description;
    }

    /** Get description without HTML tags (description may contain HTML tags)*/
    public String getPlainTextDescription(){
        return Html.fromHtml(description).toString().replace('\n', (char) 32)
                .replace((char) 160, (char) 32).replace((char) 65532, (char) 32).trim();
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setPubDate(String pubDate) {
        // TODO Refactor move it out
        this.pubDate=RSSUtils.strDateToMillis(pubDate);
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

    // TODO move to utils?
    public String getPubDateFormatted() {
        Date date = new Date(pubDate);
        return  DATE_OUT_FORMAT.format(date);
    }

    // PARCELABLE //



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(title);
        out.writeString(description);
        out.writeString(link);
        out.writeLong(pubDate);
        //category?
        out.writeStringList(imageUrls);
    }
}
