package com.example.gilad.fp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;

public class StoryActivity extends AppCompatActivity {

    FastPhrase FP;
    String[] password = new String[6];
    MainActivity.Types type;
    int stage;
    int timesLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_story);

//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        FP = (TripleStoryFP) findViewById(R.id.story_fp);
        type = MainActivity.Types.TRIPLE_STORY;
        stage = getIntent().getIntExtra("stage", 0);
        timesLeft = DispatchActivity.ITERATIONS[stage];

        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FP.reset();
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

        FP.setOnCompleteListener(new FastPhrase.OnCompleteListener() {
            @Override
            public void onComplete(String[] pass, ArrayList<TouchData> touchLog) {
//                FP.reset();
                timesLeft--;
                Intent next = new Intent(FP.getContext(), SuccMsg.class);
                next.putExtra("type", type);
                boolean equal = true;
                for (int i = 0 ; i < 6 ; i++)
                {
                    if (!(pass[i].equals(password[i])))
                    {
                        equal = false;
                        break;
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
            SharedPreferences prefs = getSharedPreferences(getString(R.string.filename), MODE_PRIVATE);
            for (int i = 0; i < 6; i++) {
                password[i] = prefs.getString(String.format("char%d", i), "");
            }
            if (password[0].equals("")) {
                Intent next = new Intent(this, PassGenerate.class);
                next.putExtra("type", type);
                startActivity(next);
            }
            FP.reset();
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
