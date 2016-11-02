package com.example.android.rssreader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.StringReader;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity implements RSSFeedAdapter.ViewHolder.RSSItemClickListener {
    public static final String LOG_TAG=MainActivity.class.getSimpleName();

    private MyBroadcastReceiver receiver;

    // DELFI
    public static final String URL="http://rus.delfi.lv/rss.php";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiver = new MyBroadcastReceiver();
        this.registerReceiver(receiver, new IntentFilter(MyBroadcastReceiver.ACTION));

        // bind views
        rootLayout= (LinearLayout) findViewById(R.id.content);
        outputTv= (TextView) findViewById(R.id.output_tv);
        rssItemsRv= (RecyclerView) findViewById(R.id.item_rv);
        channelImageIv= (ImageView) findViewById(R.id.channel_image);

        // database
        final RSSDBHelper helper=RSSDBHelper.getInstance(this);
        Cursor dbCursor = helper.getReadableDatabase().query(RSSDBContract.FEED_TABLE_NAME, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        while(dbCursor.moveToNext()){
            Log.d(LOG_TAG,"title: "+dbCursor.getString(dbCursor.getColumnIndex("title"))+"");
        }
        String columns="";
        for (String column: columnNames) {
            columns+=" "+column;
        }
        Log.d(LOG_TAG,"db columns: "+columns);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG,response);

                            // FIXME в некоторых рсс надо декодить кирилилцу, в некоторых не надо
                            // decode cyrillic
//                            String s = URLDecoder.decode(URLEncoder.encode(response, "iso8859-1"),"UTF-8");
//                            Log.d(LOG_TAG,s);
//                            outputTv.setText(s);
                            feed=readFile(response);
                            if(feed!=null){
                                // TODO refactor-move
                                // download channel image
                                // Retrieves an image specified by the URL, displays it in the UI.
                                ImageRequest request = new ImageRequest(feed.getImageUri(),
                                        new Response.Listener<Bitmap>() {
                                            @Override
                                            public void onResponse(Bitmap bitmap) {
                                                Log.d(LOG_TAG,"bitmap byte count: "+bitmap.getByteCount()+"");
                                                feed.setLogo(bitmap);
                                                Palette palette = Palette.from(bitmap).generate();
                                                channelImageIv.setImageBitmap(bitmap);
                                                // set panel colors
                                                int defaultPanelColor = 0xFF808080;
                                                int defaultFabColor = 0xFFEEEEEE;
//                                                rootLayout.setBackgroundColor(palette.getVibrantColor(defaultPanelColor));
//                                                rootLayout.setBackgroundColor(palette.getDarkVibrantColor(defaultPanelColor));
                                                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(palette.getDarkVibrantColor(defaultPanelColor)));
                                                rootLayout.setBackgroundColor(palette.getVibrantColor(defaultPanelColor));
                                                getSupportActionBar().setTitle(feed.getTitle());
                                                rssItemsRv.setBackgroundDrawable(new ColorDrawable(palette.getLightVibrantColor(defaultPanelColor)));

                                                long id=helper.saveRSSFeed(feed);
                                                feed=helper.getRSSFeed(id,true);
                                                // TODO put to db
                                                helper.saveRSSFeed(
                                                        feed
                                                );
                                                DateFormat formatter = DateFormat.getDateTimeInstance(
                                                        DateFormat.DEFAULT,
                                                        DateFormat.SHORT,
                                                        Locale.getDefault());
                                                String formattedDate = formatter.format(new Date(feed.getLastBuildDate()));
                                                outputTv.setText(feed.getTitle()+"\n"+feed.getDescription()+"\n"
                                                        +"lastBuildDate="+formattedDate
                                                        +"\nlink "+feed.getLink());

                                                // use a linear layout manager
                                                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                                                rssItemsRv.setLayoutManager(layoutManager);

                                                // specify an adapter (see also next example)
                                                rssItemsAdapter = new RSSFeedAdapter(MainActivity.this,feed.getAllItems(),MainActivity.this);
                                                rssItemsRv.setAdapter(rssItemsAdapter);

                                            }
                                        }, 0, 0, null,
                                        new Response.ErrorListener() {
                                            public void onErrorResponse(VolleyError error) {
                                                // error
                                                Toast.makeText(MainActivity.this, "Error downloading image", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                queue.add(request);

                            }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG,error.toString());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    public RSSFeed readFile(String text) {
        try {
            // 1. get the XML reader (SAX api)
            // just a boilerplate code
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlreader = parser.getXMLReader();

            // 2. set content handler
            RSSFeedHandler theRssHandler = new RSSFeedHandler();
            xmlreader.setContentHandler(theRssHandler);

            // 3. read the file from internal storage
//            FileInputStream in = this.openFileInput(FILENAME);

            // 4. parse the data
            // sax parser will use this to read from inputstream and parsing
            InputSource is = new InputSource(new StringReader( text ) );
            // start parsing file
            xmlreader.parse(is);

            // 5. set the feed in the activity
            // retrieve feed from our handler object (that was set as content handler for xmlreader)
            RSSFeed feed = theRssHandler.getFeed();
            for (RSSItem item:feed.getAllItems()) {
                Log.d(LOG_TAG,item.toString());
            }
            return feed;
        }
        catch (Exception e) {
            Log.e("News reader", e.toString());
            return null;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_settings:
                // open settings activity
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClicked(int pos) {
        Log.d(LOG_TAG,"click at: "+pos);
        Bundle bundle = new Bundle();
        RSSItem item = feed.getAllItems().get(pos);
        // TODO refactor - no magic strings
        bundle.putString("title",item.getTitle());
        bundle.putString("description",item.getDescription());
        bundle.putLong("pubDate",item.getPubDate());
        bundle.putString("link",item.getLink());
        Intent intent = new Intent(this,DescriptionActivity.class);
        intent.putExtra("item",bundle);

    }

    class MyBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION = "com.example.ACTION_SETTINGS_CHANGED";

        @Override
        public void onReceive(Context context, Intent intent) {
            String test = intent.getStringExtra("dataToPass");
            Log.d(LOG_TAG,"onReceive()");
            // refresh adapter
            // TODO refactor, probably
            rssItemsAdapter.refreshSettings();
            rssItemsAdapter.notifyDataSetChanged();
        }
    }

}
