package com.example.android.rssreader.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.rssreader.model.RSSFeed;
import com.example.android.rssreader.model.RSSItem;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RSSUtils {

    private static final String LOG_TAG = RSSUtils.class.getSimpleName();
    private Context ctx;
    private RequestQueue queue;
    private RSSFeed feed;

    // TODO do singleton
    public RSSUtils(Context context){
        ctx=context;
        queue= Volley.newRequestQueue(ctx);
    }

    // TODO сделай адаптивным ( если не подошел первый, а подошел второй, то юзать его)
    public static final SimpleDateFormat[] PARSE_OUT_PATTERS=
            {
                    // most popular first
                    new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH),// Wed, 02 Nov 2016 13:15:11 +0200
                    new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH) //02 Nov 2016 15:06:48 +0300
            };

    public static final SimpleDateFormat DATE_OUT_FORMAT =
            new SimpleDateFormat("EE h:mm a (MMM d)", Locale.ENGLISH);

    public static final SimpleDateFormat DATE_IN_FORMAT =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    public static String toUtf8(String text){
        try {
            return URLDecoder.decode(URLEncoder.encode(text, "iso8859-1"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long strDateToMillis(String dateStr){
        Date date=null;
        for (SimpleDateFormat format: PARSE_OUT_PATTERS) {
            try {
                date = format.parse(dateStr.trim());
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date.getTime();
    }

    public void downloadRSSFeed(String feedUrl, final OnFeedDownloadedListener listener){
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, feedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG,response);
                        // to utf 8
                        response=toUtf8(response);
                        feed=readFile(response);
                        if(feed!=null){
                            String imageUrl = feed.getImageUri();
                            if(imageUrl!=null) {
                                // TODO refactor-move
                                // download channel image
                                // Retrieves an image specified by the URL, displays it in the UI.
                                ImageRequest request = new ImageRequest(feed.getImageUri(),
                                        new Response.Listener<Bitmap>() {
                                            @Override
                                            public void onResponse(Bitmap bitmap) {
                                                Log.d(LOG_TAG, "bitmap byte count: " + bitmap.getByteCount() + "");
                                                feed.setLogo(bitmap);
                                                listener.feedDownloaded(feed);
                                            }
                                        }, 0, 0, null,
                                        new Response.ErrorListener() {
                                            public void onErrorResponse(VolleyError error) {
                                                // TODO handle error
                                                listener.feedDownloaded(feed);
                                            }
                                        });
                                queue.add(request);
                            }else{
                                listener.feedDownloaded(feed);
                            }
                        }else{
                            // will be null
                            listener.feedDownloaded(feed);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG,error.toString());
                        listener.feedDownloaded(feed);
                    }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public RSSFeed readFile(String text) {
        try {
            // 1. get the XML reader (SAX api)
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


    public interface OnFeedDownloadedListener {
        void feedDownloaded(RSSFeed feed);
    }

}
