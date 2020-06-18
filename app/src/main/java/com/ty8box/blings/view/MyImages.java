package com.ty8box.blings.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MyImages extends android.support.v7.widget.AppCompatImageView{

    public MyImages(Context context) {
        super(context);
    }

    public MyImages(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImages(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), getWidth(), getWidth(), Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);

    }
}
