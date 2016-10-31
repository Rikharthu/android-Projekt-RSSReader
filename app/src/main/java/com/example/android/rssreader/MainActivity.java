package com.example.android.rssreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.rssreader.adapters.RSSFeedAdapter;
import com.example.android.rssreader.model.RSSFeed;
import com.example.android.rssreader.model.RSSItem;
import com.example.android.rssreader.utils.RSSFeedHandler;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.FileInputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG=MainActivity.class.getSimpleName();

    // DELFI
    public static final String URL="http://rus.delfi.lv/rss.php";
    // BBC
//    public static final String URL="http://feeds.bbci.co.uk/news/world/rss.xml";

    private TextView outputTv;
    private RecyclerView rssItemsRv;
    private RecyclerView.Adapter rssItemsAdapter;

    private RSSFeed feed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bind views
        outputTv= (TextView) findViewById(R.id.output_tv);
        rssItemsRv= (RecyclerView) findViewById(R.id.item_rv);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG,response);
                        try {
                            // decode cyrillic
                            String s = URLDecoder.decode(URLEncoder.encode(response, "iso8859-1"),"UTF-8");
//                            outputTv.setText(s);
                            feed=readFile(s);
                            if(feed!=null){
                                // use a linear layout manager
                                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                                rssItemsRv.setLayoutManager(layoutManager);

                                // specify an adapter (see also next example)
                                rssItemsAdapter = new RSSFeedAdapter(MainActivity.this,feed.getAllItems());
                                rssItemsRv.setAdapter(rssItemsAdapter);
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
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

}
