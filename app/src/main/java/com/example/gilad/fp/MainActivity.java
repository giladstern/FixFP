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
        Intent next = new Intent(this, FirstScreen.class);
        next.putExtra(getString(R.string.pass_type), Vals.Types.LIST);
        startActivity(next);
        finish();
    }

    public void storyOnClick(View v)
    {
        getSharedPreferences(getString(R.string.stage_file), MODE_PRIVATE).edit().putInt(getString(R.string.order), DispatchActivity.STORY_LIST).commit();
        Intent next = new Intent(this, FirstScreen.class);
        next.putExtra(getString(R.string.pass_type), Vals.Types.TRIPLE_STORY);
        startActivity(next);
        finish();
    }

    public void patternOnClick(View v)
    {
        getSharedPreferences(getString(R.string.stage_file), MODE_PRIVATE).edit().putInt(getString(R.string.order), DispatchActivity.PATTERN_LIST).commit();
        Intent next = new Intent(this, FirstScreen.class);
        next.putExtra(getString(R.string.pass_type), Vals.Types.PATTERN);
        startActivity(next);
        finish();
    }

    public void pinOnClick(View v)
    {
        getSharedPreferences(getString(R.string.stage_file), MODE_PRIVATE).edit().putInt(getString(R.string.order), DispatchActivity.PIN_LIST).commit();
        Intent next = new Intent(this, FirstScreen.class);
        next.putExtra(getString(R.string.pass_type), Vals.Types.PIN);
        startActivity(next);
        finish();
    }
}
