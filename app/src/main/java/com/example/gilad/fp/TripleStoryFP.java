package com.example.gilad.fp;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Gilad on 8/4/2015.
 */
public class TripleStoryFP extends FastPhrase {

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

    private int arrowColor;
    private Paint arrowPaint;
    private float x = 0;
    private float y = 0;
    private float scale;
    private float arrowBaseY;
    private float arrowHeight;
    private float arrowWidth;

    private float borderHeightRatio;
    private float borderWidthRatio;
    private int borderColor;
    private float borderStrokeWidth;
    private Paint borderPaint;
    private Paint edgePaint;

//    private ArrayList<TouchData> touchLog = new ArrayList<TouchData>();

//    private Paint linePaint;

    private static final float radiusRatio = 0.6f;

    public TripleStoryFP(Context context)
    {
        this(context, null);
    }

    public TripleStoryFP(final Context context, AttributeSet attrs) {
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
            arrowColor = a.getColor(R.styleable.StoryFastPhrase_arrowColor, Color.argb(128, 255, 100, 100));
            arrowBaseY = a.getFloat(R.styleable.StoryFastPhrase_arrowBaseY, 50);
            arrowHeight = a.getFloat(R.styleable.StoryFastPhrase_arrowHeight, 30);
            arrowWidth = a.getFloat(R.styleable.StoryFastPhrase_arrowWidth, 40);

            topPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            topPaint.setStyle(Paint.Style.FILL);
            topPaint.setColor(topColor);

            bottomPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bottomPaint.setStyle(Paint.Style.FILL);
            bottomPaint.setColor(bottomColor);

            middlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            middlePaint.setStyle(Paint.Style.FILL);
            middlePaint.setColor(middleColor);

            arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            arrowPaint.setStyle(Paint.Style.FILL);
            arrowPaint.setColor(arrowColor);

            borderHeightRatio = 0.15f;
            borderWidthRatio = 0.15f;
            borderStrokeWidth = 2.5f;
            borderColor = Color.WHITE;

            borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            borderPaint.setColor(borderColor);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(borderStrokeWidth);

            edgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            edgePaint.setColor(borderColor);
            edgePaint.setStyle(Paint.Style.STROKE);
            edgePaint.setStrokeWidth(borderStrokeWidth * 2);


//            linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//            linePaint.setColor(altTextColor);
//            linePaint.setStyle(Paint.Style.STROKE);
//            linePaint.setStrokeWidth(2);

            res = getResources();
            scale = res.getDisplayMetrics().density;
            font = Typeface.createFromAsset(res.getAssets(), "fonts/androidemoji.ttf");
            cartoonFont = Typeface.createFromAsset(res.getAssets(), "fonts/cartoon.ttf"); //Cartoons
            locationsFont = Typeface.createFromAsset(res.getAssets(), "fonts/FamousBuildings.ttf");
            vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);

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

                    boolean hit = hitBottomBorders(v, ev);

                    if (hit)
                    {
                        if (!middleOff && vibrator != null)
                        {
                            vibrator.vibrate(200);
                        }
                        middleOff = true;
                        return true;
                    }

