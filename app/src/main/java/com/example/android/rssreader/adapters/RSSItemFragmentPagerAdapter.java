package com.example.android.rssreader.adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.rssreader.model.RSSItem;
import com.example.android.rssreader.ui.DescriptionFragment;

import java.util.ArrayList;

public class RSSItemFragmentPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<RSSItem> items;
    private Context context;
    public RSSItemFragmentPagerAdapter(FragmentManager fragmentManager, Context context, ArrayList<RSSItem> items){
        super(fragmentManager);
        this.context=context;
        this.items=items;
    }

    @Override
    public Fragment getItem(int position) {
        return DescriptionFragment.newInstance(items.get(position));
    }

    @Override
    public int getCount() {
        return items.size();
    }
}
