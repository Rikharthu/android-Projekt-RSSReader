package com.example.android.rssreader.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;


public class MyWebView extends WebView {

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super((Context)null, (AttributeSet)null, 0, 0);
    }

    @Override
    public void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh); // don't forget this or things will break!
//        Log.d(TAG, "WebView height" + getContentHeight());
    }

}
