package com.example.android.rssreader.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.android.rssreader.R;
import com.example.android.rssreader.RSSReaderApplicationSettings;
import com.example.android.rssreader.model.RSSItem;

import java.util.ArrayList;

public class RSSFeedAdapter extends RecyclerView.Adapter<RSSFeedAdapter.ViewHolder> {

    private static final String LOG_TAG = RSSFeedAdapter.class.getSimpleName();
    private Context ctx;
    private ArrayList<RSSItem> items;
    private int maxDescCharsCount;

    private boolean cropDesc;

    public RSSFeedAdapter(Context ctx, ArrayList<RSSItem> items) {
        this.ctx = ctx;
        this.items = items;
        RSSReaderApplicationSettings settings = new RSSReaderApplicationSettings(ctx);
        cropDesc=settings.shouldCropDescription();
        maxDescCharsCount=settings.getMaxCropDescChars();
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

        // show first 100 characters if not selected "show full desc" in preferences
        // prepare
        String fullDesc = items.get(position).getPlainTextDescription();
        if(cropDesc) {
            if (fullDesc.length() <= maxDescCharsCount) {
                holder.setDescription(fullDesc);
            } else {
                // index of first space after 100 characters
                int endIndex = fullDesc.indexOf(' ', maxDescCharsCount);
                if (endIndex != -1) {
                    holder.setDescription(fullDesc.substring(0, endIndex) + " . . . ");
                } else {
                    // find first space before index 60 (reverse string and find first ' ')
                    endIndex = new StringBuilder(fullDesc.substring(0, maxDescCharsCount)).reverse().toString().indexOf(' ');
                    if (endIndex != -1) {
                        holder.setDescription(fullDesc.substring(0, maxDescCharsCount - endIndex) + " . . . ");
                    } else {
                        holder.setDescription(fullDesc.substring(maxDescCharsCount));
                    }
                }
            }
        }else{
            holder.setDescription(fullDesc);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView titleTv;
        private TextView descriptionTv;
        private WebView descriptionWv;
        private RSSItemClickListener itemClickListener;


        public ViewHolder(View itemView) {
            super(itemView);
            titleTv= (TextView) itemView.findViewById(R.id.title_text);
            descriptionTv= (TextView) itemView.findViewById(R.id.description_text);
//            descriptionWv= (WebView) itemView.findViewById(R.id.description_text);
            itemView.setOnClickListener(this);
        }

        public void setTitle(String title){
            titleTv.setText(title);
        }

        public void setDescription(String description){
            descriptionTv.setText(description);
//            descriptionWv.loadDataWithBaseURL("", description, "text/html", "UTF-8", "");
        }

        @Override
        public void onClick(View view) {
//            RSSItemClickListener.click;
        }

        interface RSSItemClickListener{
            void itemClicked(int pos);
        }

    }

}
