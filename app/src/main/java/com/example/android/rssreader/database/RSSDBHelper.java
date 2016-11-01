package com.example.android.rssreader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RSSDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG=RSSDBHelper.class.getSimpleName();

    private static RSSDBHelper instance;

    public static final int DB_VERSION=1;
    public static final String DB_NAME="RSSReader.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_CHANNELS_TABLE =
            "CREATE TABLE " + RSSDBContract.FEED_TABLE_NAME + " (" +
                    RSSDBContract._ID + " INTEGER PRIMARY KEY," +
                    RSSDBContract.FEED_COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                    RSSDBContract.FEED_COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL," +
                    RSSDBContract.FEED_COLUMN_NAME_LAST_BUILD_DATE + " INTEGER"  +
                    RSSDBContract.FEED_COLUMN_NAME_LINK + " TEXT NOT NULL,"  + " )";

//    private static final String SQL_DELETE_ENTRIES =
//            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    private RSSDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    public static synchronized RSSDBHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (instance == null) {
            synchronized(RSSDBHelper.class){
                if (instance == null) {
                    instance = new RSSDBHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
