package com.example.gilad.fp.tutorial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.gilad.fp.ListActivity;
import com.example.gilad.fp.PatternActivity;
import com.example.gilad.fp.PinActivity;
import com.example.gilad.fp.R;
import com.example.gilad.fp.StoryActivity;
import com.example.gilad.fp.utils.Vals;

import java.util.List;

public class LoopActivity extends AppCompatActivity {

    Vals.Types type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loop);

        type = (Vals.Types) getIntent().getSerializableExtra(getString(R.string.pass_type));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loop, menu);
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

    public void backOnClick(View v)
    {
        Intent intent = null;
        switch(type)
        {
            case LIST:
                intent = new Intent(this, ListTutorial.class);
                break;
            case TRIPLE_STORY:
                intent = new Intent(this, StoryTutorial.class);
                break;
            case PIN:
                intent = new Intent(this, PinTutorial.class);
                break;
            case PATTERN:
                intent = new Intent(this, PatternTutorial.class);
                break;
        }

        intent.putExtra(getString(R.string.generate), false);
        startActivity(intent);
        finish();
    }

    public void nextOnClick(View v)
    {
        Intent intent = new Intent(this, TutorialEnd.class);

        intent.putExtra(getString(R.string.pass_type), type);
        startActivity(intent);
        finish();
    }
}
