package com.neinei.cong.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.neinei.cong.AppConfig;

public class GlideUtils {

    public static void glide(Context context, String url, ImageView imageView) {

        String img = "";

        if (url.contains("http")) {
            img = url;
        } else {
            img = AppConfig.MAIN_URL + url;
        }
        Glide.with(context).load(img).into(imageView);
    }
}
