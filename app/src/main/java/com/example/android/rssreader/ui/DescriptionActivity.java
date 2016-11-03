package com.example.android.rssreader.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.rssreader.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class DescriptionActivity extends AppCompatActivity {
    public static final String LOG_TAG=DescriptionActivity.class.getSimpleName();

    private TextView channelTv;
    private TextView titleTv;
    private TextView dateTv;
    private Button readnowBtn;
    private ImageView imageView;
    private WebView descriptionWv;

    private ShareActionProvider mShareActionProvider;

    String title;
    String description ;
    Long pubDate ;
    String link;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        channelTv= (TextView) findViewById(R.id.description_channel_tv);
        titleTv= (TextView) findViewById(R.id.description_title_tv);
        dateTv= (TextView) findViewById(R.id.description_date_tv);
        readnowBtn= (Button) findViewById(R.id.description_readnow_btn);
        descriptionWv= (WebView) findViewById(R.id.description_webview);
        imageView= (ImageView) findViewById(R.id.description_img);

        Intent intent = getIntent();
        if(intent==null){
            // TODO ERROR
            finish();
        }
        Bundle extras = intent.getBundleExtra("item");
        title = extras.getString("title");
        description = extras.getString("description");
        pubDate = extras.getLong("pubDate",System.nanoTime());
        link = extras.getString("link");

        titleTv.setText(title);
        DateFormat formatter = DateFormat.getDateTimeInstance(
                DateFormat.DEFAULT,
                DateFormat.SHORT,
                Locale.getDefault());
        String formattedDate = formatter.format(new Date(pubDate));
        dateTv.setText(formattedDate);

        descriptionWv.getSettings().setJavaScriptEnabled(true);
        descriptionWv.setScrollContainer(false);
        descriptionWv.loadDataWithBaseURL("", description, "text/html", "UTF-8", "");

        readnowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(link));
                // TODO probably check if someone can handle that
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_description, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // TODO make it sexier and use device language
        // TODO add link to download app
        // TODO USE MODEL'S getPlainTextDescr.. from parcelable
        String shortInfo=title+"\n"+ Html.fromHtml(description).toString()+"\n\nRead more at "+link+"\n\n"+"shared to you with RSS Reader. Get it now on Google Market: google.lv";

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain").setText(shortInfo).getIntent();
        setShareIntent(shareIntent);

        // Return true to display menu
        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

}
