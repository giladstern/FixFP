package com.example.gilad.fp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.gilad.fp.tutorial.ListTutorial;
import com.example.gilad.fp.utils.FastPhrase;
import com.example.gilad.fp.utils.Vals;
import com.example.gilad.fp.utils.WideNoLinesFP;
import com.example.gilad.fp.utils.TouchData;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    WideNoLinesFP FP;
    String[] password = new String[6];
    Vals.Types type;
    WideNoLinesFP.Batch curBatch = WideNoLinesFP.Batch.FIRST;
    int timesLeft;
    int stage;
    TextView topMessage;
    int passShown;
    ArrayList<String> stringData = new ArrayList<>();
    ArrayList<Boolean> successData  = new ArrayList<>();
    ArrayList<Boolean> forgotData  = new ArrayList<>();
    ArrayList<Long> timeData  = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        FP = (WideNoLinesFP) findViewById(R.id.list_fp);
        type = Vals.Types.LIST;
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

        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FP.reset();
            }
        });
        findViewById(R.id.center).setBackgroundColor(Color.GRAY);
        findViewById(R.id.left).setBackgroundColor(Color.DKGRAY);
        findViewById(R.id.right).setBackgroundColor(Color.GRAY);

        FP.setOnCompleteListener(new FastPhrase.OnCompleteListener() {
            @Override
            public void onComplete(String[] pass, ArrayList<TouchData> touchLog) {

                stringData.add(TouchData.toJSONArray(touchLog).toString());
                timeData.add(touchLog.get(touchLog.size() - 1).time - touchLog.get(0).time);
                boolean equal = true;
                for (int i = 0 ; i < 6 ; i++)
                {
                    if (!(pass[i].equals(password[i])))
                    {
                        equal = false;
                        break;
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

                if (timesLeft == 0)
                {
                    Intent intent = new Intent(ListActivity.this, AlarmSetActivity.class);
                    intent.putExtra(getString(R.string.stage), stage);
                    intent.putStringArrayListExtra(getString(R.string.log_data), stringData);
                    intent.putExtra(getString(R.string.success_data), successData);
                    intent.putExtra(getString(R.string.forgot_data), forgotData);
                    intent.putExtra(getString(R.string.time_data), timeData);
                    intent.putExtra(getString(R.string.pass_type), type);
                    startActivity(intent);
                    finish();
                }
            }
        });

        FP.setOnFirstListener(new WideNoLinesFP.OnFirstListener() {
            @Override
            public void onFirst() {
                findViewById(R.id.center).setBackgroundColor(Color.GRAY);
                findViewById(R.id.right).setBackgroundColor(Color.GRAY);
                findViewById(R.id.left).setBackgroundColor(Color.DKGRAY);
                curBatch = WideNoLinesFP.Batch.FIRST;
            }
        });
        FP.setOnSecondListener(new WideNoLinesFP.OnSecondListener() {
            @Override
            public void onSecond() {
                findViewById(R.id.left).setBackgroundColor(Color.DKGRAY);
                findViewById(R.id.right).setBackgroundColor(Color.GRAY);
                findViewById(R.id.center).setBackgroundColor(Color.DKGRAY);
                curBatch = WideNoLinesFP.Batch.SECOND;
            }
        });
        FP.setOnThirdListener(new WideNoLinesFP.OnThirdListener() {
            @Override
            public void onThird() {
                findViewById(R.id.center).setBackgroundColor(Color.DKGRAY);
                findViewById(R.id.left).setBackgroundColor(Color.DKGRAY);
                findViewById(R.id.right).setBackgroundColor(Color.DKGRAY);
                curBatch = WideNoLinesFP.Batch.THIRD;
            }
        });

        findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WideNoLinesFP) findViewById(R.id.list_fp)).firstBatch();
            }
        });
        findViewById(R.id.center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curBatch != WideNoLinesFP.Batch.FIRST) {
                    ((WideNoLinesFP) findViewById(R.id.list_fp)).secondBatch();
                }
            }
        });
        findViewById(R.id.right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curBatch != WideNoLinesFP.Batch.FIRST && curBatch != WideNoLinesFP.Batch.SECOND) {
                    ((WideNoLinesFP) findViewById(R.id.list_fp)).thirdBatch();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            FP.reset();
            FP.clearLog();
        }
        else
        {
            Intent intent = new Intent(this, AlarmSetActivity.class);
            intent.putExtra(getString(R.string.stage), stage);
            intent.putStringArrayListExtra(getString(R.string.log_data), stringData);
            intent.putExtra(getString(R.string.success_data), successData);
            intent.putExtra(getString(R.string.forgot_data), forgotData);
            intent.putExtra(getString(R.string.time_data), timeData);
            intent.putExtra(getString(R.string.pass_type), type);
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
                        onResume();
                        forgotData.add(false);
                    }
                }).setPositiveButton(R.string.forgot, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        passShown++;
                        forgotData.add(true);
                        Intent intent = new Intent(ListActivity.this, PassGenerate.class);
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
