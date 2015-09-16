package com.example.gilad.fp;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;


/**
 * Created by Gilad on 8/4/2015.
 */
public class DoubleStoryFP extends RelativeLayout {

    private int topColor;
    private int bottomColor;
    private int textColor;
    private String[] pass = new String[6];
    private String topSelected = "";
    private String bottomSelected = "";
    private enum Batch {FIRST, SECOND, THIRD}
    private Batch curBatch;
    private Resources res;
    private boolean drawCirc = false;
    private float circX;
    private float circY;
    private Paint circPaint;
    private int circColor;
    private int circRad;
    private RectF topRect;
    private Paint topPaint;
    private RectF bottomRect;
    private Paint bottomPaint;
    private float scale;
    private int selected = -1;
//    private Handler timerHandler = new Handler();
//    private Runnable timerFunc = new Runnable() {
//        @Override
//        public void run() {
//            if (selected != -1) {
//                if (selected < 5) {
//                    topSelected = ((ColoredTextSwitch) getChildAt(selected)).getText();
//                } else {
//                    bottomSelected = ((ColoredTextSwitch) getChildAt(selected)).getText();
//                    nextBatch();
//                }
//                circPaint.setColor(altCircColor);
//                selected = -1;
//                invalidate();
//            }
//
//        }
//    };
//    static int time = 300;


    public interface OnCompleteListener
    {
        public void onComplete(String[] pass);
    }
    private OnCompleteListener listen;

    public DoubleStoryFP(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ListFastPhrase,
                0, 0);
        try {
            topColor = a.getColor(R.styleable.FastPhrase_topColor, Color.GRAY);
            bottomColor = a.getColor(R.styleable.FastPhrase_bottomColor, Color.DKGRAY);
            textColor = a.getColor(R.styleable.FastPhrase_textColor, Color.WHITE);
            circColor = a.getColor(R.styleable.ListFastPhrase_circColor, Color.argb(128, 255, 255, 0));
            circRad = a.getInt(R.styleable.ListFastPhrase_circRad, 30);

            topPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            topPaint.setStyle(Paint.Style.FILL);
            topPaint.setColor(topColor);

            bottomPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bottomPaint.setStyle(Paint.Style.FILL);
            bottomPaint.setColor(bottomColor);

            circPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            circPaint.setColor(circColor);
            circPaint.setStyle(Paint.Style.FILL);

            res = getResources();
            Typeface font = Typeface.createFromAsset(res.getAssets(), "fonts/androidemoji.ttf");

            scale = res.getDisplayMetrics().density;

            setWillNotDraw(false);
            for (int i = 0; i < 10 ; i++)
            {
                ColoredTextSwitch child = new ColoredTextSwitch(context, textColor, font);
                child.setId(i + 1);
                addView(child);
            }

            for (int i = 10 ; i < 20 ; i++)
            {
                ColoredTextSwitch child = new ColoredTextSwitch(context, textColor);
                child.setId(i + 1);
                addView(child);
            }

            // Manually laying out the views in relation to each other.
            // First item of first row on top.
            LayoutParams layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(0).setLayoutParams(layout);

            // First item of last row on bottom.
            layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.BELOW, 1);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(10).setLayoutParams(layout);

            layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.BELOW, 11);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(5).setLayoutParams(layout);

            layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.BELOW, 6);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(15).setLayoutParams(layout);

            // Align rows according to first item.
            for (int j = 0 ; j < 4 ; j ++)
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
                    if (ev.getAction() == MotionEvent.ACTION_DOWN || (ev.getAction() == MotionEvent.ACTION_MOVE && topSelected.equals(""))) {

                        drawCirc = true;
                        selected = v.getId() - 1;
//                        if (ev.getAction() == MotionEvent.ACTION_MOVE && selected != v.getId() - 1)
//                        {
//                            selected = v.getId() - 1;
//                            timerHandler.postDelayed(timerFunc, time);
//                        }
//                        else
//                        {
                        topSelected = ((ColoredTextSwitch) v).getText();
//                        }
                    }
                    else if (selected != v.getId() - 1)
                    {
                        selected = -1;
                    }
                    return true;
                }

            };

            OnTouchListener bottomListen = new OnTouchListener(){
                public boolean onTouch(View v, MotionEvent ev) {
                    if(!topSelected.equals("")) {
//                        if (ev.getAction() == MotionEvent.ACTION_DOWN)
//                        {
//                            drawCirc = true;
//                            selected = v.getId() - 1;
//                            timerHandler.postDelayed(timerFunc, time);
//                        }
                        if (ev.getAction() == MotionEvent.ACTION_UP)
                        {
                            bottomSelected = ((ColoredTextSwitch) v).getText();
                            nextBatch();
                        }
//                        else if (selected != v.getId() - 1)
//                        {
//                            selected = v.getId() - 1;
//                            timerHandler.postDelayed(timerFunc, time);
//                        }
                        else
                        {
                            drawCirc = true;
                            selected = v.getId() - 1;
                        }

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
        int yUnit = hh /2;
        int xUnit =  ww / 5;

        topRect = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + ww, getPaddingTop() + yUnit);
        bottomRect = new RectF(getPaddingLeft(), getPaddingTop() + yUnit, getPaddingLeft() + ww, getPaddingTop() + hh);

        // They are already lain out in relation to each other, only need to fix size.


        for (int i = 0 ; i < 10 ; i++)
        {
            ((LayoutParams) getChildAt(i).getLayoutParams()).height = (yUnit * 4 / 5);
            ((LayoutParams) getChildAt(i).getLayoutParams()).width = xUnit;
            ((LayoutParams) getChildAt(i + 10).getLayoutParams()).height = (yUnit / 5);
            ((LayoutParams) getChildAt(i + 10).getLayoutParams()).width = xUnit;
        }

        // Fix lost pixels.
        if (xUnit * 5 < ww)
        {
            int fixedWidth = ww - xUnit * 4;
            ((LayoutParams) getChildAt(4).getLayoutParams()).width = fixedWidth;
            ((LayoutParams) getChildAt(9).getLayoutParams()).width = fixedWidth;
            ((LayoutParams) getChildAt(14).getLayoutParams()).width = fixedWidth;
            ((LayoutParams) getChildAt(19).getLayoutParams()).width = fixedWidth;
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        float x = ev.getX();
        float y = ev.getY();
        circX = x;
        circY = y;
        int prevSelected = selected;
        if (ev.getAction() == MotionEvent.ACTION_UP)
        {
//            selected = -1;
//            timerHandler.removeCallbacks(timerFunc);
            drawCirc = false;
            selected = -1;
        }

        boolean retVal = true;
//        boolean outside = false;

            for (int i = 0; i < 10; i++) {
                View child = getChildAt(i);
                View label = getChildAt(i + 10);
                float midX = (child.getLeft() + child.getRight()) / 2.0f;
                float midY = (child.getTop() + label.getBottom()) / 2.0f;

                if (x > child.getLeft() && x < child.getRight() && y > child.getTop() && y < label.getBottom()) {

                    if ((x < midX && 0.25 * (midX - x) > x - child.getLeft()) || (x > midX && 0.25 * (x - midX) > child.getRight() - x)) {
//                        outside = true;
                        selected = -1;
                        break;
                    }

                    if ((y < midY && 0.25 * (midY - y) > y - child.getTop()) || (y > midY && 0.25 * (y - midY) > label.getBottom() - ev.getY())) {
//                        outside = true;
                        selected = -1;
                        break;
                    }
                    retVal = child.dispatchTouchEvent(ev);
                    break;
                }
            }

        if (prevSelected != selected)
        {
            if (prevSelected != -1)
            {
                ((AutoResizeTextView) ((ColoredTextSwitch) getChildAt(prevSelected)).getCurrentView()).partial();
            }
            if (selected != -1)
            {
                ((AutoResizeTextView) ((ColoredTextSwitch) getChildAt(selected)).getCurrentView()).full();
            }

            //performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }

//        if (outside)
//        {
//            circPaint.setColor(altCircColor);
//            selected = -1;
//            timerHandler.removeCallbacks(timerFunc);
//        }

        invalidate();
        return retVal;
    }

    private void firstBatch()
    {
        curBatch = Batch.FIRST;
        topSelected = "";
        bottomSelected = "";
        for (int i = 0 ; i < 6 ; i++)
        {
            pass[i] = "";
        }

        int[] people = res.getIntArray(R.array.people);
        String[] people_text = res.getStringArray(R.array.people_text);
        int[] sports = res.getIntArray(R.array.sports);
        String[] sports_text = res.getStringArray(R.array.sports_text);

        for (int i = 0; i < 5; i++) {
            ((ColoredTextSwitch) getChildAt(i)).setText(new String(Character.toChars(people[i])));
            ((ColoredTextSwitch) getChildAt(i + 10)).setText(people_text[i]);
            ((ColoredTextSwitch) getChildAt(i + 5)).setText(new String(Character.toChars(sports[i])));
            ((ColoredTextSwitch) getChildAt(i + 15)).setText(sports_text[i]);
        }
    }

    private void secondBatch()
    {
        curBatch = Batch.SECOND;
        topSelected = "";
        bottomSelected = "";
        pass[2] = "";
        pass[3] = "";

        int[] vehicles = res.getIntArray(R.array.vehicles);
        String[] vehicles_text = res.getStringArray(R.array.vehicles_text);
        String[] locations_text = res.getStringArray(R.array.locations_text);

        for (int i = 0; i < 5; i++) {
            ((ColoredTextSwitch) getChildAt(i)).setText(new String(Character.toChars(vehicles[i])));
            ((ColoredTextSwitch) getChildAt(i + 10)).setText(vehicles_text[i]);
            ((ColoredTextSwitch) getChildAt(i + 5)).setText(locations_text[i]);
            ((ColoredTextSwitch) getChildAt(i + 15)).setText(locations_text[i]);
        }
    }

    private void thirdBatch()
    {
        curBatch = Batch.THIRD;
        topSelected = "";
        bottomSelected = "";
        pass[4] = "";
        pass[5] = "";

        int[] animals = res.getIntArray(R.array.animals);
        String[] animals_text = res.getStringArray(R.array.animals_text);
        int[] food = res.getIntArray(R.array.food);
        String[] food_text = res.getStringArray(R.array.food_text);

        for (int i = 0; i < 5; i++) {
            ((ColoredTextSwitch) getChildAt(i)).setText(new String(Character.toChars(animals[i])));
            ((ColoredTextSwitch) getChildAt(i + 10)).setText(animals_text[i]);
            ((ColoredTextSwitch) getChildAt(i + 5)).setText(new String(Character.toChars(food[i])));
            ((ColoredTextSwitch) getChildAt(i + 15)).setText(food_text[i]);
        }
    }

    private void nextBatch()
    {
        switch(curBatch)
        {
            case FIRST:
                pass[0] = topSelected;
                pass[1] = bottomSelected;
                secondBatch();
                break;
            case SECOND:
                pass[2] = topSelected;
                pass[3] = bottomSelected;
                thirdBatch();
                break;
            case THIRD:
                pass[4] = topSelected;
                pass[5] = bottomSelected;
                if (listen != null)
                {
                    listen.onComplete(pass);
                }
                for (int i = 0 ; i < 6 ; i++)
                {
                    pass[i] = "";
                }
                firstBatch();
                break;
        }
    }

    public void reset()
    {
        firstBatch();
        selected = -1;
    }

    public void back()
    {
        switch(curBatch)
        {
            case FIRST:
                firstBatch();
                break;
            case SECOND:
                pass[2] = "";
                pass[3] = "";
                firstBatch();
                break;
            case THIRD:
                pass[4] = "";
                pass[5] = "";
                secondBatch();
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(topRect, topPaint);
        canvas.drawRect(bottomRect, bottomPaint);
        if (drawCirc)
        {
            canvas.drawCircle(circX, circY, circRad * scale + 0.5f, circPaint);
        }
    }

    public void setOnCompleteListener (OnCompleteListener listener)
    {
        this.listen = listener;
    }

//    public void stopClock()
//    {
//        timerHandler.removeCallbacks(timerFunc);
//    }

    private class ColoredTextSwitch extends TextSwitcher {

        private int textColor;

        public ColoredTextSwitch(Context context) {
            super(context);
        }

        private ColoredTextSwitch(Context context, final int textColor)
        {
            super(context);
            this.textColor = textColor;

            setFactory(new ViewFactory() {
                @Override
                public View makeView() {
                    AutoResizeTextView retVal = new AutoResizeTextView(getContext());
                    retVal.setTextColor(textColor);
                    retVal.setGravity(Gravity.CENTER);
                    retVal.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    return retVal;
                }
            });

            setInAnimation(context, R.anim.abc_fade_in);
            setOutAnimation(context, R.anim.abc_fade_out);

            setWillNotDraw(false);
        }

        private ColoredTextSwitch(Context context, final int textColor, final Typeface font)
        {
            super(context);
            this.textColor = textColor;

            setFactory(new ViewFactory() {
                @Override
                public View makeView() {
                    AutoResizeTextView retVal = new AutoResizeTextView(getContext());
                    retVal.setTextColor(textColor);
                    retVal.setTypeface(font);
                    retVal.setGravity(Gravity.CENTER);
                    retVal.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    return retVal;
                }
            });

            setInAnimation(context, R.anim.abc_fade_in);
            setOutAnimation(context, R.anim.abc_fade_out);

            setWillNotDraw(false);
        }

        public void setTextColor(int color)
        {
            textColor = color;
            ((TextView) getCurrentView()).setTextColor(color);
            ((TextView) getNextView()).setTextColor(color);
        }

        public int getTextColor()
        {
            return textColor;
        }

        public String getText()
        {
            return ((TextView) getCurrentView()).getText().toString();
        }

        @Override
        public void setText(CharSequence text) {
            super.setText(text);
            ((AutoResizeTextView) getCurrentView()).setTextSize(10);
        }
    }
}