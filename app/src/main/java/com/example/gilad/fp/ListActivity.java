package com.example.gilad.fp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    WideNoLinesFP FP;
    String[] password = new String[6];
    MainActivity.Types type;
    WideNoLinesFP.Batch curBatch = WideNoLinesFP.Batch.FIRST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        FP = (WideNoLinesFP) findViewById(R.id.list_fp);
        type = MainActivity.Types.LIST;

        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FP.reset();
            }
        });
        findViewById(R.id.center).setBackgroundColor(Color.GRAY);
        findViewById(R.id.left).setBackgroundColor(Color.DKGRAY);
        findViewById(R.id.right).setBackgroundColor(Color.GRAY);

        findViewById(R.id.change_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences(getString(R.string.filename), MODE_PRIVATE).edit().putInt(getString(R.string.pass_type), -1).commit();
                Intent next = new Intent(ListActivity.this, MainActivity.class);
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
}
