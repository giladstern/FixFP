package com.example.gilad.fp;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.concurrent.TimeUnit;

import com.example.gilad.fp.utils.Alarm;
import com.example.gilad.fp.utils.AutoResizeTextView;
import com.example.gilad.fp.utils.Vals;

public class AlarmSetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set);

        int stage = getIntent().getIntExtra("stage", 0);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        AutoResizeTextView textView = (AutoResizeTextView) findViewById(R.id.exit_message);

        if (stage < Vals.STAGES - 1) {
            int gap = Vals.GAP[stage];

            String unit = " day";

            if (gap != 1) {
                unit += "s";
            }


            String text = "Thank you!\nYou will be prompted in " + Integer.valueOf(gap).toString() + unit + ".";

            textView.setText(text);
//            long nextAlarm = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(gap);
            long nextAlarm = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);

            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.stage_file), MODE_PRIVATE).edit();

            editor.putInt(getString(R.string.stage), stage + 1);
            editor.putLong(getString(R.string.next_alarm), nextAlarm);

            editor.commit();

            Alarm.set(this, nextAlarm);
        }

        else
        {
            textView.setText("Thank you for participating!");
            SharedPreferences prefs = getSharedPreferences(getString(R.string.stage_file), MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(getString(R.string.stage), 0);
            editor.putLong(getString(R.string.next_alarm), 0);

            if (!prefs.getBoolean(getString(R.string.second), false))
            {
                editor.putBoolean(getString(R.string.second), true);
            }
            else
            {
                editor.putInt(getString(R.string.order), -1);
                editor.putBoolean(getString(R.string.second), false);

                textView.setText((textView.getText()) + "\nFinito!");
            }

            editor.commit();

            getSharedPreferences(getString(R.string.filename), MODE_PRIVATE).edit().clear().commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_set, menu);
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

    public void exitOnClick(View v)
    {
        finish();
    }
}
