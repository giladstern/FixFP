package com.example.gilad.fp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import haibison.android.lockpattern.util.ResourceUtils;
import haibison.android.lockpattern.widget.LockPatternView;

public class PatternActivity extends AppCompatActivity {

    LockPatternView lockPatternView;
    String[] password = new String[6];
    MainActivity.Types type;
    ArrayList<TouchData> touchLog = new ArrayList<>();
    int stage;
    int timesLeft;

    static final int START = 0;
    static final int ADD = 1;
    static final int FINISH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern);

        type = MainActivity.Types.PATTERN;
        stage = getIntent().getIntExtra("stage", 0);
        timesLeft = DispatchActivity.ITERATIONS[stage];

        // Make new ContextThemeWrapper
        Context newContext = new ContextThemeWrapper(this, R.style.Alp_42447968_Theme_Light);
        // Apply this resource
        final int resThemeResources = ResourceUtils.resolveAttribute(newContext, R.attr.alp_42447968_theme_resources);
        newContext.getTheme().applyStyle(resThemeResources, true);

        lockPatternView = new LockPatternView(newContext);

        final RelativeLayout relativeLayout =  ((RelativeLayout) findViewById(R.id.background));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(relativeLayout.getWidth(), relativeLayout.getWidth());
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        relativeLayout.addView(lockPatternView, layoutParams);

        ViewTreeObserver vto = relativeLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                lockPatternView.getLayoutParams().height = relativeLayout.getWidth();
                lockPatternView.getLayoutParams().width = relativeLayout.getHeight();
            }
        });

        lockPatternView.setOnPatternListener(new LockPatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {
                touchLog.add(new TouchData(System.currentTimeMillis(), START, -1, ""));
            }

            @Override
            public void onPatternCleared() {

            }

            @Override
            public void onPatternCellAdded(List<LockPatternView.Cell> list) {
                touchLog.add(new TouchData(System.currentTimeMillis(), ADD, list.get(list.size() - 1).getId(), ""));
            }

            @Override
            public void onPatternDetected(List<LockPatternView.Cell> list) {
                timesLeft--;
                touchLog.add(new TouchData(System.currentTimeMillis(), FINISH, -1, ""));
                Intent next = new Intent(lockPatternView.getContext(), SuccMsg.class);
                next.putExtra("type", type);
                boolean equal = true;
                if (list.size() != 6)
                {
                    equal = false;
                }
                else {
                    for (int i = 0; i < 6; i++) {
                        if (!(Integer.valueOf(list.get(i).getId()).toString().equals(password[i]))) {
                            equal = false;
                            break;
                        }
                    }
                }
                if (equal) {
                    next.putExtra("succCode", true);
                } else {
                    next.putExtra("succCode", false);
                }

                next.putExtra("time", touchLog.get(touchLog.size() - 1).time - touchLog.get(0).time);

                startActivity(next);
            }
        });

        findViewById(R.id.change_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences(getString(R.string.filename), MODE_PRIVATE).edit().putInt(getString(R.string.pass_type), -1).commit();
                Intent next = new Intent(v.getContext(), MainActivity.class);
                startActivity(next);
                finish();
            }
        });

        findViewById(R.id.new_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences(getString(R.string.filename), MODE_PRIVATE).edit().putString("char0", "").commit();
                Intent next = new Intent(v.getContext(), PassGenerate.class);
                next.putExtra("type", type);
                startActivity(next);
            }
        });

        findViewById(R.id.forgot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(v.getContext(), PassGenerate.class);
                next.putExtra("type", type);
                startActivity(next);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pattern, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (timesLeft != 0) {
            SharedPreferences prefs = getSharedPreferences(getString(R.string.filename), MODE_PRIVATE);
            for (int i = 0; i < 6; i++) {
                password[i] = prefs.getString(String.format("char%d", i), "");
            }
            if (password[0].equals("")) {
                Intent next = new Intent(this, PassGenerate.class);
                next.putExtra("type", type);
                startActivity(next);
            }
            lockPatternView.clearPattern();
        }
        else
        {
            Intent intent = new Intent(this, AlarmSetActivity.class);
            intent.putExtra("stage", stage);
            startActivity(intent);
            finish();
        }
    }
}
