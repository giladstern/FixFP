package com.example.gilad.fp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

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

    public static final int STAGES = 6;
    public static final int[] ITERATIONS = {10, 5, 5, 3, 3, 3};
    public static final int[] GAP = {1, 1, 1, 2, 2, 2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);
        SharedPreferences preferences = getSharedPreferences(getString(R.string.stage_file), MODE_PRIVATE);
        int order = preferences.getInt(getString(R.string.order), -1);
        int stage = preferences.getInt(getString(R.string.stage), 0);
        boolean second = preferences.getBoolean(getString(R.string.second), false);
        long alarmTime = preferences.getLong(getString(R.string.next_alarm), 0);
        
        Intent intent = null;
        if (stage == 0)
        {
            getSharedPreferences(getString(R.string.filename), MODE_PRIVATE).edit().putString("char0", "").commit();
        }

        if (alarmTime < System.currentTimeMillis()) {

            switch (order) {
                case -1:
                    //TODO: Change into choosing mechanism.
                    order = (int) (Math.random() * 10);
                    preferences.edit().putInt(getString(R.string.order), order).commit();
                case STORY_LIST:
                    if (!second) {
                        if (stage != 0) {
                            intent = new Intent(this, StoryActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, StoryActivity.class);
                        }
                    } else {
                        if (stage != 0) {
                            intent = new Intent(this, ListActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, ListActivity.class);
                        }
                    }
                    break;
                case LIST_STORY:
                    if (second) {
                        if (stage != 0) {
                            intent = new Intent(this, StoryActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, StoryActivity.class);
                        }
                    } else {
                        if (stage != 0) {
                            intent = new Intent(this, ListActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, ListActivity.class);
                        }
                    }
                    break;
                case STORY_PIN:
                    if (!second) {
                        if (stage != 0) {
                            intent = new Intent(this, StoryActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, StoryActivity.class);
                        }
                    } else {
                        if (stage != 0) {
                            intent = new Intent(this, PinActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, PinActivity.class);
                        }
                    }
                    break;
                case PIN_STORY:
                    if (second) {
                        if (stage != 0) {
                            intent = new Intent(this, StoryActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, StoryActivity.class);
                        }
                    }
                    else {
                        if (stage != 0) {
                            intent = new Intent(this, PinActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, PinActivity.class);
                        }
                    }
                    break;
                case STORY_PATTERN:
                    if (!second) {
                        if (stage != 0) {
                            intent = new Intent(this, StoryActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, StoryActivity.class);
                        }
                    } else {
                        if (stage != 0) {
                            intent = new Intent(this, PatternActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, PatternActivity.class);
                        }
                    }
                    break;
                case PATTERN_STORY:
                    if (second) {
                        if (stage != 0) {
                            intent = new Intent(this, StoryActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, StoryActivity.class);
                        }
                    } else {
                        if (stage != 0) {
                            intent = new Intent(this, PatternActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, PatternActivity.class);
                        }
                    }
                    break;
                case LIST_PIN:
                    if (!second) {
                        if (stage != 0) {
                            intent = new Intent(this, ListActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, ListActivity.class);
                        }
                    } else {
                        if (stage != 0) {
                            intent = new Intent(this, PinActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, PinActivity.class);
                        }
                    }
                    break;
                case PIN_LIST:
                    if (second) {
                        if (stage != 0) {
                            intent = new Intent(this, ListActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, ListActivity.class);
                        }
                    } else {
                        if (stage != 0) {
                            intent = new Intent(this, PinActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, PinActivity.class);
                        }
                    }
                    break;
                case LIST_PATTERN:
                    if (!second) {
                        if (stage != 0) {
                            intent = new Intent(this, ListActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, ListActivity.class);
                        }
                    } else {
                        if (stage != 0) {
                            intent = new Intent(this, PatternActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, PatternActivity.class);
                        }
                    }
                    break;
                case PATTERN_LIST:
                    if (second) {
                        if (stage != 0) {
                            intent = new Intent(this, ListActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, ListActivity.class);
                        }
                    } else {

                        if (stage != 0) {
                            intent = new Intent(this, PatternActivity.class);
                        } else {
                            //TODO: change to tutorial intent.
                            intent = new Intent(this, PatternActivity.class);
                        }
                    }
                    break;
            }

            intent = new Intent(this, StoryActivity.class);

            if (intent != null) {
                intent.putExtra(getString(R.string.stage), stage);
                startActivity(intent);
                finish();
            }
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

            String message = String.format("It is not time yet.\nYou have:\n%d " + dayString + ", %d " +
                    hourString + " and %d " + minuteString + "\nleft until your next usage.", days, hours, minutes);

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
