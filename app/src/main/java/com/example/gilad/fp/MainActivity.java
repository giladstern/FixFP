package com.example.gilad.fp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

        int passType = prefs.getInt(getString(R.string.pass_type), -1);

        switch (passType)
        {
            case Vals.list:
                listOnClick(null);
                break;
            case Vals.story:
                storyOnClick(null);
                break;
            case Vals.pattern:
                patternOnClick(null);
                break;
            case Vals.pin:
                pinOnClick(null);
                break;
            case -1:
                prefs.edit().putString("char0", "").commit();
        }
    }

    public void listOnClick(View v)
    {
        getSharedPreferences(getString(R.string.filename), MODE_PRIVATE).edit().putInt(getString(R.string.pass_type), Vals.list).commit();
        Intent next = new Intent(this, ListActivity.class);
        startActivity(next);
        finish();
    }

    public void storyOnClick(View v)
    {
        getSharedPreferences(getString(R.string.filename), MODE_PRIVATE).edit().putInt(getString(R.string.pass_type), Vals.story).commit();
        Intent next = new Intent(this, StoryActivity.class);
        startActivity(next);
        finish();
    }

    public void patternOnClick(View v)
    {
        getSharedPreferences(getString(R.string.filename), MODE_PRIVATE).edit().putInt(getString(R.string.pass_type), Vals.pattern).commit();
        Intent next = new Intent(this, PatternActivity.class);
        startActivity(next);
        finish();
    }

    public void pinOnClick(View v)
    {
        getSharedPreferences(getString(R.string.filename), MODE_PRIVATE).edit().putInt(getString(R.string.pass_type), Vals.pin).commit();
        Intent next = new Intent(this, PinActivity.class);
        startActivity(next);
        finish();
    }
}
