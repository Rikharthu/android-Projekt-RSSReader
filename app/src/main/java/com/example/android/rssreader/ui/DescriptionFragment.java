package com.example.android.rssreader.ui;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.rssreader.R;
import com.example.android.rssreader.model.RSSItem;
import com.example.android.rssreader.utils.StringUtils;

import java.util.List;

public class DescriptionFragment extends Fragment {
    public static final String LOG_TAG = DescriptionFragment.class.getSimpleName();

    public static final String FEED_ITEM_KEY = "FEED_ITEM_KEY";
    private RSSItem item;
    private int currentPosition;
    // Views
    private ViewGroup rootView;
    private TextView channelTv;
    private TextView titleTv;
    private TextView dateTv;
    private TextView descriptionTv;
    private Button readnowBtn;
    private ImageView imageView;
    private WebView descriptionWv;
    private ScrollView scrollView;
    private LinearLayout contentLayout;

    private ShareActionProvider shareActionProvider;


    public static DescriptionFragment newInstance(RSSItem item) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(FEED_ITEM_KEY, item);
        DescriptionFragment fragment = new DescriptionFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(FEED_ITEM_KEY)) {
            item = arguments.getParcelable(FEED_ITEM_KEY);
        } else {
            // FIXME handle properly
            Toast.makeText(getContext(), "Error occured", Toast.LENGTH_SHORT).show();
            getActivity().getFragmentManager().popBackStack();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_description, container, false);
        wireViews(rootView);
        initData();
        return rootView;
    }

    /**
     * Populate Views with data
     */
    private void initData() {
        titleTv.setText(item.getTitle());
        dateTv.setText(StringUtils.getFormattedLocalDate(item.getPubDate()));
//        descriptionTv.setText(Html.fromHtml(item.getDescription()));
        descriptionWv.getSettings().setJavaScriptEnabled(true);
        descriptionWv.setBackgroundColor(Color.TRANSPARENT);
        descriptionWv.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                // hid webview, use description text view
                // TODO check if is not null or empty and replace with "no desc available"
                descriptionWv.setVisibility(View.GONE);
                descriptionTv.setVisibility(View.VISIBLE);
                descriptionTv.setText(item.);
            }
        });
        // for TalkBack and screen readers
        if(item.getPlainTextDescription()==null || item.getPlainTextDescription().isEmpty()){
            // TODO hide webview, put textview
            descriptionWv.loadDataWithBaseURL("", "<i style=\"color:#c8c8c8\">no description available</i>", "text/html", "UTF-8", "");
            descriptionWv.setVisibility(View.GONE);
            descriptionTv.setVisibility(View.VISIBLE);
            // TODO remove unsupported tags (Jsoup)
            descriptionTv.setText(Html.fromHtml(item.getDescription()));
        }else {
            descriptionWv.loadDataWithBaseURL("", item.getDescription(), "text/html", "UTF-8", "");
            descriptionWv.setContentDescription(item.getPlainTextDescription());
        }
        readnowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(item.getLink()));
                // delegate that to browser app
                startActivity(i);
            }
        });
        // download images if any
        List<String> urls = item.getImageUrls();
        if(urls!=null){
            RequestQueue queue= Volley.newRequestQueue(getContext());
            String url=urls.get(0);
            ImageRequest request = new ImageRequest(url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            imageView.setVisibility(View.VISIBLE);
                            imageView.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            // TODO handle error
                            error.toString();
                        }
                    });
            queue.add(request);
        }
    }

    /**
     * Initialize Views from the layout
     */
    private void wireViews(View rootView) {
        channelTv = (TextView) rootView.findViewById(R.id.description_channel_tv);
        titleTv = (TextView) rootView.findViewById(R.id.description_title_tv);
        dateTv = (TextView) rootView.findViewById(R.id.description_date_tv);
        descriptionTv = (TextView) rootView.findViewById(R.id.details_description_text_tv);
        readnowBtn = (Button) rootView.findViewById(R.id.description_readnow_btn);
        descriptionWv = (WebView) rootView.findViewById(R.id.description_webview);
        imageView = (ImageView) rootView.findViewById(R.id.description_img);
        scrollView = (ScrollView) rootView.findViewById(R.id.description_scroll_view);
    }
}
