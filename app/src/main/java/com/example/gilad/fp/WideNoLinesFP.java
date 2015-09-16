package com.example.gilad.fp;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Gilad on 8/4/2015.
 */
public class WideNoLinesFP extends FastPhrase {

    private int topColor;
    private int bottomColor;
    private int textColor;
//    private String pass[] = new String[6];
    private String topSelected = "";
    private String bottomSelected = "";
    public enum Batch {FIRST, SECOND, THIRD}
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
    private boolean off = true;
    private Vibrator vibrator;
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

    public interface OnFirstListener
    {
        public void onFirst();
    }
    public interface OnSecondListener
    {
        public void onSecond();
    }
    public interface OnThirdListener
    {
        public void onThird();
    }

    private OnFirstListener firstListener;
    private OnSecondListener secondListener;
    private OnThirdListener thirdListener;

    public WideNoLinesFP(Context context, AttributeSet attrs) {
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

            vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);

            setWillNotDraw(false);
            for (int i = 0; i < 5 ; i++)
            {
                ColoredTextSwitch child = new ColoredTextSwitch(context, textColor);
                child.setId(i + 1);
                addView(child);
            }

            for (int i = 5 ; i < 10 ; i++)
            {
                ColoredTextSwitch child = new ColoredTextSwitch(context, textColor, font);
                child.setId(i + 1);
                addView(child);
            }

            for (int i = 10; i < 15 ; i++)
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
            getChildAt(5).setLayoutParams(layout);

            layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.BELOW, 6);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(10).setLayoutParams(layout);

            // Align rows according to first item.
            for (int i = 1 ; i < 5; i++)
            {
                layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layout.addRule(RelativeLayout.RIGHT_OF, i);
                layout.addRule(RelativeLayout.ALIGN_TOP, i);
                getChildAt(i).setLayoutParams(layout);
            }

            for (int i = 6 ; i < 10; i++)
            {
                layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layout.addRule(RelativeLayout.RIGHT_OF, i);
                layout.addRule(RelativeLayout.ALIGN_TOP, i);
                getChildAt(i).setLayoutParams(layout);

                layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }

            for (int i = 11 ; i < 15; i++)
            {
                layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layout.addRule(RelativeLayout.RIGHT_OF, i);
                layout.addRule(RelativeLayout.ALIGN_TOP, i);
                getChildAt(i).setLayoutParams(layout);
            }


            //String[] numbers = res.getStringArray(R.array.numbers);

            for (int i = 0 ; i < 5 ; i++) {
                //((ColoredTextSwitch) getChildAt(i)).setText(numbers[i]);
                ((ColoredTextSwitch) getChildAt(i)).setText(((Integer) (i + 1)).toString());
            }

            firstBatch();

            OnTouchListener topListen = new OnTouchListener(){
                public boolean onTouch(View v, MotionEvent ev) {
                    if (ev.getAction() == MotionEvent.ACTION_DOWN) {

                        if (curBatch == Batch.FIRST)
                        {
                            touchLog.clear();
                        }
                        off = false;
                        touchLog.add(new TouchData(System.currentTimeMillis(), ev.getAction(), v.getId() - 1, ((ColoredTextSwitch) v).getText()));

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


                    if (ev.getAction() == MotionEvent.ACTION_UP)
                    {
                        touchLog.add(new TouchData(System.currentTimeMillis(), ev.getAction(), v.getId() - 1, ((ColoredTextSwitch) v).getText()));

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
                        if (ev.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            touchLog.add(new TouchData(System.currentTimeMillis(), ev.getAction(), v.getId() - 1, ((ColoredTextSwitch) v).getText()));
                            selected = v.getId() - 1;
                            off = false;
                        }

                        if (!off)
                        {
                            if (ev.getAction() == MotionEvent.ACTION_UP)
                            {
                                touchLog.add(new TouchData(System.currentTimeMillis(), ev.getAction(), v.getId() - 1, ((ColoredTextSwitch) v).getText()));
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
        int topY = hh * 4 / 9;
        int bottomY = hh * 5 / 9;
        int xUnit =  ww / 5;

        topRect = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + ww, getPaddingTop() + topY);
        bottomRect = new RectF(getPaddingLeft(), getPaddingTop() + topY, getPaddingLeft() + ww, getPaddingTop() + hh);

        // They are already lain out in relation to each other, only need to fix size.
        for (int i = 0 ; i < 5 ; i++)
        {
            ((LayoutParams) getChildAt(i).getLayoutParams()).height = topY;
            ((LayoutParams) getChildAt(i).getLayoutParams()).width = xUnit;
        }

        // Fix lost pixels.
        if (xUnit * 5 < ww)
        {
            ((LayoutParams) getChildAt(4).getLayoutParams()).width = ww - xUnit * 4;
        }

        for (int i = 5 ; i < 10 ; i++)
        {
            ((LayoutParams) getChildAt(i).getLayoutParams()).height = (int) (bottomY * 0.8);
            ((LayoutParams) getChildAt(i).getLayoutParams()).width = xUnit;
            ((LayoutParams) getChildAt(i + 5).getLayoutParams()).height = (int) (bottomY / 5);
            ((LayoutParams) getChildAt(i + 5).getLayoutParams()).width = xUnit;
        }

        // Fix lost pixels.
        if (xUnit * 5 < ww)
        {
            ((LayoutParams) getChildAt(9).getLayoutParams()).width = ww - xUnit * 4;
            ((LayoutParams) getChildAt(14).getLayoutParams()).width = ww - xUnit * 4;
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        float x = ev.getX();
        float y = ev.getY();
        circX = x;
        circY = y;
        int prevSelected = selected;


        boolean retVal = true;
        boolean dispatched = false;
//        boolean outside = false;
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            for (int i = 0; i < 5; i++) {
                View child = getChildAt(i);
                if (x > child.getLeft() && x < child.getRight() && y > child.getTop() && y < child.getBottom()) {
                    dispatched = true;
                    float midX = (child.getLeft() + child.getRight()) / 2.0f;
                    float midY = (child.getTop() + child.getBottom()) / 2.0f;
                    if ((ev.getX() < midX && 0.25 * (midX - ev.getX()) > ev.getX() - child.getLeft()) || (ev.getX() > midX && 0.25 * (ev.getX() - midX) > child.getRight() - ev.getX())) {
//                    outside = true;
                        selected = -1;
                        break;
                    }

                    if ((ev.getY() < midY && 0.25 * (midY - ev.getY()) > ev.getY() - child.getTop()) || (ev.getY() > midY && 0.25 * (ev.getY() - midY) > child.getBottom() - ev.getY())) {
//                    outside = true;
                        selected = -1;
                        break;
                    }
                    retVal = child.dispatchTouchEvent(ev);
                    break;
                }
            }
            if (!dispatched) {
                for (int i = 5; i < 10; i++) {
                    View child = getChildAt(i);
                    View label = getChildAt(i + 5);
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
            }
        }

        else
        {
            for (int i = 0; i < 5; i++) {
                View child = getChildAt(i);
                if (x > child.getLeft() && x < child.getRight() && y > child.getTop() && y < child.getBottom()) {
                    dispatched = true;
                    retVal = child.dispatchTouchEvent(ev);
                    break;
                }
            }
            if (!dispatched) {
                for (int i = 5; i < 10; i++) {
                    View child = getChildAt(i);
                    View label = getChildAt(i + 5);

                    if (x > child.getLeft() && x < child.getRight() && y > child.getTop() && y < label.getBottom()) {
                        retVal = child.dispatchTouchEvent(ev);
                        break;
                    }
                }
            }
        }

        if (ev.getAction() == MotionEvent.ACTION_UP)
        {
//            selected = -1;
//            timerHandler.removeCallbacks(timerFunc);
            drawCirc = false;
            selected = -1;
            off = true;
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
                if (vibrator != null)
                {
                    vibrator.vibrate(5);
                }
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

    public void firstBatch()
    {
        if(firstListener != null)
        {
            firstListener.onFirst();
        }
        curBatch = Batch.FIRST;
        topSelected = "";
        bottomSelected = "";
        for (int i = 0 ; i < 6 ; i++)
        {
            pass[i] = "";
        }

        int[] vehicles = res.getIntArray(R.array.vehicles);
        String[] text = res.getStringArray(R.array.vehicles_text);

        for (int i = 0; i < 5; i++)
        {
            ((ColoredTextSwitch) getChildAt(i + 5)).setText(new String(Character.toChars(vehicles[i])));
            ((ColoredTextSwitch) getChildAt(i + 10)).setText(text[i]);
        }
        off = true;
    }

    public void secondBatch()
    {
        if(secondListener != null)
        {
            secondListener.onSecond();
        }
        curBatch = Batch.SECOND;
        topSelected = "";
        bottomSelected = "";
        pass[2] = "";
        pass[3] = "";

        int[] animals = res.getIntArray(R.array.animals);
        String[] text = res.getStringArray(R.array.animals_text);

        for (int i = 0; i < 5; i++)
        {
            ((ColoredTextSwitch) getChildAt(i + 5)).setText(new String(Character.toChars(animals[i])));
            ((ColoredTextSwitch) getChildAt(i + 10)).setText(text[i]);
        }
        off = true;
    }

    public void thirdBatch()
    {
        if(thirdListener != null)
        {
            thirdListener.onThird();
        }
        curBatch = Batch.THIRD;
        topSelected = "";
        bottomSelected = "";
        pass[4] = "";
        pass[5] = "";

        int[] clothes = res.getIntArray(R.array.clothes);
        String[] text = res.getStringArray(R.array.clothes_text);

        for (int i = 0; i < 5; i++)
        {
            ((ColoredTextSwitch) getChildAt(i + 5)).setText(new String(Character.toChars(clothes[i])));
            ((ColoredTextSwitch) getChildAt(i + 10)).setText(text[i]);
        }
        off = true;
    }

    private void nextBatch()
    {
        switch(curBatch)
        {
            case FIRST:
                pass[0] = topSelected;
                pass[1] = bottomSelected;
                if (firstCompleteListener != null)
                {
                    String[] send = {pass[0], pass[1]};
                    firstCompleteListener.onFirstComplete(send);
                }
                secondBatch();
                break;
            case SECOND:
                pass[2] = topSelected;
                pass[3] = bottomSelected;
                if (secondCompleteListener != null)
                {
                    String[] send = {pass[2], pass[3]};
                    secondCompleteListener.onSecondComplete(send);
                }
                thirdBatch();
                break;
            case THIRD:
                pass[4] = topSelected;
                pass[5] = bottomSelected;
                if (thirdCompleteListener != null)
                {
                    String[] send = {pass[0], pass[1]};
                    thirdCompleteListener.onThirdComplete(send);
                }
                if (listener != null)
                {
                    complete();
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

    public void setOnFirstListener(OnFirstListener listener) {this.firstListener = listener;}
    public void setOnSecondListener(OnSecondListener listener) {this.secondListener = listener;}
    public void setOnThirdListener(OnThirdListener listener) {this.thirdListener = listener;}
//    public void stopClock()
//    {
//        timerHandler.removeCallbacks(timerFunc);
//    }

    //For Testing purposes.
    public interface OnFirstCompleteListener
    {
        public void onFirstComplete(String[] pass);
    }

    public interface OnSecondCompleteListener
    {
        public void onSecondComplete(String[] pass);
    }

    public interface OnThirdCompleteListener
    {
        public void onThirdComplete(String[] pass);
    }

    private OnFirstCompleteListener firstCompleteListener;
    private OnSecondCompleteListener secondCompleteListener;
    private OnThirdCompleteListener thirdCompleteListener;
}