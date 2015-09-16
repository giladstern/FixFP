package com.example.gilad.fp;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Gilad on 8/4/2015.
 */
public class DiagonalStoryFP extends FastPhrase {

    private int topColor;
    private int middleColor;
    private int bottomColor;
    private int textColor;
    private int altTextColor;
    private int topSelected = -1;
    private int middleSelected = -1;
    private int bottomSelected = -1;
    private Resources res;
    private enum Batch {FIRST, SECOND}
    private Batch curBatch;
    private RectF topRect;
    private Paint topPaint;
    private RectF bottomRect;
    private Paint bottomPaint;
    private RectF middleRect;
    private Paint middlePaint;
//    private String[] pass = new String[6];
    private Typeface font;
    private Typeface cartoonFont;
    private Typeface locationsFont;
    private Vibrator vibrator;
    private boolean middleOff = false;
    private boolean bottomOff = false;

//    private ArrayList<TouchData> touchLog = new ArrayList<TouchData>();

//    private Paint linePaint;

    private static final float radiusRatio = 0.4f;

    public DiagonalStoryFP(Context context)
    {
        this(context, null);
    }

    public DiagonalStoryFP(final Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StoryFastPhrase,
                0, 0);
        try {
            topColor = a.getColor(R.styleable.FastPhrase_topColor, Color.DKGRAY);
            middleColor = a.getColor(R.styleable.StoryFastPhrase_middleColor, Color.GRAY);
            bottomColor = a.getColor(R.styleable.FastPhrase_bottomColor, Color.DKGRAY);
            textColor = a.getColor(R.styleable.FastPhrase_textColor, Color.WHITE);
            altTextColor = a.getColor(R.styleable.StoryFastPhrase_altTextColor, Color.YELLOW);

            topPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            topPaint.setStyle(Paint.Style.FILL);
            topPaint.setColor(topColor);

            bottomPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bottomPaint.setStyle(Paint.Style.FILL);
            bottomPaint.setColor(bottomColor);

            middlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            middlePaint.setStyle(Paint.Style.FILL);
            middlePaint.setColor(middleColor);


//            linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//            linePaint.setColor(altTextColor);
//            linePaint.setStyle(Paint.Style.STROKE);
//            linePaint.setStrokeWidth(2);

            res = getResources();
            font = Typeface.createFromAsset(res.getAssets(), "fonts/androidemoji.ttf");
            cartoonFont = Typeface.createFromAsset(res.getAssets(), "fonts/cartoon.ttf"); //Cartoons
            locationsFont = Typeface.createFromAsset(res.getAssets(), "fonts/FamousBuildings.ttf");
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            setWillNotDraw(false);
            for (int i = 0; i < 15 ; i++)
            {
                ColoredTextSwitch child = new ColoredTextSwitch(context, textColor, font);
                child.setId(i + 1);
                addView(child);
            }

            for (int i = 15 ; i < 30 ; i++)
            {
                ColoredTextSwitch child = new ColoredTextSwitch(context, textColor);
                child.setId(i + 1);
                addView(child);
            }

            // Manually laying out the views in relation to each other.
            // First item of each row.
            LayoutParams layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(0).setLayoutParams(layout);

            layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.BELOW, 1);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(15).setLayoutParams(layout);

            layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.BELOW, 16);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(5).setLayoutParams(layout);

            layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.BELOW, 6);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(20).setLayoutParams(layout);

            layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.BELOW, 21);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(10).setLayoutParams(layout);

            layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.BELOW, 11);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(25).setLayoutParams(layout);

            // Align rows according to first item.
            for (int j = 0 ; j < 6 ; j ++)
            {
                for (int i = 1 ; i < 5; i++)
                {
                    layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layout.addRule(RelativeLayout.RIGHT_OF, 5 * j + i);
                    layout.addRule(RelativeLayout.ALIGN_TOP, 5 * j + i);
                    getChildAt(5 * j + i).setLayoutParams(layout);
                }
            }

            firstBatch();

            OnTouchListener topListen = new OnTouchListener(){
                public boolean onTouch(View v, MotionEvent ev) {

                    if (ev.getAction() == MotionEvent.ACTION_UP)
                    {
                        touchLog.add(new TouchData(System.currentTimeMillis(), ev.getAction(), v.getId() - 1, ((ColoredTextSwitch) v).getText()));
                        return true;
                    }

                    if (!insideCirc(v, ev) && topSelected != -1 && ev.getAction() != MotionEvent.ACTION_DOWN)
                    {
                        return true;
                    }

                    if (ev.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        TouchData toAdd = new TouchData(System.currentTimeMillis(), ev.getAction(), v.getId() - 1, ((ColoredTextSwitch) v).getText());

                        if (curBatch == Batch.FIRST) {
                            touchLog.clear();
                        }
                        touchLog.add(toAdd);
                    }

                    if (bottomSelected != -1)
                    {
                        formatDeselect(bottomSelected);
                        bottomSelected = -1;
                    }

                    if (middleSelected != -1)
                    {
                        formatDeselect(middleSelected);
                        middleSelected = -1;
                    }

                    if (topSelected == v.getId() - 1)
                    {
                        return true;
                    }

                    if (topSelected != -1)
                    {
                        formatDeselect(topSelected);
                    }

                    topSelected = v.getId() - 1;
                    formatSelect(topSelected);

                    return true;
                }

            };

            final OnTouchListener middleListen = new OnTouchListener(){
                public boolean onTouch(View v, MotionEvent ev) {
                    if(topSelected != -1) {


                        if (ev.getAction() == MotionEvent.ACTION_UP)
                        {
                            touchLog.add(new TouchData(System.currentTimeMillis(), ev.getAction(), v.getId() - 1, ((ColoredTextSwitch) v).getText()));
                            return true;
                        }
                        if (ev.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            touchLog.add(new TouchData(System.currentTimeMillis(), ev.getAction(), v.getId() - 1, ((ColoredTextSwitch) v).getText()));
                        }

                        else if (!insideCirc(v, ev) && middleSelected != -1)
                        {
                            return true;
                        }

                        if (bottomSelected != -1)
                        {
                            formatDeselect(bottomSelected);
                            bottomSelected = -1;
                        }

                        if (middleSelected == v.getId() - 1)
                        {
                            return true;
                        }

                        if (middleSelected != -1) {
                            formatDeselect(middleSelected);
                        }


                        middleSelected = v.getId() - 1;
                        formatSelect(middleSelected);
//
//                        if (middleSelected != -1)
//                        {
//                            formatDeselect(middleSelected);
//
//                        }
//
//                        middleSelected = v.getId() - 1;
//                        formatSelect(middleSelected);

                    }

                    return true;
                }
            };

            OnTouchListener bottomListen = new OnTouchListener(){
                public boolean onTouch(View v, MotionEvent ev) {
                    if(middleSelected != -1) {
                        if (ev.getAction() == MotionEvent.ACTION_DOWN || ev.getAction() == MotionEvent.ACTION_UP)
                        {
                            touchLog.add(new TouchData(System.currentTimeMillis(), ev.getAction(), v.getId() - 1, ((ColoredTextSwitch) v).getText()));
                        }

                        if(!insideCirc(v, ev) && bottomSelected != -1 && ev.getAction() != MotionEvent.ACTION_DOWN)
                        {
                            return true;
                        }

                        if (bottomSelected == v.getId() - 1)
                        {
                            return true;
                        }

                        if (bottomSelected != -1) {
                            formatDeselect(bottomSelected);
                        }

                        bottomSelected = v.getId() - 1;
                        formatSelect(bottomSelected);
//                        if (bottomSelected != -1)
//                        {
//                            formatDeselect(bottomSelected);
//
//                        }
//
//
//                        bottomSelected = v.getId() - 1;
//                        formatSelect(bottomSelected);
                    }
                    return true;
                }
            };

            for (int i = 0 ; i < 5 ; i++)
            {
                getChildAt(i).setOnTouchListener(topListen);
            }

            for (int i = 5 ; i < 10 ; i++)
            {
                getChildAt(i).setOnTouchListener(middleListen);
            }

            for (int i = 10 ; i < 15 ; i++)
            {
                getChildAt(i).setOnTouchListener(bottomListen);
            }


        } finally {
            a.recycle();
        }
    }

    public void setTopColor(int color)
    {
        topColor = color;
        topPaint.setColor(color);
        invalidate();
    }

    public void setBottomColor(int color)
    {
        bottomColor = color;
        bottomPaint.setColor(color);
        invalidate();
    }

    public void setTextColor(int color) {
        textColor = color;
        for (int i = 0 ; i < 10 ; i++)
        {
            ((ColoredTextSwitch) getChildAt(i)).setTextColor(textColor);
        }
    }


    public int getTopColor()
    {
        return topColor;
    }

    public int getTextColor()
    {
        return textColor;
    }

    public int getBottomColor()
    {
        return bottomColor;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int xpad = getPaddingRight() + getPaddingLeft();
        int ypad = getPaddingLeft() + getPaddingRight();

        int ww = w - xpad;
        int hh = h - ypad;

        // Calculate the size of each cell.
        int yUnit = hh / 3;
        int xUnit =  ww / 5;

        topRect = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + ww, getPaddingTop() + yUnit);
        middleRect = new RectF(getPaddingLeft(), getPaddingTop() + yUnit, getPaddingLeft() + ww, getPaddingTop() + 2 * yUnit);
        bottomRect = new RectF(getPaddingLeft(), getPaddingTop() + 2 * yUnit, getPaddingLeft() + ww, getPaddingTop() + 3 * yUnit);

        // They are already lain out in relation to each other, only need to fix size.


        for (int i = 0 ; i < 15 ; i++)
        {
            ((LayoutParams) getChildAt(i).getLayoutParams()).height = (yUnit * 3 / 4);
            ((LayoutParams) getChildAt(i).getLayoutParams()).width = xUnit;
            ((LayoutParams) getChildAt(i + 15).getLayoutParams()).height = (yUnit / 4);
            ((LayoutParams) getChildAt(i + 15).getLayoutParams()).width = xUnit;
        }

        // Fix lost pixels.
        if (xUnit * 5 < ww)
        {
            int fixedWidth = ww - xUnit * 4;
            ((LayoutParams) getChildAt(4).getLayoutParams()).width = fixedWidth;
            ((LayoutParams) getChildAt(9).getLayoutParams()).width = fixedWidth;
            ((LayoutParams) getChildAt(14).getLayoutParams()).width = fixedWidth;
            ((LayoutParams) getChildAt(19).getLayoutParams()).width = fixedWidth;
            ((LayoutParams) getChildAt(24).getLayoutParams()).width = fixedWidth;
            ((LayoutParams) getChildAt(29).getLayoutParams()).width = fixedWidth;
        }
    }

    public boolean dispatchTouchEvent(@NonNull MotionEvent ev)
    {
        float x = ev.getX();
        float y = ev.getY();


        boolean retVal = true;
        boolean inside = false;
        for (int i = 0; i < 15; i++) {
            View child = getChildAt(i);
            View label = getChildAt(i + 15);

            if (x > child.getLeft() && x < child.getRight() && y > child.getTop() && y < label.getBottom()) {

                retVal = child.dispatchTouchEvent(ev);
                inside = true;
                break;
            }
        }
        invalidate();

        if (ev.getAction() == MotionEvent.ACTION_UP)
        {
            middleOff = false;
            bottomOff = false;
            if (!inside)
            {
                touchLog.add(new TouchData(System.currentTimeMillis(), ev.getAction(), -1, ""));
            }
            if(topSelected != -1 && middleSelected != -1 && bottomSelected != -1)
            {
                nextBatch();
                return true;
            }
        }
        return retVal;
    }

    private void firstBatch()
    {
        touchLog.clear();
        curBatch = Batch.FIRST;
        if (bottomSelected != -1)
        {
            formatDeselect(bottomSelected);
        }

        if (middleSelected != -1)
        {
            formatDeselect(middleSelected);
        }

        if (topSelected != -1)
        {
            formatDeselect(topSelected);
        }

        topSelected = -1;
        middleSelected = -1;
        bottomSelected = -1;
        for (int i = 0 ; i < 6; i++)
        {
            pass[i] = "";
        }



        //int[] people = res.getIntArray(R.array.people);
        //String[] people_text = res.getStringArray(R.array.people_text);
        String[] people = res.getStringArray(R.array.cartoons);
        String[] people_text = res.getStringArray(R.array.cartoons_text);
        int[] vehicles = res.getIntArray(R.array.vehicles);
        String[] vehicles_text = res.getStringArray(R.array.vehicles_text);
        String[] locations = res.getStringArray(R.array.locations);
        String[] locations_text = res.getStringArray(R.array.locations_text);

        for (int i = 0; i < 5; i++) {
            //((ColoredTextSwitch) getChildAt(i)).setText(new String(Character.toChars(people[i])));
            //((AutoResizeTextView) ((ColoredTextSwitch) getChildAt(i)).getCurrentView()).setTypeface(font);
            ((ColoredTextSwitch) getChildAt(i)).setText(people[i]);
            ((AutoResizeTextView) ((ColoredTextSwitch) getChildAt(i)).getCurrentView()).setTypeface(cartoonFont);
            ((ColoredTextSwitch) getChildAt(i + 5)).setText(new String(Character.toChars(vehicles[i])));
            //((AutoResizeTextView) ((ColoredTextSwitch) getChildAt(i + 10)).getCurrentView()).setTypeface(Typeface.DEFAULT);
            ((ColoredTextSwitch) getChildAt(i + 10)).setText(locations[i]);
            ((AutoResizeTextView) ((ColoredTextSwitch) getChildAt(i + 10)).getCurrentView()).setTypeface(locationsFont);
            ((ColoredTextSwitch) getChildAt(i + 15)).setText(people_text[i]);
            ((ColoredTextSwitch) getChildAt(i + 20)).setText(vehicles_text[i]);
            ((ColoredTextSwitch) getChildAt(i + 25)).setText(locations_text[i]);
        }
//        invalidate();
    }

    private void secondBatch()
    {
        curBatch = Batch.SECOND;
        if (bottomSelected != -1)
        {
            formatDeselect(bottomSelected);
        }

        if (middleSelected != -1)
        {
            formatDeselect(middleSelected);
        }

        if (topSelected != -1)
        {
            formatDeselect(topSelected);
        }
        topSelected = -1;
        middleSelected = -1;
        bottomSelected = -1;
        for (int i = 3 ; i < 6; i++)
        {
            pass[i] = "";
        }

        int[] animals = res.getIntArray(R.array.animals);
        String[] animals_text = res.getStringArray(R.array.animals_text);
        String[] amounts = res.getStringArray(R.array.amounts);
        String[] amounts_text = res.getStringArray(R.array.amounts_text);
        int[] food = res.getIntArray(R.array.food);
        String[] food_text = res.getStringArray(R.array.food_text);

        for (int i = 0; i < 5; i++) {
            ((ColoredTextSwitch) getChildAt(i)).setText(amounts[i]);
            ((AutoResizeTextView) ((ColoredTextSwitch) getChildAt(i)).getCurrentView()).setTypeface(Typeface.DEFAULT);
            ((ColoredTextSwitch) getChildAt(i + 5)).setText(new String(Character.toChars(animals[i])));
            ((ColoredTextSwitch) getChildAt(i + 10)).setText(new String(Character.toChars(food[i])));
            ((AutoResizeTextView) ((ColoredTextSwitch) getChildAt(i + 10)).getCurrentView()).setTypeface(font);
            ((ColoredTextSwitch) getChildAt(i + 15)).setText(amounts_text[i]);
            ((ColoredTextSwitch) getChildAt(i + 20)).setText(animals_text[i]);
            ((ColoredTextSwitch) getChildAt(i + 25)).setText(food_text[i]);
        }
//        invalidate();
    }

    private void nextBatch()
    {
        switch(curBatch)
        {
            case FIRST:
                pass[0] = ((ColoredTextSwitch) getChildAt(topSelected)).getText();
                pass[1] = ((ColoredTextSwitch) getChildAt(middleSelected)).getText();
                pass[2] = ((ColoredTextSwitch) getChildAt(bottomSelected)).getText();
                secondBatch();
                break;
            case SECOND:
                pass[3] = ((ColoredTextSwitch) getChildAt(topSelected)).getText();
                pass[4] = ((ColoredTextSwitch) getChildAt(middleSelected)).getText();
                pass[5] = ((ColoredTextSwitch) getChildAt(bottomSelected)).getText();
                if (listener != null)
                {
                    complete();
                }
                firstBatch();
                break;
        }
    }

    public void reset()
    {
        firstBatch();
    }

    public void back()
    {
        switch(curBatch)
        {
            case FIRST:
                firstBatch();
                break;
            case SECOND:
                for (int i = 3 ; i < 6; i++)
                {
                    pass[i] = "";
                }
                firstBatch();
                break;
        }
    }

    private void formatDeselect(int index)
    {
        ((AutoResizeTextView) ((ColoredTextSwitch) getChildAt(index)).getCurrentView()).partial();
        colorSelect(index, textColor);
        if (index < 10)
        {
            colorSelect(index + 5, textColor);
        }
        if (index % 5 != 0)
        {
            colorSelect(index - 1, textColor);
        }
        if (index % 5 != 4)
        {
            colorSelect(index + 1, textColor);
        }
    }

    private void formatSelect(int index)
    {
        ((AutoResizeTextView) ((ColoredTextSwitch) getChildAt(index)).getCurrentView()).full();
        colorSelect(index, altTextColor);

        if (vibrator != null)
        {
            vibrator.vibrate(20);
        }
    }

    private void colorSelect(int index, int color)
    {
        ((ColoredTextSwitch) getChildAt(index)).setTextColor(color);
        ((ColoredTextSwitch) getChildAt(index + 15)).setTextColor(color);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(topRect, topPaint);
        canvas.drawRect(bottomRect, bottomPaint);
        canvas.drawRect(middleRect, middlePaint);

//        if (middleSelected != -1)
//        {
//            View top = getChildAt(topSelected);
//            View beneath = getChildAt(topSelected + 5);
//            View next = getChildAt(middleSelected);
//
//            canvas.drawLine((top.getLeft() + top.getRight()) / 2, (top.getTop() + top.getBottom()) / 2, (beneath.getLeft() + beneath.getRight()) / 2, (beneath.getTop() + beneath.getBottom()) / 2, linePaint);
//            canvas.drawLine((beneath.getLeft() + beneath.getRight()) / 2, (beneath.getTop() + beneath.getBottom()) / 2, (next.getLeft() + next.getRight()) / 2, (next.getTop() + next.getBottom()) / 2, linePaint);
//        }
//        if (bottomSelected != -1)
//        {
//            View top = getChildAt(middleSelected);
//            View beneath = getChildAt(middleSelected + 5);
//            View next = getChildAt(bottomSelected);
//
//            canvas.drawLine((top.getLeft() + top.getRight()) / 2, (top.getTop() + top.getBottom()) / 2, (beneath.getLeft() + beneath.getRight()) / 2, (beneath.getTop() + beneath.getBottom()) / 2, linePaint);
//            canvas.drawLine((beneath.getLeft() + beneath.getRight()) / 2, (beneath.getTop() + beneath.getBottom()) / 2, (next.getLeft() + next.getRight()) / 2, (next.getTop() + next.getBottom()) / 2, linePaint);
//        }
    }

    public boolean insideCirc(View v, MotionEvent ev)
    {
        int width = v.getWidth();
        int height = v.getHeight() + getChildAt(v.getId() + 14).getHeight();
        int xCenter = (v.getLeft() + v.getRight()) / 2;
        int yCenter = (v.getTop() + getChildAt(v.getId() + 14).getBottom()) / 2;
        float radius = Math.min(height, width) * radiusRatio;

        float xDist = ev.getX() - xCenter;
        float yDist = ev.getY() - yCenter;

        return xDist * xDist + yDist * yDist < radius * radius;

//        int height = v.getHeight() + getChildAt(v.getId() + 14).getHeight();
//        int yCenter = (v.getTop() + getChildAt(v.getId() + 14).getBottom()) / 2;
//
//        return yCenter - ev.getY() < height * 0.2 && ev.getY() - yCenter < height * 0.2;
    }

}