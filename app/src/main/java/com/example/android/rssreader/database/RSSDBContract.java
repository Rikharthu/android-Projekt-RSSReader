package com.example.android.rssreader.database;


import android.provider.BaseColumns;

public interface RSSDBContract extends BaseColumns {

    // RSSFeed
    String FEED_TABLE_NAME ="feeds";
    String FEED_COLUMN_NAME_TITLE ="title";
    String FEED_COLUMN_NAME_LINK ="link";
    String FEED_COLUMN_NAME_DESCRIPTION ="description";
    String FEED_COLUMN_NAME_LAST_BUILD_DATE ="lastBuildDate";
    String FEED_COLUMN_NAME_IMAGE ="bitmap";

    // RSSItem
    String ITEMS_TABLE_NAME="items";
    String ITEMS_COLUMN_NAME_TITLE="title";
    String ITEMS_COLUMN_NAME_DESCRIPTION="description";
    String ITEMS_COLUMN_NAME_LINK="link";
    String ITEMS_COLUMN_NAME_PUB_DATE="pubDate";
    String ITEMS_COLUMN_NAME_CATEGORY="category";
    // foreign key
    String ITEMS_COLUMN_NAME_FEED_ID="feedId";
}
