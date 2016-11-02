package com.example.android.rssreader.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.android.rssreader.R;

public class DescriptionActivity extends AppCompatActivity {
    public static final String LOG_TAG=DescriptionActivity.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
    }
}
