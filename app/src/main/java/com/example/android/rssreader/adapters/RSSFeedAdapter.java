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

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RSSFeedAdapter extends RecyclerView.Adapter<RSSFeedAdapter.ViewHolder> {

    private static final String LOG_TAG = RSSFeedAdapter.class.getSimpleName();
    private Context ctx;
    private ArrayList<RSSItem> items;
    private int maxDescCharsCount;

    private boolean cropDesc;
    private boolean showDesc;

    private static ViewHolder.RSSItemClickListener listener;

    public RSSFeedAdapter(Context ctx, ArrayList<RSSItem> items, ViewHolder.RSSItemClickListener listener) {
            this.ctx = ctx;
        this.items = items;
        RSSFeedAdapter.listener=listener;
        refreshSettings();
    }


    public void refreshSettings(){
        RSSReaderApplicationSettings settings = new RSSReaderApplicationSettings(ctx);
        cropDesc=settings.shouldCropDescription();
        maxDescCharsCount=settings.getMaxCropDescChars();
        showDesc=settings.shouldShowDescription();
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
        if(!showDesc){
            // do not show description
            holder.bind(items.get(position),0);
        }else if(cropDesc) {
            // crop description to maxDescCharsCount
            holder.bind(items.get(position),maxDescCharsCount);
        }else{
            // do not crop
            holder.bind(items.get(position),-1);
        }
        // else - do not show text at all
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RSSItem rssItem;
        private TextView titleTv;
        private TextView descriptionTv;
        private TextView dateTv;
        private WebView descriptionWv;
        private View dividerView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv= (TextView) itemView.findViewById(R.id.title_text);
            descriptionTv= (TextView) itemView.findViewById(R.id.description_text);
            dateTv= (TextView) itemView.findViewById(R.id.date_tv);
            dividerView=itemView.findViewById(R.id.item_divider);
//            descriptionWv= (WebView) itemView.findViewById(R.id.description_text);
            itemView.setOnClickListener(this);
        }

        public void setTitle(String title){
            titleTv.setText(title);
        }

        /**
         * Bind this ViewHolder to represent a particular RSSItem
         * @param item RSSItem to bind
         * @param descCharsCount how many description characters to show.
         *                       -1 to show all,
         *                       0 to hide description.
         */
        public void bind(RSSItem item, int descCharsCount){
            rssItem=item;
            // TODO refactor - extract somewhere (to utils probably)
            PrettyTime p = new PrettyTime();
            // match device locale
            p.setLocale(Locale.getDefault());
            dateTv.setText(p.format(new Date(item.getPubDate())));
            titleTv.setText(rssItem.getTitle());
            // description text
            if(descCharsCount==0){
                dividerView.setVisibility(View.GONE);
                descriptionTv.setVisibility(View.GONE);
            }else {
                dividerView.setVisibility(View.VISIBLE);
                descriptionTv.setVisibility(View.VISIBLE);
                if (descCharsCount == -1) {
                    // show full description
                    descriptionTv.setText(rssItem.getPlainTextDescription());
                } else if (descCharsCount != 0) {
                    String fullDesc = rssItem.getPlainTextDescription();
                    if (fullDesc.length() <= descCharsCount) {
                        descriptionTv.setText(fullDesc);
                    } else {
                        // index of first ' ', ','  after descCharsCount characters
                        int endIndex = fullDesc.indexOf(' ', descCharsCount);
                        if (endIndex != -1) {
                            descriptionTv.setText(fullDesc.substring(0, endIndex) + " . . . ");
                        } else {
                            // find first space before descCharsCount (reverse string and find first ' ' and sum up)
                            endIndex = new StringBuilder(fullDesc.substring(0, descCharsCount)).reverse().toString().indexOf(' ');
                            if (endIndex != -1) {
                                descriptionTv.setText(fullDesc.substring(0, descCharsCount - endIndex) + " . . . ");
                            } else {
                                descriptionTv.setText(fullDesc.substring(descCharsCount));
                            }
                        }
                    }
                }
            }

        }

        public void setDescription(String description){
            descriptionTv.setText(description);
//            descriptionWv.loadDataWithBaseURL("", description, "text/html", "UTF-8", "");
        }

        @Override
        public void onClick(View view) {
            listener.itemClicked(getAdapterPosition());
        }

        public interface RSSItemClickListener{
            void itemClicked(int pos);
        }

    }

}
