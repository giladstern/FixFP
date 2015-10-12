package com.example.gilad.fp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.gilad.fp.tutorial.FirstScreen;
import com.example.gilad.fp.utils.Vals;

public class MainActivity extends AppCompatActivity {


    private Vals.Types type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    }

    public void listOnClick(View v)
    {
        getSharedPreferences(getString(R.string.stage_file), MODE_PRIVATE).edit().putInt(getString(R.string.order), DispatchActivity.LIST_PATTERN).commit();
        nextScreen(Vals.Types.LIST);
    }

    public void storyOnClick(View v)
    {
        getSharedPreferences(getString(R.string.stage_file), MODE_PRIVATE).edit().putInt(getString(R.string.order), DispatchActivity.STORY_LIST).commit();
        nextScreen(Vals.Types.TRIPLE_STORY);
    }

    public void patternOnClick(View v)
    {
        getSharedPreferences(getString(R.string.stage_file), MODE_PRIVATE).edit().putInt(getString(R.string.order), DispatchActivity.PATTERN_LIST).commit();
        nextScreen(Vals.Types.PATTERN);
    }

    public void pinOnClick(View v)
    {
        getSharedPreferences(getString(R.string.stage_file), MODE_PRIVATE).edit().putInt(getString(R.string.order), DispatchActivity.PIN_LIST).commit();
        nextScreen(Vals.Types.PIN);
    }

    private void nextScreen(Vals.Types type)
    {
        Intent next = null;
        SharedPreferences preferences = getSharedPreferences(getString(R.string.filename), MODE_PRIVATE);
        String pass = "";
        switch (type)
        {
            case LIST:
                pass = preferences.getString("list_pass0", "");
                break;
            case TRIPLE_STORY:
                pass = preferences.getString("story_pass0", "");
                break;
            case PIN:
                pass = preferences.getString("pin_pass0", "");
                break;
            case PATTERN:
                pass = preferences.getString("pattern_pass0", "");
                break;
        }
        if (((CheckBox)findViewById(R.id.checkBox)).isChecked() || pass.equals(""))
        {
            next = new Intent(this, FirstScreen.class);
            next.putExtra(getString(R.string.pass_type), type);
            next.putExtra(getString(R.string.generate), ((CheckBox) findViewById(R.id.checkBox)).isChecked());
        }
        else
        {
            switch (type)
            {
                case LIST:
                    next = new Intent(this, ListActivity.class);
                    break;
                case TRIPLE_STORY:
                    next = new Intent(this, StoryActivity.class);
                    break;
                case PIN:
                    next = new Intent(this, PinActivity.class);
                    break;
                case PATTERN:
                    next = new Intent(this, PatternActivity.class);
                    break;
            }

            next.putExtra("skipped", true);

        }

        startActivity(next);
        finish();


    }
}
