package com.example.gilad.fp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.example.gilad.fp.utils.Alarm;
import com.example.gilad.fp.utils.AutoResizeTextView;
import com.example.gilad.fp.utils.SendService;
import com.example.gilad.fp.utils.Vals;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

public class AlarmSetActivity extends AppCompatActivity {

    ProgressDialog dialog;
    ParseUser user;
    int stage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set);

        Intent intent = getIntent();

        stage = intent.getIntExtra("stage", 0);
        Vals.Types type = (Vals.Types) intent.getSerializableExtra(getString(R.string.pass_type));

        String parseName = "";
        if (type != null) {
            switch (type) {
                case LIST:
                    parseName = "List";
                    break;
                case TRIPLE_STORY:
                    parseName = "Story";
                    break;
                case PATTERN:
                    parseName = "Pattern";
                    break;
                case PIN:
                    parseName = "Pin";
                    break;
            }
        }


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        AutoResizeTextView textView = (AutoResizeTextView) findViewById(R.id.exit_message);

        user = ParseUser.getCurrentUser();
        ArrayList<String> stringData = intent.getStringArrayListExtra(getString(R.string.log_data));
        ArrayList<Boolean> successData  = (ArrayList<Boolean>) intent.getSerializableExtra(getString(R.string.success_data));
        ArrayList<Boolean> forgotData  = (ArrayList<Boolean>) intent.getSerializableExtra(getString(R.string.forgot_data));
        ArrayList<Long> timeData  = (ArrayList<Long>) intent.getSerializableExtra(getString(R.string.time_data));


        if (stringData != null && successData != null && forgotData != null && timeData != null)
        {
            for (int i = 0; i < stringData.size() ; i++)
            {
                ParseObject toAdd = ParseObject.create(parseName);
                try {
                    toAdd.put("actions", new JSONArray(stringData.get(i)));
                }
                catch (Exception e)
                {
                    Log.d("Debug", "WTF?");
                }
                toAdd.put("correct", successData.get(i).booleanValue());
                toAdd.put("forgot", forgotData.get(i).booleanValue());
                toAdd.put("time", timeData.get(i).longValue());
                toAdd.put("user", user);
                toAdd.saveEventually();
            }
        }

        SharedPreferences preferences = getSharedPreferences(getString(R.string.stage_file), MODE_PRIVATE);
        boolean second = preferences.getBoolean(getString(R.string.finished), false);

        if (stage < Vals.STAGES - 1) {
            int gap = Vals.GAP[stage];

            String unit = "day";

            if (gap != 1) {
                unit += "s";
            }

            int overall = 0;

            for (int i = stage; i < Vals.STAGES - 1; i++)
            {
                overall += Vals.GAP[i];
            }

            if(!second)
            {
                //Extra day between input types.
                overall++;
                for (int i = 0; i < Vals.STAGES - 1; i++)
                {
                    overall += Vals.GAP[i];
                }
            }

            String overallUnit = "day";
            if (overall != 1)
            {
                overallUnit += "s";
            }


            String text = String.format("Excellent!\n" +
                    "There are %d %s left to complete the experiment.\n" +
                    "We will prompt you again in %d %s.", overall, overallUnit, gap, unit);

            textView.setText(text);

            //TODO: Change to actual time.
            long nextAlarm = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);

            SharedPreferences.Editor editor = preferences.edit();

            editor.putInt(getString(R.string.stage), stage + 1);
            editor.putLong(getString(R.string.next_alarm), nextAlarm);

            editor.commit();

            Alarm.set(this, nextAlarm);
        }

        else if (!second)
        {
            SharedPreferences.Editor editor = preferences.edit();
            long nextAlarm = 0;
            String text;

            if (!preferences.getBoolean(getString(R.string.second), false))
            {
                //TODO: Change to actual time.
                nextAlarm = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);
                editor.putInt(getString(R.string.stage), 0);
                editor.putBoolean(getString(R.string.second), true);
                Alarm.set(this, nextAlarm);
                text = "Congratulations!\n" +
                        "You finished half of the experiment.\n" +
                        "In 1 day we will prompt you to repeat the experiment with a different type of code.\n" +
                        "User ID: " + user.getObjectId();
            }
            else
            {
                text = "Thank you for participating!\n" +
                        "You’ve completed the experiments.\n" +
                        "Please send your data.\n" +
                        "User ID: " + user.getObjectId();
                editor.putInt(getString(R.string.stage), stage + 1);
                Button button = (Button) findViewById(R.id.button);
                button.setText("Send");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog = ProgressDialog.show(AlarmSetActivity.this, "Sending", "Please Wait");
                        finalSave("List");
                    }
                });
            }

            textView.setText(text);

            editor.putLong(getString(R.string.next_alarm), nextAlarm);

            editor.commit();
            getSharedPreferences(getString(R.string.filename), MODE_PRIVATE).edit().clear().commit();
        }

        else
        {
            textView.setText("Congratulations!\n" +
                    "You've completed the experiment.\n" +
                    "User ID: " + user.getObjectId());
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

    @Override
    protected void onDestroy() {
        startService(new Intent(this, SendService.class));
        Log.d("Debug", "Peace Out!");
        super.onDestroy();
    }

    private void finalSave(final String title) {
        Log.d("Debug", "Inside");
        ParseQuery<ParseObject> query = ParseQuery.getQuery(title);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        finalError(title);
                    } else {
                        switch (title) {
                            case "List":
                                finalSave("Story");
                                break;
                            case "Story":
                                finalSave("Pattern");
                                break;
                            case "Pattern":
                                finalSave("Pin");
                                break;
                            case "Pin":
                                userSave();
                                break;
                        }
                    }
                } else {
                    finalError(title);
                }
            }
        });
    }

    private void finalError(final String title)
    {
        if (dialog != null) {
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage("An error has occurred.\n" +
                        "Make sure you have an internet connection and try again.")
                .setNeutralButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlarmSetActivity.this.dialog = ProgressDialog.show(AlarmSetActivity.this, "Sending", "Please Wait");
                        finalSave(title);
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                AlarmSetActivity.this.dialog = ProgressDialog.show(AlarmSetActivity.this, "Sending", "Please Wait");
                finalSave(title);
            }
        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    private void userSave() {
        Log.d("Debug", "Here");
        user.put("finished", true);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    userError();
                }
                else
                {
                    getSharedPreferences(getString(R.string.stage_file), MODE_PRIVATE)
                            .edit().putBoolean(getString(R.string.finished), true).commit();
                    dialog.dismiss();
                    ((TextView) findViewById(R.id.exit_message)).setText("Congratulations!\nYou've completed the experiment.");
                    findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                    ((Button) findViewById(R.id.button)).setText("Exit");
                }
            }
        });

    }

    private void userError()
    {
        if (dialog != null) {
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage("An error has occurred.\n" +
                        "Make sure you have an internet connection and try again.")
                .setNeutralButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlarmSetActivity.this.dialog = ProgressDialog.show(AlarmSetActivity.this, "Sending", "Please Wait");
                        userSave();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                AlarmSetActivity.this.dialog = ProgressDialog.show(AlarmSetActivity.this, "Sending", "Please Wait");
                userSave();
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
