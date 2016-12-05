package com.example.android.rssreader.adapters;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.android.rssreader.Networker;
import com.example.android.rssreader.R;
import com.example.android.rssreader.RSSReaderApplicationSettings;
import com.example.android.rssreader.model.RSSItem;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RSSFeedAdapter extends RecyclerView.Adapter<RSSFeedAdapter.ViewHolder> {

    private static final String LOG_TAG = RSSFeedAdapter.class.getSimpleName();
    private Context ctx;
    private ArrayList<RSSItem> items;

    private Networker networker;

    private  boolean cropDesc;
    private  boolean showDesc;
    private  int maxDescCharsCount;

    private ViewHolder.RSSItemClickListener listener;

    public RSSFeedAdapter(Context ctx, ArrayList<RSSItem> items, ViewHolder.RSSItemClickListener listener) {
        this.ctx = ctx;
        this.items = items;
        this.listener=listener;
        networker=Networker.getInstance(ctx);
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        RSSItem item = items.get(position);
        // might or might not be
        holder.setTitle(item.getTitle());
        // show first 100 characters if not selected "show full desc" in preferences
        // prepare
        if(!showDesc){
            // do not show description
            holder.bind(item,0);
        }else if(cropDesc) {
            // crop description to maxDescCharsCount
            holder.bind(item,maxDescCharsCount);
        }else{
            // do not crop
            holder.bind(item,-1);
        }
        // else - do not show text at all

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.itemClicked(position);
            }
        });

        // image
        // TODO refactor
        List<String> urls = item.getImageUrls();
        if(urls!=null){
            String url=urls.get(0);
            networker.loadImage(url,holder.networkImageView);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        private RSSItem rssItem;
        private TextView titleTv;
        private TextView descriptionTv;
        private ImageView networkImageView;
        private TextView dateTv;
        private View dividerView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView= (CardView) itemView.findViewById(R.id.card_view);
            titleTv= (TextView) itemView.findViewById(R.id.title_text);
            networkImageView = (ImageView) itemView.findViewById(R.id.list_description_image);
            descriptionTv= (TextView) itemView.findViewById(R.id.description_text);
            dateTv= (TextView) itemView.findViewById(R.id.date_tv);
            dividerView=itemView.findViewById(R.id.item_divider);
//            descriptionWv= (WebView) itemView.findViewById(R.id.description_text);
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
            if(descCharsCount==0 || rssItem.getPlainTextDescription().isEmpty()){
                // "do not show description" is selected or there is no description
                dividerView.setVisibility(View.GONE);
                descriptionTv.setVisibility(View.GONE);
            }else {
                dividerView.setVisibility(View.VISIBLE);
                descriptionTv.setVisibility(View.VISIBLE);
                if (descCharsCount == -1) {
                    // show full description
                    descriptionTv.setText(rssItem.getPlainTextDescription());
                } else{
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
            // TODO refactor
            if(item.getImageUrls()!=null){
                dividerView.setVisibility(View.VISIBLE);
            }

        }

        public void setDescription(String description){
            descriptionTv.setText(description);
//            descriptionWv.loadDataWithBaseURL("", description, "text/html", "UTF-8", "");
        }



        public interface RSSItemClickListener{
            void itemClicked(int pos);
        }

    }

}
