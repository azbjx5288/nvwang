package com.neinei.cong.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class WaneImageView extends android.support.v7.widget.AppCompatImageView {
    public WaneImageView(Context context) {
        this(context,null);
    }

    public WaneImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WaneImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), 15, 15, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
