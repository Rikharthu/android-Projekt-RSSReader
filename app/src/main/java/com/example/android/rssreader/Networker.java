package com.example.android.rssreader;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class Networker {

    private static final Object LOCK = new Object();

    private static Networker instance;
    private Context context;
    private RequestQueue requestQueue;

    private Networker(Context ctx){
        context=ctx.getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
    }

    public static Networker getInstance(Context context){
        if(instance==null){
            synchronized (LOCK){
                if(instance==null){
                    instance = new Networker(context);
                }
            }
        }
        return instance;
    }

    public void loadImage(String imageUrl, final ImageView imageView){
        ImageRequest request = new ImageRequest(imageUrl,
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
        requestQueue.add(request);
    }
}
