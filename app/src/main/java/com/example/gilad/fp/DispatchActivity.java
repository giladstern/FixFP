package com.example.gilad.fp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.gilad.fp.tutorial.FirstScreen;
import com.example.gilad.fp.utils.Vals.Types;

public class DispatchActivity extends AppCompatActivity {

    // In lieu of Enum for SharedPreferences.
    static final int STORY_LIST = 0;
    static final int LIST_STORY = 1;
    static final int STORY_PIN = 2;
    static final int PIN_STORY = 3;
    static final int STORY_PATTERN = 4;
    static final int PATTERN_STORY = 5;
    static final int LIST_PIN = 6;
    static final int PIN_LIST = 7;
    static final int LIST_PATTERN = 8;
    static final int PATTERN_LIST = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);
        SharedPreferences preferences = getSharedPreferences(getString(R.string.stage_file), MODE_PRIVATE);
        int order = preferences.getInt(getString(R.string.order), -1);
        int stage = preferences.getInt(getString(R.string.stage), 0);
        boolean second = preferences.getBoolean(getString(R.string.second), false);
        long alarmTime = preferences.getLong(getString(R.string.next_alarm), 0);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();

        Intent intent;
        
        if (order == -1)
        {
            order = (int) (Math.random() * 10);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(getString(R.string.order), order);
            editor.putInt(getString(R.string.stage), 0);
            editor.putBoolean(getString(R.string.second), false);
            editor.putLong(getString(R.string.next_alarm), 0);
            editor.commit();
        }

        if (stage == 0)
        {
            intent = new Intent(this, FirstScreen.class);
            getSharedPreferences(getString(R.string.filename), MODE_PRIVATE).edit().clear().commit();
        }

        else
        {
            intent = new Intent(this, WelcomeScreen.class);
            intent.putExtra(getString(R.string.stage), stage);
        }

        if (alarmTime < System.currentTimeMillis()) {

            switch (order) {
                case STORY_LIST:
                    if (!second) {
                        intent.putExtra(getString(R.string.pass_type), Types.TRIPLE_STORY);
                    } else {
                        intent.putExtra(getString(R.string.pass_type), Types.LIST);
                    }
                    break;
                case LIST_STORY:
                    if (second) {
                        intent.putExtra(getString(R.string.pass_type), Types.TRIPLE_STORY);
                    } else {
                        intent.putExtra(getString(R.string.pass_type), Types.LIST);
                    }
                    break;
                case STORY_PIN:
                    if (!second) {
                        intent.putExtra(getString(R.string.pass_type), Types.TRIPLE_STORY);
                    } else {
                        intent.putExtra(getString(R.string.pass_type), Types.PIN);
                    }
                    break;
                case PIN_STORY:
                    if (second) {
                        intent.putExtra(getString(R.string.pass_type), Types.TRIPLE_STORY);
                    }
                    else {
                        intent.putExtra(getString(R.string.pass_type), Types.PIN);
                    }
                    break;
                case STORY_PATTERN:
                    if (!second) {
                        intent.putExtra(getString(R.string.pass_type), Types.TRIPLE_STORY);
                    } else {
                        intent.putExtra(getString(R.string.pass_type), Types.PATTERN);
                    }
                    break;
                case PATTERN_STORY:
                    if (second) {
                        intent.putExtra(getString(R.string.pass_type), Types.TRIPLE_STORY);
                    } else {
                        intent.putExtra(getString(R.string.pass_type), Types.PATTERN);
                    }
                    break;
                case LIST_PIN:
                    if (!second) {
                        intent.putExtra(getString(R.string.pass_type), Types.LIST);
                    } else {
                        intent.putExtra(getString(R.string.pass_type), Types.PIN);
                    }
                    break;
                case PIN_LIST:
                    if (second) {
                        intent.putExtra(getString(R.string.pass_type), Types.LIST);
                    } else {
                        intent.putExtra(getString(R.string.pass_type), Types.PIN);
                    }
                    break;
                case LIST_PATTERN:
                    if (!second) {
                        intent.putExtra(getString(R.string.pass_type), Types.LIST);
                    } else {
                        intent.putExtra(getString(R.string.pass_type), Types.PATTERN);
                    }
                    break;
                case PATTERN_LIST:
                    if (second) {
                        intent.putExtra(getString(R.string.pass_type), Types.LIST);
                    } else {
                        intent.putExtra(getString(R.string.pass_type), Types.PATTERN);
                    }
                    break;
            }

            startActivity(intent);
            finish();
        }

        else
        {
            long timeLeft = alarmTime - System.currentTimeMillis();
            timeLeft /= 60000;
            long  minutes = timeLeft % 60;
            timeLeft /= 60;
            long hours = timeLeft % 60;
            long days = timeLeft / 24;

            if (days + hours + minutes == 0)
            {
                minutes = 1;
            }

            String minuteString = "minute";
            if (minutes != 1)
            {
                minuteString += "s";
            }

            String hourString = "hour";
            if (hours != 1)
            {
                hourString += "s";
            }

            String dayString = "day";
            if (days != 1)
            {
                dayString += "s";
            }

            String message = String.format("You have %d " + dayString + ", %d " +
                    hourString + " and %d " + minuteString + " until your next session.\nThank you!", days, hours, minutes);

            ((TextView) findViewById(R.id.message)).setText(message);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dispatch, menu);
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
