package com.example.gilad.fp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.gilad.fp.utils.TouchData;
import com.example.gilad.fp.utils.Vals;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import haibison.android.lockpattern.util.ResourceUtils;
import haibison.android.lockpattern.widget.LockPatternView;

public class PatternActivity extends AppCompatActivity {

    LockPatternView lockPatternView;
    String[] password = new String[6];
    Vals.Types type;
    ArrayList<TouchData> touchLog = new ArrayList<>();
    int stage;
    int timesLeft;
    TextView topMessage;
    int passShown = 0;
    ArrayList<String> stringData = new ArrayList<>();
    ArrayList<Boolean> successData  = new ArrayList<>();
    ArrayList<Boolean> forgotData  = new ArrayList<>();
    ArrayList<Long> timeData  = new ArrayList<>();

    static final int START = 0;
    static final int ADD = 1;
    static final int FINISH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern);


        type = Vals.Types.PATTERN;
        stage = getIntent().getIntExtra("stage", 0);
        timesLeft = Vals.ITERATIONS[stage];
        topMessage = (TextView) findViewById(R.id.top_message);

        if (stage == 0)
        {
            Intent pass = new Intent(this, PassGenerate.class);
            pass.putExtra(getString(R.string.pass_type), type);
            pass.putExtra(getString(R.string.message), "Now that you know how to enter your code, memorize it again for future use. \n" +
                    "Don’t write it down.\n" +
                    "If you forget the code, we will remind you.");
            startActivity(pass);
        }

        // Make new ContextThemeWrapper
        Context newContext = new ContextThemeWrapper(this, R.style.Alp_42447968_Theme_Light);
        // Apply this resource
        final int resThemeResources = ResourceUtils.resolveAttribute(newContext, R.attr.alp_42447968_theme_resources);
        newContext.getTheme().applyStyle(resThemeResources, true);

        lockPatternView = new LockPatternView(newContext);

        final RelativeLayout relativeLayout =  ((RelativeLayout) findViewById(R.id.background));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(relativeLayout.getWidth(), relativeLayout.getWidth());
        layoutParams.addRule(RelativeLayout.BELOW, R.id.scrollView);

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
                stringData.add(TouchData.toJSONArray(touchLog).toString());
                if (touchLog.size() == 0)
                {
                    timeData.add(0l);
                }
                else {
                    timeData.add(touchLog.get(touchLog.size() - 1).time - touchLog.get(0).time);
                }
                boolean equal = true;
                if (list.size() != 6) {
                    equal = false;
                } else {
                    for (int i = 0; i < 6; i++) {
                        if (!(Integer.valueOf(list.get(i).getId()).toString().equals(password[i]))) {
                            equal = false;
                            break;
                        }
                    }
                }

                if (equal)
                {
                    timesLeft--;
                    successData.add(true);
                    forgotData.add(false);
                    topMessage.setText(String.format(getString(R.string.num_left_msg), timesLeft));
                }

                else
                {
                    successData.add(false);
                    alert();
                }

                touchLog.clear();
                lockPatternView.clearPattern();
                if (timesLeft == 0)
                {
                    Intent intent = new Intent(PatternActivity.this, AlarmSetActivity.class);
                    intent.putExtra(getString(R.string.stage), stage);
                    intent.putStringArrayListExtra(getString(R.string.log_data), stringData);
                    intent.putExtra(getString(R.string.success_data), successData);
                    intent.putExtra(getString(R.string.forgot_data), forgotData);
                    intent.putExtra(getString(R.string.time_data), timeData);
                    startActivity(intent);
                    finish();
                }
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
            if (password[0] == null)
            {
                SharedPreferences prefs = getSharedPreferences(getString(R.string.filename), MODE_PRIVATE);
                for (int i = 0; i < 6; i++) {
                    password[i] = prefs.getString(String.format("char%d", i), "");
                }
            }
            topMessage.setText(String.format(getString(R.string.num_left_msg), timesLeft));
            lockPatternView.clearPattern();
        }
        else
        {
            Intent intent = new Intent(this, AlarmSetActivity.class);
            intent.putExtra(getString(R.string.stage), stage);
            intent.putStringArrayListExtra(getString(R.string.log_data), stringData);
            intent.putExtra(getString(R.string.success_data), successData);
            intent.putExtra(getString(R.string.forgot_data), forgotData);
            intent.putExtra(getString(R.string.time_data), timeData);
            startActivity(intent);
            finish();
        }
    }

    private void alert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.incorrect)
                .setNeutralButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        forgotData.add(false);
                        onResume();
                    }
                }).setPositiveButton(R.string.forgot, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        forgotData.add(true);
                        passShown++;
                        Intent intent = new Intent(PatternActivity.this, PassGenerate.class);
                        intent.putExtra(getString(R.string.generate), false);
                        intent.putExtra(getString(R.string.pass_type), type);
                        startActivity(intent);
                    }
                }

        ).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                forgotData.add(false);
                onResume();
            }
        });
        builder.show();
    }
}
