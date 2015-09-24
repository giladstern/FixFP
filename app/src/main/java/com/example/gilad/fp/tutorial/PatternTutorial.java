package com.example.gilad.fp.tutorial;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gilad.fp.AlarmSetActivity;
import com.example.gilad.fp.DispatchActivity;
import com.example.gilad.fp.MainActivity;
import com.example.gilad.fp.PassGenerate;
import com.example.gilad.fp.PatternActivity;
import com.example.gilad.fp.R;
import com.example.gilad.fp.SuccMsg;
import com.example.gilad.fp.utils.TouchData;
import com.example.gilad.fp.utils.Vals;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import haibison.android.lockpattern.util.ResourceUtils;
import haibison.android.lockpattern.widget.LockPatternView;

public class PatternTutorial extends AppCompatActivity {

    Vals.Types type = Vals.Types.PATTERN;

    LockPatternView lockPatternView;
    String[] password = new String[6];
    ArrayList<TouchData> touchLog = new ArrayList<>();

    static final int START = 0;
    static final int ADD = 1;
    static final int FINISH = 0;

    boolean finished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_tutorial);

        getSharedPreferences(getString(R.string.filename), MODE_PRIVATE).edit().putString("char0", "").commit();

        Intent passIntent = new Intent(this, PassGenerate.class);
        passIntent.putExtra(getString(R.string.pass_type), type);
        passIntent.putExtra(getString(R.string.generate), getIntent().getBooleanExtra(getString(R.string.generate), true));
        startActivity(passIntent);

        // Make new ContextThemeWrapper
        Context newContext = new ContextThemeWrapper(this, R.style.Alp_42447968_Theme_Light);
        // Apply this resource
        final int resThemeResources = ResourceUtils.resolveAttribute(newContext, R.attr.alp_42447968_theme_resources);
        newContext.getTheme().applyStyle(resThemeResources, true);

        lockPatternView = new LockPatternView(newContext);

        final RelativeLayout relativeLayout =  ((RelativeLayout) findViewById(R.id.background));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(relativeLayout.getWidth(), relativeLayout.getWidth());
        layoutParams.addRule(RelativeLayout.BELOW, findViewById(R.id.message).getId());

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
                touchLog.add(new TouchData(System.currentTimeMillis(), FINISH, -1, ""));
                Intent next = new Intent(PatternTutorial.this, TutorialSuccess.class);
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
                    next.putExtra(getString(R.string.success), true);
                    finished = true;
                } else {
                    next.putExtra(getString(R.string.success), false);
                }

                startActivity(next);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pattern_tutorial, menu);
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
        if (finished)
        {
            Intent next = new Intent(this, LoopActivity.class);
            next.putExtra(getString(R.string.pass_type), type);
            startActivity(next);
            finish();
        }
        else
        {
            SharedPreferences prefs = getSharedPreferences(getString(R.string.filename), MODE_PRIVATE);

            for (int i = 0; i < 6; i++) {
                password[i] = prefs.getString(String.format("char%d", i), "");

            }

            if (password[0] != null && !password[0].equals(""))
            {
                ArrayList<LockPatternView.Cell> pattern = new ArrayList<>();
                for (int i = 0; i< 6; i++) {
                    int pass = Integer.parseInt(password[i]);
                    pattern.add(LockPatternView.Cell.of(pass));
                }
                lockPatternView.setPattern(LockPatternView.DisplayMode.Animate, pattern);
            }
            touchLog.clear();
        }
    }
}
