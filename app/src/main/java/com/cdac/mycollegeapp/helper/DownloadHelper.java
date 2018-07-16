package com.cdac.mycollegeapp.helper;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Saurabh on 29-Jun-17.
 */

public class DownloadHelper {

    public static void loadImagesWithGlide(Context context, int imageRes, ImageView imageView)
    {
        Glide.with(context)
                .load(imageRes)
                .crossFade()
                .into(imageView);
    }

    public static void loadImageWithGlideURL(Context context, String url, ImageView imageView)
    {
        Glide.with(context)
                .load(url)
                .crossFade()
                .into(imageView);
    }
}
