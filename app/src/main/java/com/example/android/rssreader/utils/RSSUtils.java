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

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
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
    private static SimpleDateFormat[] parseInFormats =
            {
                    // most popular first
                    new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH),// Wed, 02 Nov 2016 13:15:11 +0200
                    new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH) //02 Nov 2016 15:06:48 +0300
            };

    public static final SimpleDateFormat DATE_OUT_FORMAT =
            new SimpleDateFormat("EE h:mm a (MMM d)", Locale.ENGLISH);

    public static final SimpleDateFormat DATE_IN_FORMAT =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    public static String is8859toUtf8(String text){

        try {
            return new String(text.getBytes("8859_1"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }

    }

    public static long strDateToMillis(String dateStr){
        Date date=null;
        // try all our date formats
        for(int i= 0;i<parseInFormats.length;i++){
            try {
                Log.d(LOG_TAG,"try "+parseInFormats[i].toPattern());
                date = parseInFormats[i].parse(dateStr);
                // we wont get any further if parse fails (format is wrong)
                // replace with active for optimization
                Log.d(LOG_TAG,"success");
                if(parseInFormats[0]!=parseInFormats[i]){
                    Log.d(LOG_TAG,"swap");
                    SimpleDateFormat format = parseInFormats[0];
                    parseInFormats[0]=parseInFormats[i];
                    parseInFormats[i]=format;
                }
                return date.getTime();
            }
            catch (ParseException e) {
                Log.d(LOG_TAG,"fail");
                e.printStackTrace();
            }
        }
        return -1;
    }

    public void downloadRSSFeed(String feedUrl, final OnFeedDownloadedListener listener){
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, feedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG,response);
                        // to utf 8
                        response= is8859toUtf8(response);
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
            Log.d(LOG_TAG,"source text is "+text);
            // 4. parse the data
            // sax parser will use this to read from inputstream and parsing
            InputSource is = new InputSource(new StringReader(text ) );
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
