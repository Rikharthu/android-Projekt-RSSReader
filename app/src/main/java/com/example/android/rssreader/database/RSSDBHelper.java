package com.example.android.rssreader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.android.rssreader.model.RSSFeed;
import com.example.android.rssreader.model.RSSItem;

import java.io.ByteArrayOutputStream;

import static android.provider.BaseColumns._ID;
import static com.example.android.rssreader.database.RSSDBContract.FEED_COLUMN_NAME_DESCRIPTION;
import static com.example.android.rssreader.database.RSSDBContract.FEED_COLUMN_NAME_IMAGE;
import static com.example.android.rssreader.database.RSSDBContract.FEED_COLUMN_NAME_LAST_BUILD_DATE;
import static com.example.android.rssreader.database.RSSDBContract.FEED_COLUMN_NAME_LINK;
import static com.example.android.rssreader.database.RSSDBContract.FEED_COLUMN_NAME_TITLE;
import static com.example.android.rssreader.database.RSSDBContract.FEED_TABLE_NAME;
import static com.example.android.rssreader.database.RSSDBContract.ITEMS_COLUMN_NAME_CATEGORY;
import static com.example.android.rssreader.database.RSSDBContract.ITEMS_COLUMN_NAME_DESCRIPTION;
import static com.example.android.rssreader.database.RSSDBContract.ITEMS_COLUMN_NAME_FEED_ID;
import static com.example.android.rssreader.database.RSSDBContract.ITEMS_COLUMN_NAME_LINK;
import static com.example.android.rssreader.database.RSSDBContract.ITEMS_COLUMN_NAME_PUB_DATE;
import static com.example.android.rssreader.database.RSSDBContract.ITEMS_COLUMN_NAME_TITLE;
import static com.example.android.rssreader.database.RSSDBContract.ITEMS_TABLE_NAME;
import static com.example.android.rssreader.utils.RSSUtils.is8859toUtf8;

public class RSSDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG=RSSDBHelper.class.getSimpleName();

    private static RSSDBHelper instance;

    public static final int DB_VERSION=1;
    public static final String DB_NAME="RSSReader.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_CHANNELS_TABLE =
            "CREATE TABLE " + FEED_TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    FEED_COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                    FEED_COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL," +
                    FEED_COLUMN_NAME_LINK + " TEXT NOT NULL,"  +
                    FEED_COLUMN_NAME_IMAGE + " BLOB,"  +
                    FEED_COLUMN_NAME_LAST_BUILD_DATE + " INTEGER"  + " )";

    private static final String SQL_CREATE_ITEMS_TABLE =
            "CREATE TABLE " + RSSDBContract.ITEMS_TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    ITEMS_COLUMN_NAME_TITLE + " TEXT," +
                    ITEMS_COLUMN_NAME_DESCRIPTION + " TEXT," +
                    ITEMS_COLUMN_NAME_LINK + " TEXT,"  +
                    ITEMS_COLUMN_NAME_PUB_DATE + " INTEGER,"  +
                    ITEMS_COLUMN_NAME_FEED_ID + " INTEGER,"  +
                    ITEMS_COLUMN_NAME_CATEGORY + " INTEGER"  + " )";

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
        sqLiteDatabase.execSQL(SQL_CREATE_CHANNELS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long saveRSSFeed(RSSFeed feed){
        ContentValues channel = new ContentValues();
        channel.put(FEED_COLUMN_NAME_TITLE, is8859toUtf8(feed.getTitle()));
        channel.put(FEED_COLUMN_NAME_LINK,feed.getLink());
        channel.put(FEED_COLUMN_NAME_DESCRIPTION, is8859toUtf8(feed.getDescription()));
        channel.put(FEED_COLUMN_NAME_LAST_BUILD_DATE,feed.getLastBuildDate());
        if(feed.getLogo()!=null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            feed.getLogo().compress(Bitmap.CompressFormat.PNG, 100, stream);
            channel.put(FEED_COLUMN_NAME_IMAGE,stream.toByteArray());
        }
        SQLiteDatabase db = getWritableDatabase();
        long rowId=db.insert(FEED_TABLE_NAME,null,channel);
        Log.d(LOG_TAG,"inserted feed at rowId: "+rowId);
        // rss items
        for (RSSItem item: feed.getAllItems()) {
            ContentValues itemValues = new ContentValues();
            itemValues.put(ITEMS_COLUMN_NAME_TITLE, is8859toUtf8(item.getTitle()));
            itemValues.put(ITEMS_COLUMN_NAME_LINK,item.getLink());
            itemValues.put(ITEMS_COLUMN_NAME_DESCRIPTION, is8859toUtf8(item.getDescription()));
            itemValues.put(ITEMS_COLUMN_NAME_PUB_DATE,item.getPubDate());
            itemValues.put(ITEMS_COLUMN_NAME_FEED_ID,rowId);
            db.insert(ITEMS_TABLE_NAME,null,itemValues);
        }
        db.close();
        return rowId;
    }

    /** Returns RSSFeed at specified rowId
     * @param full determines whether to return full RSSFeed (with it's items) or not */
    public RSSFeed getRSSFeed(long id,boolean full){
        if(!full){
            String selection = _ID + " = ?";
            String[] selectionArgs = { id +""};
            Cursor dbCursor = getReadableDatabase().query(RSSDBContract.FEED_TABLE_NAME,null, selection, selectionArgs, null, null, null);
            RSSFeed feed=new RSSFeed();
            if(dbCursor.moveToFirst()){
                feed.setTitle(dbCursor.getString(dbCursor.getColumnIndex(FEED_COLUMN_NAME_TITLE)));
                feed.setDescription(dbCursor.getString(dbCursor.getColumnIndex(FEED_COLUMN_NAME_DESCRIPTION)));
                feed.setLink(dbCursor.getString(dbCursor.getColumnIndex(FEED_COLUMN_NAME_LINK)));
                // returns millis
                feed.setLastBuildDate(dbCursor.getLong(dbCursor.getColumnIndex(FEED_COLUMN_NAME_LAST_BUILD_DATE)));
                // TODO handle if no entry
                byte[] bitmapBlob=dbCursor.getBlob(dbCursor.getColumnIndex(FEED_COLUMN_NAME_IMAGE));
                feed.setLogo(BitmapFactory.decodeByteArray(bitmapBlob, 0, bitmapBlob.length));
                return feed;
            }else{
                return null;
            }
        }else{
            RSSFeed feed=getRSSFeed(id,false);
            if(feed==null){
                // probably exceptions
                return null;
            }
            String selection = ITEMS_COLUMN_NAME_FEED_ID + " = ?";
            String[] selectionArgs = { id +""};
            Cursor itemsCursor = getReadableDatabase().query(RSSDBContract.ITEMS_TABLE_NAME,null, selection, selectionArgs, null, null, null);
            while(itemsCursor.moveToNext()){
                RSSItem item = new RSSItem();
                item.setTitle(itemsCursor.getString(itemsCursor.getColumnIndex(ITEMS_COLUMN_NAME_TITLE)));
                item.setDescription(itemsCursor.getString(itemsCursor.getColumnIndex(ITEMS_COLUMN_NAME_DESCRIPTION)));
                item.setLink(itemsCursor.getString(itemsCursor.getColumnIndex(ITEMS_COLUMN_NAME_LINK)));
                item.setPubDate(itemsCursor.getLong(itemsCursor.getColumnIndex(ITEMS_COLUMN_NAME_PUB_DATE)));
                feed.addItem(item);
            }
            return feed;
        }

    }

}
