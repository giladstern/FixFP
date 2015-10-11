package com.example.gilad.fp;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.gilad.fp.tutorial.FirstScreen;
import com.example.gilad.fp.utils.Vals;
import com.example.gilad.fp.utils.Vals.Types;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

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
    ParseUser user;
    ParseObject selected;
    SharedPreferences preferences;
    int order;
    int stage;
    boolean second;
    long alarmTime;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);

        user = ParseUser.getCurrentUser();

        preferences = getSharedPreferences(getString(R.string.stage_file), MODE_PRIVATE);
        order = preferences.getInt(getString(R.string.order), -1);
        stage = preferences.getInt(getString(R.string.stage), 0);
        second = preferences.getBoolean(getString(R.string.second), false);
        alarmTime = preferences.getLong(getString(R.string.next_alarm), 0);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();

        if ((second && stage > Vals.STAGES - 1) || preferences.getBoolean(getString(R.string.finished), false)) {
            Intent intent = new Intent(this, AlarmSetActivity.class);
            intent.putExtra(getString(R.string.stage), stage);
            startActivity(intent);
            finish();
        } else {
            if (stage == 0) {
                intent = new Intent(this, FirstScreen.class);
//                getSharedPreferences(getString(R.string.filename), MODE_PRIVATE).edit().clear().commit();
            } else {
                intent = new Intent(this, WelcomeScreen.class);
                intent.putExtra(getString(R.string.stage), stage);
            }

            if (order == -1) {
                getOrder();
            } else {
                next();
            }
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

    private void getOrder()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
//        if (order == -1) {
//            ParseQuery<ParseObject> query = ParseQuery.getQuery("CodeTypes");
//            query.findInBackground(new FindCallback<ParseObject>() {
//                @Override
//                public void done(List<ParseObject> list, ParseException e) {
//                    if (e == null) {
//                        selected = list.get(0);
//                        int userNum = selected.getInt("users");
//                        for (ParseObject elem : list) {
//                            int curUsers = elem.getInt("users");
//                            if (userNum > curUsers) {
//                                userNum = curUsers;
//                                selected = elem;
//                            }
//                        }
//
//                        order = selected.getInt("enum");
//
//                        selected.increment("users");
//                        saveInfo();
//                    } else {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(DispatchActivity.this);
//                        builder.setTitle("Error")
//                                .setMessage("An error has occurred.\n" +
//                                        "Make sure you have an internet connection and try again.")
//                                .setNeutralButton("Try Again", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        getOrder();
//                                    }
//                                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                getOrder();
//                            }
//                        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//                        });
//                        builder.show();
//                    }
//                }
//            });
//        }
//        else
//        {
//            saveUser();
//        }
    }

    private void saveInfo()
    {
        selected.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(getString(R.string.order), order);
                    editor.putInt(getString(R.string.stage), 0);
                    editor.putBoolean(getString(R.string.second), false);
                    editor.putLong(getString(R.string.next_alarm), 0);
                    editor.commit();
                    saveUser();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DispatchActivity.this);
                    builder.setTitle("Error")
                            .setMessage("An error has occurred.\n" +
                                    "Make sure you have an internet connection and try again.")
                            .setNeutralButton("Try Again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveInfo();
                                }
                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            saveInfo();
                        }
                    }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    private void saveUser()
    {
        user.put("order", selected.getInt("enum"));
        user.put("finished", false);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                {
                    next();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DispatchActivity.this);
                    builder.setTitle("Error")
                            .setMessage("An error has occurred.\n" +
                                    "Make sure you have an internet connection and try again.")
                            .setNeutralButton("Try Again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveUser();
                                }
                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            saveUser();
                        }
                    }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();
                }
            }
        });
    }


    private void next()
    {
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
            findViewById(R.id.exitButton).setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
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
}
