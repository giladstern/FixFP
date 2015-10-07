package com.example.gilad.fp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.example.gilad.fp.utils.Alarm;
import com.example.gilad.fp.utils.AutoResizeTextView;
import com.example.gilad.fp.utils.Vals;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

public class AlarmSetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set);

        Intent intent = getIntent();

        int stage = intent.getIntExtra("stage", 0);
        Vals.Types type = (Vals.Types) intent.getSerializableExtra(getString(R.string.pass_type));

        String parseName = "";
        switch (type)
        {
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

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        AutoResizeTextView textView = (AutoResizeTextView) findViewById(R.id.exit_message);

        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<String> stringData = intent.getStringArrayListExtra(getString(R.string.log_data));
        ArrayList<Boolean> successData  = (ArrayList<Boolean>) intent.getSerializableExtra(getString(R.string.success_data));
        ArrayList<Boolean> forgotData  = (ArrayList<Boolean>) intent.getSerializableExtra(getString(R.string.forgot_data));
        ArrayList<Long> timeData  = (ArrayList<Long>) intent.getSerializableExtra(getString(R.string.time_data));


        if (stringData.size() == successData.size() &&
                successData.size() == forgotData.size() &&
                forgotData.size() == timeData.size())
        {
            Log.d("Debug", "AwesomeSauce");
            ArrayList<ParseObject> list = new ArrayList<>();
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
                list.add(toAdd);
            }
            ParseObject.saveAllInBackground(list);
        }

        else
        {
            Log.d("Debug", String.format("String: %d, Success: %d, Forgot: %d, Time: %d",
                    stringData.size(), successData.size(), forgotData.size(), timeData.size()));
        }

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

            String overallUnit = "day";
            if (overall != 1)
            {
                overallUnit += "s";
            }


            String text = String.format("There are %d %s left to complete the experiment.\n" +
                    "We will prompt you again in %d %s.", overall, overallUnit, gap, unit);

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
            SharedPreferences prefs = getSharedPreferences(getString(R.string.stage_file), MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(getString(R.string.stage), 0);
            long nextAlarm = 0;
            String text;

            if (!prefs.getBoolean(getString(R.string.second), false))
            {
                nextAlarm = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1);
                editor.putBoolean(getString(R.string.second), true);
                Alarm.set(this, nextAlarm);
                text = "Congratulations!\n" +
                        "You finished half of the experiment.\n" +
                        "In 1 day we will prompt you to repeat the experiment with a different type of code.";
            }
            else
            {
                editor.putInt(getString(R.string.order), -1);
                editor.putBoolean(getString(R.string.second), false);
                text = "Thank you for participating!\n" +
                        "You’ve completed the experiments. (INSTRUCTIONS?)\n";
            }

            textView.setText(text);

            editor.putLong(getString(R.string.next_alarm), nextAlarm);

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
