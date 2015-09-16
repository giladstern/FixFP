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
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;


/**
 * Created by Gilad on 8/4/2015.
 */
public class NoLinesFP extends RelativeLayout {

    private int topColor;
    private int bottomColor;
    private int textColor;
    private String pass[] = new String[6];
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

    public interface OnCompleteListener
    {
        public void onComplete(String pass[]);
    }
    private OnCompleteListener listen;

    public NoLinesFP(Context context, AttributeSet attrs) {
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
            for (int i = 0; i < 4 ; i++)
            {
                ColoredTextSwitch child = new ColoredTextSwitch(context, textColor);
                child.setId(i + 1);
                addView(child);
            }

            for (int i = 4 ; i < 10 ; i++)
            {
                ColoredTextSwitch child = new ColoredTextSwitch(context, textColor, font);
                child.setId(i + 1);
                addView(child);
            }

            for (int i = 10; i < 16 ; i++)
            {
                ColoredTextSwitch child = new ColoredTextSwitch(context, textColor);
                child.setId(i + 1);
                addView(child);
            }

            // Manually laying out the views in relation to each other.
            // First item of first row on top.
            RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(0).setLayoutParams(layout);

            // First item of last row on bottom.
            layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.BELOW, 1);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(4).setLayoutParams(layout);

            layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.BELOW, 5);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(10).setLayoutParams(layout);

            // First item of row above bottom row.
            layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.BELOW, 11);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(7).setLayoutParams(layout);

            layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.BELOW, 8);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(13).setLayoutParams(layout);

            // Align rows according to first item.
            for (int i = 1 ; i < 4; i++)
            {
                layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layout.addRule(RelativeLayout.RIGHT_OF, i);
                layout.addRule(RelativeLayout.ALIGN_TOP, i);
                getChildAt(i).setLayoutParams(layout);
            }

            for (int i = 5 ; i < 7; i++)
            {
                layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layout.addRule(RelativeLayout.RIGHT_OF, i);
                layout.addRule(RelativeLayout.ALIGN_TOP, i);
                getChildAt(i).setLayoutParams(layout);

                layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }

            for (int i = 8 ; i < 10; i++)
            {
                layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layout.addRule(RelativeLayout.RIGHT_OF, i);
                layout.addRule(RelativeLayout.ALIGN_TOP, i);
                getChildAt(i).setLayoutParams(layout);
            }


            String[] numbers = res.getStringArray(R.array.numbers);

            for (int i = 0 ; i < 4 ; i++) {
                ((ColoredTextSwitch) getChildAt(i)).setText(numbers[i]);
            }

            for (int i = 11 ; i < 13; i++)
            {
                layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layout.addRule(RelativeLayout.RIGHT_OF, i);
                layout.addRule(RelativeLayout.ALIGN_TOP, i);
                getChildAt(i).setLayoutParams(layout);
            }

            for (int i = 14 ; i < 16; i++)
            {
                layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layout.addRule(RelativeLayout.RIGHT_OF, i);
                layout.addRule(RelativeLayout.ALIGN_TOP, i);
                getChildAt(i).setLayoutParams(layout);
            }

            firstBatch();

            OnTouchListener topListen = new OnTouchListener(){
                public boolean onTouch(View v, MotionEvent ev) {
                    if (ev.getAction() == MotionEvent.ACTION_DOWN || (ev.getAction() == MotionEvent.ACTION_MOVE && topSelected.equals(""))) {
                        float midX = (v.getLeft() + v.getRight())/2.0f;
                        float midY = (v.getTop() + v.getBottom())/2.0f;
                        if ((ev.getX() < midX && 0.25* (midX - ev.getX()) > ev.getX() - v.getLeft()) || (ev.getX() > midX && 0.25 * (ev.getX() - midX) > v.getRight() - ev.getX()))
                        {
                            drawCirc = false;
                            return true;
                        }

                        if ((ev.getY() < midY && 0.25* (midY - ev.getY()) > ev.getY() - v.getTop()) || (ev.getY() > midY && 0.25 * (ev.getY() - midY) > v.getBottom() - ev.getY()))
                        {
                            drawCirc = false;
                            return true;
                        }
                        drawCirc = true;
                        topSelected = ((ColoredTextSwitch) v).getText();
                    }
                    return true;
                }

            };

            OnTouchListener bottomListen = new OnTouchListener(){
                public boolean onTouch(View v, MotionEvent ev) {
                    if(!topSelected.equals("")) {
                        if (ev.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            drawCirc = true;
                        }
                        if (ev.getAction() == MotionEvent.ACTION_UP)
                        {
                            bottomSelected = ((ColoredTextSwitch) v).getText();
                            nextBatch();
                        }
                    }
                    return true;
                }
            };

            for (int i = 0 ; i < 4 ; i++)
            {
                getChildAt(i).setOnTouchListener(topListen);
            }

            for (int i = 4 ; i < 10 ; i++)
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
        int topX = ww / 4;
        int bottomX = ww / 3;
        int yUnit =  hh / 3;

        topRect = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + ww, getPaddingTop() + yUnit);
        bottomRect = new RectF(getPaddingLeft(), getPaddingTop() + yUnit, getPaddingLeft() + ww, getPaddingTop() + hh);

        // The are already lain out in relation to each other, only need to fix size.
        for (int i = 0 ; i < 4 ; i++)
        {
            ((RelativeLayout.LayoutParams) getChildAt(i).getLayoutParams()).height = yUnit;
            ((RelativeLayout.LayoutParams) getChildAt(i).getLayoutParams()).width = topX;
        }

        // Fix lost pixels.
        if (topX * 4 < ww)
        {
            ((RelativeLayout.LayoutParams) getChildAt(3).getLayoutParams()).width = ww - topX * 3;
        }

        for (int i = 4 ; i < 10 ; i++)
        {
            ((RelativeLayout.LayoutParams) getChildAt(i).getLayoutParams()).height = (int) (yUnit * 0.80);
            ((RelativeLayout.LayoutParams) getChildAt(i).getLayoutParams()).width = bottomX;
            ((RelativeLayout.LayoutParams) getChildAt(i + 6).getLayoutParams()).height = yUnit / 5;
            ((RelativeLayout.LayoutParams) getChildAt(i + 6).getLayoutParams()).width = bottomX;
        }

        // Fix lost pixels.
        if (bottomX * 3 < ww)
        {
            ((RelativeLayout.LayoutParams) getChildAt(6).getLayoutParams()).width = ww - bottomX * 2;
            ((RelativeLayout.LayoutParams) getChildAt(9).getLayoutParams()).width = ww - bottomX * 2;
            ((RelativeLayout.LayoutParams) getChildAt(12).getLayoutParams()).width = ww - bottomX * 2;
            ((RelativeLayout.LayoutParams) getChildAt(15).getLayoutParams()).width = ww - bottomX * 2;
        }


    }

    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        float x = ev.getX();
        float y = ev.getY();
        circX = x;
        circY = y;
        if (ev.getAction() == MotionEvent.ACTION_UP)
        {
            drawCirc = false;
        }

        else if (ev.getAction() != MotionEvent.ACTION_DOWN && !topSelected.equals(""))
        {
            invalidate();
            return true;
        }

        boolean retVal = true;
        boolean dispatched = false;
        for (int i = 0; i < 4; i++) {
            View child = getChildAt(i);
            if (x > child.getLeft() && x < child.getRight() && y > child.getTop() && y < child.getBottom()) {
                retVal = child.dispatchTouchEvent(ev);
                dispatched = true;
                break;
            }
        }
        if (!dispatched) {
            for (int i = 4; i < 10; i++) {
                View child = getChildAt(i);
                View label = getChildAt(i + 6);
                float midX = (child.getLeft() + child.getRight()) / 2.0f;
                float midY = (child.getTop() + label.getBottom()) / 2.0f;

                if (x > child.getLeft() && x < child.getRight() && y > child.getTop() && y < label.getBottom()) {
                    if ((x < midX && 0.25 * (midX - x) > x - child.getLeft()) || (x > midX && 0.25 * (x - midX) > child.getRight() - x)) {
                        break;
                    }

                    if ((y < midY && 0.25 * (midY - y) > y - child.getTop()) || (y > midY && 0.25 * (y - midY) > label.getBottom() - ev.getY())) {
                        break;
                    }
                    retVal = child.dispatchTouchEvent(ev);
                    break;
                }
            }
        }

        invalidate();
        return retVal;
    }

    private void firstBatch()
    {
        curBatch = Batch.FIRST;
        topSelected = "";
        bottomSelected = "";
        pass = new String[6];

        int[] vehicles = res.getIntArray(R.array.vehicles);
        String[] text = res.getStringArray(R.array.vehicles_text);

        for (int i = 0; i < 6; i++)
        {
            ((ColoredTextSwitch) getChildAt(i + 4)).setText(new String(Character.toChars(vehicles[i])));
            ((ColoredTextSwitch) getChildAt(i + 10)).setText(text[i]);
        }
    }

    private void secondBatch()
    {
        curBatch = Batch.SECOND;
        topSelected = "";
        bottomSelected = "";
        for (int i = 2; i < 6; i++)
        {
            pass[i] = "";
        }

        int[] animals = res.getIntArray(R.array.animals);
        String[] text = res.getStringArray(R.array.animals_text);

        for (int i = 0; i < 6; i++)
        {
            ((ColoredTextSwitch) getChildAt(i + 4)).setText(new String(Character.toChars(animals[i])));
            ((ColoredTextSwitch) getChildAt(i + 10)).setText(text[i]);
        }
    }

    private void thirdBatch()
    {
        curBatch = Batch.THIRD;
        topSelected = "";
        bottomSelected = "";
        for (int i = 4; i < 6; i++)
        {
            pass[i] = "";
        }

        int[] clothes = res.getIntArray(R.array.clothes);
        String[] text = res.getStringArray(R.array.clothes_text);

        for (int i = 0; i < 6; i++)
        {
            ((ColoredTextSwitch) getChildAt(i + 4)).setText(new String(Character.toChars(clothes[i])));
            ((ColoredTextSwitch) getChildAt(i + 10)).setText(text[i]);
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
                    retVal.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
                    retVal.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }
    }
}