                    else if (middleOff)
                    {
                        middleOff = false;
                        if (v.getId() - 1 == topSelected)
                        {
                            vibrator.vibrate(30);
                        }
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
                    if(topSelected != -1 && !middleOff) {


                        if (ev.getAction() == MotionEvent.ACTION_UP)
                        {
                            touchLog.add(new TouchData(System.currentTimeMillis(), ev.getAction(), v.getId() - 1, ((ColoredTextSwitch) v).getText()));
                            return true;
                        }
                        if (ev.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            touchLog.add(new TouchData(System.currentTimeMillis(), ev.getAction(), v.getId() - 1, ((ColoredTextSwitch) v).getText()));
                        }

                        boolean hit = hitBottomBorders(v, ev);

                        if (hit)
                        {
                            if (!bottomOff && vibrator != null)
                            {
                                vibrator.vibrate(200);
                            }
                            bottomOff = true;
                            return true;
                        }

                        else if (bottomOff){
                            bottomOff = false;
                            if (v.getId() - 1 == middleSelected)
                            {
                                vibrator.vibrate(30);
                            }
                        }

                        if(hitTopBorders(v, ev))
                        {
                            if (!middleOff && middleSelected == -1)
                            {
                                middleOff = true;
                                if (vibrator != null)
                                {
                                    vibrator.vibrate(200);
                                }
                            }
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
                    if(middleSelected != -1 && !bottomOff) {
                        if (ev.getAction() == MotionEvent.ACTION_DOWN || ev.getAction() == MotionEvent.ACTION_UP)
                        {
                            touchLog.add(new TouchData(System.currentTimeMillis(), ev.getAction(), v.getId() - 1, ((ColoredTextSwitch) v).getText()));
                        }

                        if(hitTopBorders(v, ev))
                        {
                            if (!bottomOff && bottomSelected == -1) {
                                bottomOff = true;
                                if (vibrator != null) {
                                    vibrator.vibrate(200);
                                }
                            }
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

    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        x = ev.getX();
        y = ev.getY();


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

        if (vibrator != null && !middleOff && !bottomOff)
        {
            vibrator.vibrate(30);
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

        int heightUnit = (getHeight() - getPaddingTop() - getPaddingBottom()) / 3;
        int widthUnit = (getWidth() - getPaddingLeft() - getPaddingRight()) / 5;

        int yOffset = (int) (heightUnit * borderHeightRatio);
        int xOffset = (int) (widthUnit * borderWidthRatio);
        int left = getLeft() + getPaddingLeft();
        int right = getRight() - getPaddingRight();
        int top = getTop() + getPaddingTop();
        int bottom = getBottom() - getPaddingBottom();

        for (int i = 1; i < 3; i++)
        {
            for (int j = 1; j < 5; j++)
            {
                int midX = left + widthUnit * j;
                int midY = top + heightUnit * i;
                canvas.drawLine(midX + xOffset, midY, midX - xOffset, midY, borderPaint);
                canvas.drawLine(midX, midY + yOffset, midX, midY - yOffset, borderPaint);
            }
        }


        for (int i = 1; i < 5; i++)
        {
            int midX = left + widthUnit * i;

            canvas.drawLine(midX + xOffset, top, midX - xOffset, top, edgePaint);
            canvas.drawLine(midX, top, midX, top + yOffset, borderPaint);
            canvas.drawLine(midX + xOffset, bottom, midX - xOffset, bottom, edgePaint);
            canvas.drawLine(midX, bottom, midX, bottom - yOffset, borderPaint);
        }


        for (int i = 1; i < 3; i++)
        {
            int midY = left + heightUnit * i;

            canvas.drawLine(left, midY + yOffset, left, midY - yOffset, edgePaint);
            canvas.drawLine(left, midY, left + xOffset, midY, borderPaint);
            canvas.drawLine(right, midY + yOffset, right, midY - yOffset, edgePaint);
            canvas.drawLine(right, midY, right - xOffset, midY, borderPaint);
        }

        canvas.drawLine(left, top, left + xOffset, top, edgePaint);
        canvas.drawLine(left, top, left, top + yOffset, edgePaint);

        canvas.drawLine(right, top, right - xOffset, top, edgePaint);
        canvas.drawLine(right, top, right, top + yOffset, edgePaint);

        canvas.drawLine(left, bottom, left + xOffset, bottom, edgePaint);
        canvas.drawLine(left, bottom, left, bottom - yOffset, edgePaint);

        canvas.drawLine(right, bottom, right - xOffset, bottom, edgePaint);
        canvas.drawLine(right, bottom, right, bottom - yOffset, edgePaint);


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

    private boolean hitBottomBorders(View v, MotionEvent ev)
    {
        View label = getChildAt(v.getId() + 14);
        float x = ev.getX();
        float y = ev.getY();
        float yDist = (v.getHeight() + label.getHeight()) * borderHeightRatio;
        float xDist = v.getWidth()* borderWidthRatio;

        if((x - v.getLeft() < xDist && label.getBottom() - y < yDist))
        {
            float bottomX = v.getLeft();
            float bottomY = label.getBottom();
            float topX = bottomX;
            float topY = bottomY - yDist;
            float sideX = bottomX + xDist;
            float sideY = bottomY;

            float[] pts = new float[6];
            boolean[] bools = new boolean[3];

            pts[0] = bottomX;
            pts[1] = topX;
            pts[2] = sideX;
            pts[3] = bottomY;
            pts[4] = topY;
            pts[5] = sideY;

            for (int i = 0; i < 3; i++)
            {
                int i1 = i;
                int i2 = (i + 1) % 3;
                bools[i] = (x - pts[i2]) * (pts[i1 + 3] - pts[i2 + 3]) - (pts[i1] - pts[i2]) * (y - pts[i2] + 3) < 0;
            }
            return (bools[0] != bools[1] || bools [1] != bools[2]);
        }

        else if (v.getRight() - x < xDist && label.getBottom() - y < yDist)
        {
            float bottomX = v.getRight();
            float bottomY = label.getBottom();
            float topX = bottomX;
            float topY = bottomY - yDist;
            float sideX = bottomX - xDist;
            float sideY = bottomY;

            float[] pts = new float[6];
            boolean[] bools = new boolean[3];

            pts[0] = bottomX;
            pts[1] = topX;
            pts[2] = sideX;
            pts[3] = bottomY;
            pts[4] = topY;
            pts[5] = sideY;

            for (int i = 0; i < 3; i++)
            {
                int i1 = i;
                int i2 = (i + 1) % 3;
                bools[i] = (x - pts[i2]) * (pts[i1 + 3] - pts[i2 + 3]) - (pts[i1] - pts[i2]) * (y - pts[i2] + 3) < 0;
            }
            return (bools[0] != bools[1] || bools [1] != bools[2]);
        }

        else
        {
            return false;
        }
    }

    private boolean hitTopBorders(View v, MotionEvent ev)
    {
        View label = getChildAt(v.getId() + 14);
        float x = ev.getX();
        float y = ev.getY();
        float yDist = (v.getHeight() + label.getHeight()) * borderHeightRatio;
        float xDist = v.getWidth()* borderWidthRatio;

        if(x - v.getLeft() < xDist && y - v.getTop() < yDist)
        {
            float topX = v.getLeft();
            float topY = v.getTop();
            float bottomX = topX;
            float bottomY = topY + yDist;
            float sideX = topX + xDist;
            float sideY = topY;

            float[] pts = new float[6];
            boolean[] bools = new boolean[3];

            pts[0] = bottomX;
            pts[1] = topX;
            pts[2] = sideX;
            pts[3] = bottomY;
            pts[4] = topY;
            pts[5] = sideY;

            for (int i = 0; i < 3; i++)
            {
                int i1 = i;
                int i2 = (i + 1) % 3;
                bools[i] = (x - pts[i2]) * (pts[i1 + 3] - pts[i2 + 3]) - (pts[i1] - pts[i2]) * (y - pts[i2] + 3) < 0;
            }
            return (bools[0] != bools[1] || bools [1] != bools[2]);
        }

        else if (v.getRight() - x < xDist && y - v.getTop() < yDist)
        {
            float topX = v.getRight();
            float topY = v.getTop();
            float bottomX = topX;
            float bottomY = topY + yDist;
            float sideX = topX - xDist;
            float sideY = topY;

            float[] pts = new float[6];
            boolean[] bools = new boolean[3];

            pts[0] = bottomX;
            pts[1] = topX;
            pts[2] = sideX;
            pts[3] = bottomY;
            pts[4] = topY;
            pts[5] = sideY;

            for (int i = 0; i < 3; i++)
            {
                int i1 = i;
                int i2 = (i + 1) % 3;
                bools[i] = (x - pts[i2]) * (pts[i1 + 3] - pts[i2 + 3]) - (pts[i1] - pts[i2]) * (y - pts[i2] + 3) < 0;
            }
            return (bools[0] != bools[1] || bools [1] != bools[2]);
        }

        else
        {
            return false;
        }
    }

    private class TriangleDrawer extends RelativeLayout
    {
        float x = 0;
        float y = 0;
        boolean drawing = false;

        public TriangleDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }


        public void at(float x, float y)
        {
            this.x = x;
            this.y = y;
            drawing = true;
            invalidate();
        }

        public void off()
        {
            drawing = false;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (drawing)
            {
                Path arrow = new Path();
                float height = (arrowBaseY + arrowHeight) * scale + 0.5f;
                float base = arrowBaseY * scale + 0.5f;
                float xOffset = arrowWidth / 2f * scale + 0.5f;
                arrow.moveTo(x, y - height);
                arrow.lineTo(x + xOffset, y - base);
                arrow.lineTo(x - xOffset, y - base);
                arrow.lineTo(x, y - height);
                canvas.drawPath(arrow, arrowPaint);
            }
        }
    }
}