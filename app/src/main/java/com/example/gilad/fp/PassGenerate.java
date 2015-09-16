package com.example.gilad.fp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import haibison.android.lockpattern.util.ResourceUtils;
import haibison.android.lockpattern.widget.LockPatternView;


public class PassGenerate extends AppCompatActivity {

    String[] password = new String[6];
    String[] labels = new String[6];
    SharedPreferences prefs;
    RelativeLayout layout;
    TextView views[];
    MainActivity.Types type;
    float scale;
    LockPatternView lockPatternView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pass_generate);

        Resources res = getResources();
        scale = res.getDisplayMetrics().density;
        prefs = getSharedPreferences(getString(R.string.filename), MODE_PRIVATE);
        layout = (RelativeLayout) findViewById(R.id.inner_rel_layout);

        Typeface emojiFont = Typeface.createFromAsset(getAssets(), "fonts/androidemoji.ttf");
        Typeface cartoonFont = Typeface.createFromAsset(getAssets(), "fonts/cartoon.ttf");
        Typeface locationFont = Typeface.createFromAsset(getAssets(), "fonts/FamousBuildings.ttf");

        type = (MainActivity.Types) getIntent().getSerializableExtra("type");

        for (int i = 0 ; i < 6 ; i++)
        {
            password[i] = prefs.getString(String.format("char%d", i), "");
            labels[i] = prefs.getString(String.format("label%d", i), "");
        }

        if (password[0].equals(""))
        {
            int [] indices = new int[6];

            for (int i = 0 ; i < 6 ; i++)
            {
                indices[i] = (int)(Math.random() * 5);
            }
            switch (type)
            {
                case LIST:
                    for (int i = 0 ; i < 3 ; i ++)
                    {
                        password[2 * i] = res.getStringArray(R.array.numbers)[indices[2 * i]];
                        labels[2 * i] = "";
                    }
                    password[1] = new String(Character.toChars(res.getIntArray(R.array.vehicles)[indices[1]]));
                    password[3] = new String(Character.toChars(res.getIntArray(R.array.animals)[indices[3]]));
                    password[5] = new String(Character.toChars(res.getIntArray(R.array.clothes)[indices[5]]));
                    labels[1] = res.getStringArray(R.array.vehicles_text)[indices[1]];
                    labels[3] = res.getStringArray(R.array.animals_text)[indices[3]];
                    labels[5] = res.getStringArray(R.array.clothes_text)[indices[5]];
                    break;
                case TRIPLE_STORY:
                    password[0] = res.getStringArray(R.array.cartoons)[indices[0]];
                    password[1] = new String(Character.toChars(res.getIntArray(R.array.vehicles)[indices[1]]));
                    password[2] = res.getStringArray(R.array.locations)[indices[2]];
                    password[3] = res.getStringArray(R.array.amounts)[indices[3]];
                    password[4] = new String(Character.toChars(res.getIntArray(R.array.animals)[indices[4]]));
                    password[5] = new String(Character.toChars(res.getIntArray(R.array.food)[indices[5]]));
                    labels[0] = res.getStringArray(R.array.cartoons_text)[indices[0]];
                    labels[1] = res.getStringArray(R.array.vehicles_text)[indices[1]];
                    labels[2] = res.getStringArray(R.array.locations_text)[indices[2]];
                    labels[3] = res.getStringArray(R.array.amounts_text)[indices[3]];
                    labels[4] = res.getStringArray(R.array.animals_text)[indices[4]];
                    labels[5] = res.getStringArray(R.array.food_text)[indices[5]];
                    break;
                case PIN:
                    for (int i = 0; i < 4 ; i++)
                    {
                        int digit = (int) (Math.random() * 10);
                        password[i] = Integer.valueOf(digit).toString();
                        labels[i] = "";
                    }

                    if (Math.random() < 0.5f)
                    {
                        password[4] = "*";
                    }

                    else
                    {
                        password[4] = "#";
                    }

                    password[5] = "";
                    labels[4] = "";
                    labels[5] = "";
                    break;
                case PATTERN:
                    List<Integer> possible = new ArrayList<>();
                    List<Integer> code = new ArrayList<>();

                    for (int i = 0; i < 9 ; i++)
                    {
                        possible.add(i);
                    }

                    int current = (int) (Math.random() * 9);
                    possible.remove(current);
                    code.add(current);
                    password[0] = Integer.valueOf(current).toString();
                    labels[0] = "";

                    for (int i = 1 ; i < 6 ; i++)
                    {
                        List<Integer> legalVals = new ArrayList<>(possible);
                        if (current < 3 && !code.contains(current + 3))
                        {
                            legalVals.remove(Integer.valueOf(current + 6));
                        }
                        if (current > 5 && !code.contains(current - 3))
                        {
                            legalVals.remove(Integer.valueOf(current - 6));
                        }
                        if (current % 3 == 0 && !code.contains(current + 1))
                        {
                            legalVals.remove(Integer.valueOf(current + 2));
                        }
                        if (current % 3 == 2 && !code.contains(current - 1))
                        {
                            legalVals.remove(Integer.valueOf(current - 2));
                        }
                        if (!code.contains(4))
                        {
                            if (current == 0)
                            {
                                legalVals.remove(Integer.valueOf(8));
                            }
                            else if (current == 2)
                            {
                                legalVals.remove(Integer.valueOf(6));
                            }
                            else if (current == 6)
                            {
                                legalVals.remove(Integer.valueOf(2));
                            }
                            else if (current == 8)
                            {
                                legalVals.remove(Integer.valueOf(0));
                            }
                        }

                        current = legalVals.get((int) (Math.random() * legalVals.size()));
                        code.add(current);
                        possible.remove(Integer.valueOf(current));
                        password[i] = Integer.valueOf(current).toString();
                        labels[i] = "";
                    }
                    break;
            }

            SharedPreferences.Editor editor = prefs.edit();

            for (int i = 0 ; i < 6 ; i ++)
            {
                editor.putString(String.format("char%d", i), password[i]);
                editor.putString(String.format("label%d", i), labels[i]);
            }
            editor.commit();
        }

        switch (type)
        {
            case LIST:
                views = new TextView[9];

                for (int i = 0 ; i < 9 ; i ++)
                {
                    AutoResizeTextView textView = new AutoResizeTextView(this);
                    layout.addView(textView, i);
                    textView.setId(i + 1);
                    views[i] = textView;
                    textView.setGravity(Gravity.CENTER);
                }
                for (int i = 0 ; i < 3 ; i ++)
                {
                    views[2 * i].setText(password[2 * i]);
                    views[2 * i + 1].setText(password[2 * i + 1]);
                    views[2 * i + 1].setTypeface(emojiFont);
                    views[i + 6].setText(labels[2 * i + 1]);
                }

                ((RelativeLayout.LayoutParams) views[0].getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                ((RelativeLayout.LayoutParams) views[0].getLayoutParams()).addRule(RelativeLayout.BELOW, findViewById(R.id.message).getId());

                for (int i = 1; i < 3 ; i ++)
                {
                    ((RelativeLayout.LayoutParams) views[i * 2].getLayoutParams()).addRule(RelativeLayout.ALIGN_LEFT, views[(i - 1) * 2].getId());
                    ((RelativeLayout.LayoutParams) views[i * 2].getLayoutParams()).addRule(RelativeLayout.BELOW, views[(i - 1) * 2].getId());
                    ((RelativeLayout.LayoutParams) views[i * 2].getLayoutParams()).setMargins(0, (int) (30 * scale + 0.5f), 0, 0);
                }

                for (int i = 0 ; i < 3 ; i++)
                {
                    ((RelativeLayout.LayoutParams) views[i * 2 + 1].getLayoutParams()).addRule(RelativeLayout.ALIGN_TOP, views[i * 2].getId());
                    ((RelativeLayout.LayoutParams) views[i * 2 + 1].getLayoutParams()).addRule(RelativeLayout.RIGHT_OF, views[i * 2].getId());
                    ((RelativeLayout.LayoutParams) views[i + 6].getLayoutParams()).addRule(RelativeLayout.BELOW, views[i * 2 + 1].getId());
                    ((RelativeLayout.LayoutParams) views[i + 6].getLayoutParams()).addRule(RelativeLayout.ALIGN_LEFT, views[i * 2 + 1].getId());
                }

                break;
            case TRIPLE_STORY:
                views = new TextView[17];
                for (int i = 0 ; i < 12 ; i ++)
                {
                    AutoResizeTextView textView = new AutoResizeTextView(this);
                    textView.full();
                    layout.addView(textView, i);
                    textView.setId(i + 1);
                    views[i] = textView;
                    views[i].setGravity(Gravity.CENTER);
                    views[i].setTextColor(Color.BLACK);
                }
                for (int i = 12 ; i < 16; i++)
                {
                    SingleLineTextView textView = new SingleLineTextView(this);
                    layout.addView(textView, i);
                    textView.setId(i + 1);
                    views[i] = textView;
                    views[i].setGravity(Gravity.CENTER);
                    views[i].setTextColor(Color.rgb(170, 170, 170));
                    views[i].setTextSize(30);
                }

                views[16] = new AutoResizeTextView(this);
                layout.addView(views[16], 16);
                views[16].setGravity(Gravity.CENTER);
                views[16].setTextColor(Color.GRAY);
                views[16].setId(16 + 1);

                for (int i = 0 ; i < 6 ; i ++)
                {
                    views[i].setText(password[i]);
                    views[i + 6].setText(labels[i]);
                    ((RelativeLayout.LayoutParams) views[i + 6].getLayoutParams()).addRule(RelativeLayout.ALIGN_LEFT, views[i].getId());
                    ((RelativeLayout.LayoutParams) views[i + 6].getLayoutParams()).addRule(RelativeLayout.BELOW, views[i].getId());
                }

                views[12].setText("took");
                views[13].setText("to");
                views[14].setText("hungry");
                views[15].setText("eating");
                views[16].setText("and saw");

                views[0].setTypeface(cartoonFont);
                views[1].setTypeface(emojiFont);
                views[2].setTypeface(locationFont);
                views[4].setTypeface(emojiFont);
                views[5].setTypeface(emojiFont);

                ((RelativeLayout.LayoutParams) views[0].getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                ((RelativeLayout.LayoutParams) views[0].getLayoutParams()).addRule(RelativeLayout.BELOW, findViewById(R.id.message).getId());
                ((RelativeLayout.LayoutParams) views[16].getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                ((RelativeLayout.LayoutParams) views[16].getLayoutParams()).addRule(RelativeLayout.BELOW, views[6].getId());
                ((RelativeLayout.LayoutParams) views[3].getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                ((RelativeLayout.LayoutParams) views[3].getLayoutParams()).addRule(RelativeLayout.BELOW, views[16].getId());

                for (int i = 0 ; i < 2 ; i ++)
                {
                    for (int j = 0 ; j < 2 ; j ++)
                    {
                        ((RelativeLayout.LayoutParams) views[j + i * 2 + 12].getLayoutParams()).addRule(RelativeLayout.RIGHT_OF, views[j + 3 * i].getId());
                        ((RelativeLayout.LayoutParams) views[j + i * 2 + 12].getLayoutParams()).addRule(RelativeLayout.ALIGN_TOP, views[j + 3 * i].getId());
                        ((RelativeLayout.LayoutParams) views[j + i * 3 + 1].getLayoutParams()).addRule(RelativeLayout.RIGHT_OF, views[j + i * 2 + 12].getId());
                        ((RelativeLayout.LayoutParams) views[j + i * 3 + 1].getLayoutParams()).addRule(RelativeLayout.ALIGN_TOP, views[j + i * 2 + 12].getId());
                    }
                }
                break;
            case PIN:
                AutoResizeTextView textView = new AutoResizeTextView(this);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (200 * scale + 0.5f));
                layoutParams.addRule(RelativeLayout.BELOW, findViewById(R.id.message).getId());
                textView.setGravity(Gravity.CENTER);
                textView.setText(password[0] + " " + password[1] + " " + password[2] + " " + password[3] + " " + password[4]);
                layout.addView(textView, layoutParams);
                break;

            case PATTERN:
                // Make new ContextThemeWrapper
                Context newContext = new ContextThemeWrapper(PassGenerate.this, R.style.Alp_42447968_Theme_Light);
                // Apply this resource
                final int resThemeResources = ResourceUtils.resolveAttribute(newContext, R.attr.alp_42447968_theme_resources);
                newContext.getTheme().applyStyle(resThemeResources, true);

                lockPatternView = new LockPatternView(newContext);

                layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.addRule(RelativeLayout.BELOW, findViewById(R.id.message).getId());

                layout.addView(lockPatternView, layoutParams);

                ArrayList<LockPatternView.Cell> pattern =  new ArrayList<>();
                for (int i = 0; i < 6 ; i++)
                {
                    int pass = Integer.parseInt(password[i]);
                    pattern.add(LockPatternView.Cell.of(pass));
                }
                lockPatternView.setPattern(LockPatternView.DisplayMode.Animate, pattern);
                lockPatternView.disableInput();
                break;
        }

        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = layout.getMeasuredWidth() - layout.getPaddingLeft() - layout.getPaddingRight();
                int height;
                int widthUnit;
                switch (type) {
                    case LIST:
                        height = (int) (75 * scale + 0.5f);
                        widthUnit = width / 2;
                        for (int i = 0; i < 3; i++) {
                            views[i * 2].getLayoutParams().height = height;
                            views[i * 2 + 1].getLayoutParams().height = (int) (height * 0.75);
                            views[i + 6].getLayoutParams().height = (int) (height * 0.25);
                            views[i].getLayoutParams().width = widthUnit;
                            views[i + 3].getLayoutParams().width = widthUnit;
                            views[i + 6].getLayoutParams().width = widthUnit;
                        }

                        for (int i = 0; i < 9; i++) {
                            ((AutoResizeTextView) views[i]).full();
                        }
                        break;
                    case TRIPLE_STORY:
                        height = (int) (100 * scale + 0.5f);
                        widthUnit = width / 8;
                        for (int i = 0; i < 6; i++) {
                            views[i].getLayoutParams().height = (int) (height * 0.75f);
                            views[i + 6].getLayoutParams().height = (int) (height * 0.25f);
                            views[i].getLayoutParams().width = widthUnit * 2;
                            views[i + 6].getLayoutParams().width = widthUnit * 2;
                        }
                        for (int i = 12; i < 16; i++) {
                            views[i].getLayoutParams().height = height;
                            views[i].getLayoutParams().width = widthUnit;
                        }

                        ((RelativeLayout.LayoutParams) views[16].getLayoutParams()).width = ViewGroup.LayoutParams.MATCH_PARENT;
                        ((RelativeLayout.LayoutParams) views[16].getLayoutParams()).height = (int) (100 * scale + 0.5f);
                        views[16].setPadding(0, (int) (30 * scale + 0.5f), 0, (int) (30 * scale + 0.5f));
                        break;
                    case PATTERN:
                        lockPatternView.getLayoutParams().height = layout.getWidth();
                        lockPatternView.getLayoutParams().width = layout.getHeight();
                        break;
                }
            }
        });
    }


    public void buttonOnClick(View v)
    {
        finish();
    }

}
