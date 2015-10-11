package com.example.gilad.fp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.gilad.fp.utils.TouchData;
import com.example.gilad.fp.utils.DiagonalStoryFP;
import com.example.gilad.fp.utils.FastPhrase;
import com.example.gilad.fp.utils.Vals;

import java.util.ArrayList;

public class StoryActivity extends AppCompatActivity {

    FastPhrase FP;
    String[] password = new String[6];
    Vals.Types type;
    int stage;
    int timesLeft;
    TextView topMessage;
    int passShown;
    ArrayList<String> stringData = new ArrayList<>();
    ArrayList<Boolean> successData  = new ArrayList<>();
    ArrayList<Boolean> forgotData  = new ArrayList<>();
    ArrayList<Long> timeData  = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_story);

//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        FP = (DiagonalStoryFP) findViewById(R.id.story_fp);
        type = Vals.Types.TRIPLE_STORY;
        stage = getIntent().getIntExtra("stage", 0);
        timesLeft = Vals.ITERATIONS[stage];
        topMessage = (TextView) findViewById(R.id.top_message);

        if (stage == 0)
        {
            Intent pass = new Intent(this, PassGenerate.class);
            pass.putExtra(getString(R.string.pass_type), type);
            pass.putExtra(getString(R.string.message), "Now that you know how to enter your code, memorize it again for future use. " +
                    "Don’t write it down. " +
                    "If you forget the code, we will remind you.");
            startActivity(pass);
        }

        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FP.reset();
            }
        });

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
                    topMessage.setText(Html.fromHtml(String.format(getString(R.string.num_left_msg), timesLeft)));
                }

                alert(equal);
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
            topMessage.setText(Html.fromHtml(String.format(getString(R.string.num_left_msg), timesLeft)));
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

    private void alert(boolean success)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (success) {
            forgotData.add(false);
            successData.add(true);
            builder.setTitle(R.string.correct);
            timesLeft--;
            if (timesLeft != 0)
            {
                builder.setNeutralButton(R.string.again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResume();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        onResume();
                    }
                });
            }
            else
            {
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (timesLeft == 0)
                        {
                            Intent intent = new Intent(StoryActivity.this, AlarmSetActivity.class);
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
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (timesLeft == 0)
                        {
                            Intent intent = new Intent(StoryActivity.this, AlarmSetActivity.class);
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
            }
        } else {
            successData.add(false);
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
                            passShown++;
                            forgotData.add(true);
                            Intent intent = new Intent(StoryActivity.this, PassGenerate.class);
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
        }
        builder.show();
    }
}
