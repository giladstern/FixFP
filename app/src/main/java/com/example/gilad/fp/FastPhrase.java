package com.example.gilad.fp;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gilad on 8/30/2015.
 */
public abstract class FastPhrase extends RelativeLayout {
    public abstract void back();
    public abstract void reset();
    protected String[] pass = new String[6];
    protected ArrayList<TouchData> touchLog = new ArrayList<TouchData>();


    public FastPhrase(Context context, AttributeSet attrs) {super(context, attrs);}

    public interface OnCompleteListener
    {
        public void onComplete(String[] pass, ArrayList<TouchData> touchLog);
    }
    protected OnCompleteListener listener;

    public void setOnCompleteListener(OnCompleteListener listener) {this.listener = listener;}

    protected void complete()
    {
        listener.onComplete(pass, touchLog);
    }

    protected class ColoredTextSwitch extends TextSwitcher {

        private int textColor;

        public ColoredTextSwitch(Context context) {
            super(context);
        }

        public ColoredTextSwitch(Context context, final int textColor)
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

        public ColoredTextSwitch(Context context, final int textColor, final Typeface font)
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
            ((AutoResizeTextView) getCurrentView()).setTextColor(color);
            ((AutoResizeTextView) getNextView()).setTextColor(color);
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
        }
    }
}
