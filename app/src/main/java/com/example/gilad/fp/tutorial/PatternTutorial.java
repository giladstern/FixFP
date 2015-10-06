package com.example.gilad.fp.tutorial;

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

import com.example.gilad.fp.AlarmSetActivity;
import com.example.gilad.fp.DispatchActivity;
import com.example.gilad.fp.MainActivity;
import com.example.gilad.fp.PassGenerate;
import com.example.gilad.fp.PatternActivity;
import com.example.gilad.fp.R;
import com.example.gilad.fp.SuccMsg;
import com.example.gilad.fp.utils.TouchData;
import com.example.gilad.fp.utils.Vals;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import haibison.android.lockpattern.util.ResourceUtils;
import haibison.android.lockpattern.widget.LockPatternView;

public class PatternTutorial extends AppCompatActivity {

    Vals.Types type = Vals.Types.PATTERN;

    LockPatternView lockPatternView;
    TextView titleView;
    String[] password = new String[6];
    ArrayList<TouchData> touchLog = new ArrayList<>();

    static final int START = 0;
    static final int ADD = 1;
    static final int FINISH = 0;

    boolean prevEqual = false;
    boolean consecEqual = false;
    int timesEqual = 0;
    boolean secondPart = false;
    boolean prevWrong = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_tutorial);

        Intent passIntent = new Intent(this, PassGenerate.class);
        passIntent.putExtra(getString(R.string.pass_type), type);
        passIntent.putExtra(getString(R.string.generate), getIntent().getBooleanExtra(getString(R.string.generate), true));
        passIntent.putExtra(getString(R.string.message), "Your code is the following pattern.\n" +
                "For now, take a look at it, but don’t try to memorize it.");
        startActivity(passIntent);

        titleView = (TextView) findViewById(R.id.message);

        // Make new ContextThemeWrapper
        Context newContext = new ContextThemeWrapper(this, R.style.Alp_42447968_Theme_Light);
        // Apply this resource
        final int resThemeResources = ResourceUtils.resolveAttribute(newContext, R.attr.alp_42447968_theme_resources);
        newContext.getTheme().applyStyle(resThemeResources, true);

        lockPatternView = new LockPatternView(newContext);

        final RelativeLayout relativeLayout =  ((RelativeLayout) findViewById(R.id.background));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(relativeLayout.getWidth(), relativeLayout.getWidth());
        layoutParams.addRule(RelativeLayout.BELOW, findViewById(R.id.scrollView).getId());

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
                    if (!secondPart) {
                        if (prevEqual) {
                            secondPart = true;
                            prevEqual = false;
                            midwayAlert();
                            return;
                        }
                        prevEqual = true;

                    }
                    else {
                        if (prevEqual) {
                            consecEqual = true;
                        }
                        prevEqual = true;
                        prevWrong = false;
                        timesEqual++;
                    }
                }
                else
                {
                    if (secondPart)
                    {
                        prevEqual = false;
                        prevWrong = true;
                    }
                }

                alert(equal);
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
        lockPatternView.clearPattern();
        if (secondPart)
        {
            titleView.setText("Try entering your code (without assistance).\n" +
                    "If you don’t remember the code, give it your best shot.");
            if (consecEqual && timesEqual >= 3) {
                Intent next = new Intent(this, PatternActivity.class);
                startActivity(next);
                finish();
            }
        }
        else
        {
            titleView.setText("Enter your code by dragging your finger across the given pattern.\n" +
                    "The animation will stop once you start.");
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

    private void alert(boolean success)
    {
        String title;
        String message;
        if (success) {
            title = "Correct";
            if (consecEqual && timesEqual >= 3)
            {
                message = "OK";
            }
            else
            {
                message = "Once again.";
            }
        } else {
            title = "Incorrect";
            message = "Try again";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setNeutralButton(message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResume();
                        if (prevWrong) {
                            Intent intent = new Intent(PatternTutorial.this, PassGenerate.class);
                            intent.putExtra(getString(R.string.generate), false);
                            intent.putExtra(getString(R.string.pass_type), Vals.Types.PATTERN);
                            startActivity(intent);
                        }
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onResume();
                if (prevWrong) {
                    Intent intent = new Intent(PatternTutorial.this, PassGenerate.class);
                    intent.putExtra(getString(R.string.generate), false);
                    intent.putExtra(getString(R.string.pass_type), Vals.Types.PATTERN);
                    startActivity(intent);
                }
            }
        });
        builder.show();
    }

    private void midwayAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Correct")
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResume();

                        Intent intent = new Intent(PatternTutorial.this, PassGenerate.class);
                        intent.putExtra(getString(R.string.generate), false);
                        intent.putExtra(getString(R.string.pass_type), Vals.Types.PATTERN);
                        intent.putExtra(getString(R.string.message), "Here is your code once more.\n" +
                                "This time try remembering it, but don’t write it down.\n" +
                                "If you forget it, we will remind you.");
                        startActivity(intent);
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onResume();
                Intent intent = new Intent(PatternTutorial.this, PassGenerate.class);
                intent.putExtra(getString(R.string.generate), false);
                intent.putExtra(getString(R.string.pass_type), Vals.Types.PATTERN);
                intent.putExtra(getString(R.string.message), "Here is your code once more.\n" +
                        "This time try remembering it, but don’t write it down.\n" +
                        "If you forget it, we will remind you.");
                startActivity(intent);

            }
        });
        builder.show();
    }
}
