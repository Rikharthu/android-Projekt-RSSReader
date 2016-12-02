package com.example.android.rssreader.ui;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.rssreader.R;
import com.example.android.rssreader.adapters.RSSFeedAdapter;
import com.example.android.rssreader.database.RSSDBContract;
import com.example.android.rssreader.database.RSSDBHelper;
import com.example.android.rssreader.model.RSSFeed;
import com.example.android.rssreader.model.RSSItem;
import com.example.android.rssreader.utils.RSSFeedHandler;
import com.example.android.rssreader.utils.RSSUtils;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.StringReader;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static com.example.android.rssreader.ui.DescriptionActivity.FEED_ID_KEY;
import static com.example.android.rssreader.ui.DescriptionActivity.SELECTED_ITEM_POS_KEY;

public class MainActivity extends AppCompatActivity implements RSSFeedAdapter.ViewHolder.RSSItemClickListener,
        AddRSSChannelDialogFragment.AddChannelDialogListener {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private MyBroadcastReceiver receiver;

    // DELFI
//    public static final String URL="http://rus.delfi.lv/rss.php";
    public static final String URL = "http://delfi.lv/rss.php";
    // BBC
//    public static final String URL="http://feeds.bbci.co.uk/news/world/rss.xml";
    // CNN
//    public static final String URL="http://rss.cnn.com/rss/cnn_tech.rss";
    // Lenta
    // TODO у неё нету lastBuildDate - хэндли это
    // TODO также не читате их дескрипшн у items  Unparseable date: "Wed, 02 N" (at offset 8)
//    public static final String URL="https://lenta.ru/rss/news";
    // Yandex
    // TODO у них дата по другому хранится
//    public static final String URL="https://news.yandex.ru/world.rss";
    // EurekaAlert!
//    public static final String URL="https://www.eurekalert.org/rss/technology_engineering.xml";

    RequestQueue queue;

    private LinearLayout rootLayout;
    private TextView outputTv;
    private RecyclerView rssItemsRv;
    private RSSFeedAdapter rssItemsAdapter;
    private ImageView channelImageIv;
    private RSSFeed feed;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiver = new MyBroadcastReceiver();
        this.registerReceiver(receiver, new IntentFilter(MyBroadcastReceiver.ACTION));

        // bind views
        rootLayout = (LinearLayout) findViewById(R.id.content);
        outputTv = (TextView) findViewById(R.id.output_tv);
        rssItemsRv = (RecyclerView) findViewById(R.id.item_rv);
        channelImageIv = (ImageView) findViewById(R.id.channel_image);

        // database
        final RSSDBHelper helper = RSSDBHelper.getInstance(this);
        Cursor dbCursor = helper.getReadableDatabase().query(RSSDBContract.FEED_TABLE_NAME, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        while (dbCursor.moveToNext()) {
            Log.d(LOG_TAG, "title: " + dbCursor.getString(dbCursor.getColumnIndex("title")) + "");
        }
        String columns = "";
        for (String column : columnNames) {
            columns += " " + column;
        }

        final RSSUtils utils = new RSSUtils(this);
        final RSSUtils.OnFeedDownloadedListener feedListener = new RSSUtils.OnFeedDownloadedListener() {
            @Override
            public void feedDownloaded(RSSFeed f) {
                if (f != null) {
                    id = helper.saveRSSFeed(f);
                    // db
                    // TODO save image urls in db too
                    feed = helper.getRSSFeed(id, true);
//                    feed = f;
                    // we got feed
                    Palette palette = Palette.from(feed.getLogo()).generate();
                    channelImageIv.setImageBitmap(feed.getLogo());
                    // set panel colors
                    int defaultPanelColor = 0xFF808080;
                    int defaultFabColor = 0xFFEEEEEE;
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(palette.getDarkVibrantColor(defaultPanelColor)));
                    rootLayout.setBackgroundColor(palette.getVibrantColor(defaultPanelColor));
                    getSupportActionBar().setTitle(feed.getTitle());
                    rssItemsRv.setBackgroundDrawable(new ColorDrawable(palette.getLightVibrantColor(defaultPanelColor)));

                    DateFormat formatter = DateFormat.getDateTimeInstance(
                            DateFormat.DEFAULT,
                            DateFormat.SHORT,
                            Locale.getDefault());
                    String formattedDate = formatter.format(new Date(feed.getLastBuildDate()));
                    outputTv.setText(feed.getTitle() + "\n" + feed.getDescription() + "\n"
                            + "lastBuildDate=" + formattedDate
                            + "\nlink " + feed.getLink());

                    // use a linear layout manager
                    LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                    rssItemsRv.setLayoutManager(layoutManager);

                    // specify an adapter (see also next example)
                    rssItemsAdapter = new RSSFeedAdapter(MainActivity.this, feed.getAllItems(), MainActivity.this);
                    rssItemsRv.setAdapter(rssItemsAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Invalid feed", Toast.LENGTH_SHORT).show();
                }
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                utils.downloadFeed(URL, feedListener);
                ;
            }
        });
        thread.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                // Settings
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_about:
                showEditDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClicked(int pos) {
        Log.d(LOG_TAG, "click at: " + pos);
        RSSItem item = feed.getAllItems().get(pos);
        Intent intent = new Intent(this, DescriptionActivity.class);
        intent.putExtra(FEED_ID_KEY, id);
        intent.putExtra(SELECTED_ITEM_POS_KEY, pos);
        // TODO probably configure some flags
        // TODO or replace with parcelable
        startActivity(intent);

        // FRAGMENT VERSION

//        DescriptionFragment fragment = DescriptionFragment.newInstance(feed,pos);
//        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.placeholder);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().add(R.id.placeholder,fragment).commit();

    }

    @Override
    public void onDialogPositiveClick(String url) {
        // attempt to download
        Toast.makeText(this, "Added to feed " + url, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    class MyBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION = "com.example.ACTION_SETTINGS_CHANGED";

        @Override
        public void onReceive(Context context, Intent intent) {
            String test = intent.getStringExtra("dataToPass");
            Log.d(LOG_TAG, "onReceive()");
            // refresh adapter
            // TODO refactor, probably
            rssItemsAdapter.refreshSettings();
            rssItemsAdapter.notifyDataSetChanged();
        }
    }

    // TODO refactor
    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        AddRSSChannelDialogFragment editNameDialogFragment = AddRSSChannelDialogFragment.newInstance("Some Title");
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }
}
