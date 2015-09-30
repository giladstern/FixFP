package com.example.gilad.fp.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Gilad on 9/30/2015.
 */
public class Overlay extends View {
    ArrayList<RectF> toDraw = new ArrayList<>();

    public Overlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public void highlight(int top, int bottom)
    {
        toDraw.clear();
        int height = getHeight();
        int midHeight = height * 4 / 9;
        float width = getWidth() / 5f;
        toDraw.add(new RectF(top * width, 0, (top + 1) * width, midHeight));
        toDraw.add(new RectF(bottom * width, midHeight, (bottom + 1) * width, height));

        invalidate();
    }

    public void highlight(int top, int middle, int bottom)
    {
        toDraw.clear();
//        float height = getHeight() / 6f;
//        float width = getWidth() / 10f;
//        float radius = Math.min(getHeight() / 3f, getWidth() / 5f) / 2;
//        float[] topCirc = {(top * 2 + 1) * width, height, radius};
//        float[] middleCirc = {(middle * 2 + 1) * width, height * 3, radius};
//        float[] bottomCirc = {(bottom * 2 + 1) * width, height * 5, radius};
//        toDraw.add(topCirc);
//        toDraw.add(middleCirc);
//        toDraw.add(bottomCirc);

        float height = getHeight() / 3f;
        float width = getWidth() / 5f;
        toDraw.add(new RectF(top * width, 0, (top + 1) * width, height));
        toDraw.add(new RectF(middle * width, height, (middle + 1) * width, height * 2));
        toDraw.add(new RectF(bottom * width, height * 2, (bottom + 1) * width, height * 3));
        invalidate();
    }

    public void off()
    {
        toDraw.clear();
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(64,255,255,0));

        for(RectF shape:toDraw)
        {
//            canvas.drawCircle(circle[0], circle[1], circle[2], paint);
            canvas.drawRect(shape, paint);
        }
    }
}
