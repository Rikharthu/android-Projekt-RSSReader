package com.example.android.rssreader.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.android.rssreader.R;
import com.example.android.rssreader.model.RSSItem;

import java.util.ArrayList;

public class RSSFeedAdapter extends RecyclerView.Adapter<RSSFeedAdapter.ViewHolder> {

    private Context ctx;
    private ArrayList<RSSItem> items;

    public RSSFeedAdapter(Context ctx, ArrayList<RSSItem> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @Override
    public RSSFeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.feed_item,parent,false);
        RSSFeedAdapter.ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // might or might not be
        holder.setTitle(items.get(position).getTitle());
        holder.setDescription(items.get(position).getPlainTextDescription());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTv;
        private TextView descriptionTv;
        private WebView descriptionWv;


        public ViewHolder(View itemView) {
            super(itemView);
            titleTv= (TextView) itemView.findViewById(R.id.title_text);
            descriptionTv= (TextView) itemView.findViewById(R.id.description_text);
//            descriptionWv= (WebView) itemView.findViewById(R.id.description_text);
        }

        public void setTitle(String title){
            titleTv.setText(title);
        }

        public void setDescription(String description){
            descriptionTv.setText(description);
//            descriptionWv.loadDataWithBaseURL("", description, "text/html", "UTF-8", "");
        }
    }

}
