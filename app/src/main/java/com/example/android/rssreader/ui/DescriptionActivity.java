package com.example.android.rssreader.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.rssreader.R;
import com.example.android.rssreader.adapters.RSSItemFragmentPagerAdapter;
import com.example.android.rssreader.database.RSSDBHelper;
import com.example.android.rssreader.model.RSSFeed;
import com.example.android.rssreader.model.RSSItem;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DescriptionActivity extends AppCompatActivity {
    public static final String LOG_TAG=DescriptionActivity.class.getSimpleName();


    public static final String SELECTED_ITEM_POS_KEY="SELECTED_ITEM_POS";
    public static final String FEED_ID_KEY="FEED_ID_KEY";

    private RSSFeed feed;
    private int currentItemPosition;
    private ShareActionProvider mShareActionProvider;
    private DescriptionFragment currentFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        RSSDBHelper helper = RSSDBHelper.getInstance(this);
        long id = getIntent().getLongExtra(FEED_ID_KEY,-1);
        currentItemPosition = getIntent().getIntExtra(SELECTED_ITEM_POS_KEY,1);
        // FIXME DEBUG (HAS IMAGES)
//        feed = helper.getRSSFeed(id, true);
        feed = getIntent().getParcelableExtra("feed");

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        RSSItemFragmentPagerAdapter pagerAdapter = new RSSItemFragmentPagerAdapter(getSupportFragmentManager()
                ,this,feed.getAllItems());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(currentItemPosition);

        if(savedInstanceState==null) {
//            currentFragment = DescriptionFragment.newInstance(feed.getAllItems().get(currentItemPosition));
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction().add(R.id.placeholder, currentFragment).commit();
        }else{

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
//        getMenuInflater().inflate(R.menu.menu_description, menu);
//
//        // Locate MenuItem with ShareActionProvider
//        MenuItem item = menu.findItem(R.id.menu_item_share);
//
//        // Fetch and store ShareActionProvider
//        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
//
//        // TODO make it sexier and use device language
//        // TODO add link to download app
//        // TODO USE MODEL'S getPlainTextDescr.. from parcelable
//        //String shortInfo=title+"\n"+ Html.fromHtml(description).toString()+"\n\nRead more at "+link+"\n\n"+"shared to you with RSS Reader. Get it now on Google Market: google.lv";
//        String shareText = "\"\n"+rssItem.getTitle()+"\"\n\t"+rssItem.getPlainTextDescription();
//        String footer = getResources().getString(R.string.share_action_disclaimer_footer);
//
//        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
//                .setType("text/plain").setText(shareText+"\n\n"+footer).getIntent();
//        setShareIntent(shareIntent);
//
//        // Return true to display menu
        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

}
