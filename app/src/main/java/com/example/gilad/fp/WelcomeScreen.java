package com.example.gilad.fp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.gilad.fp.tutorial.ListTutorial;
import com.example.gilad.fp.tutorial.PatternTutorial;
import com.example.gilad.fp.tutorial.PinTutorial;
import com.example.gilad.fp.tutorial.StoryTutorial;
import com.example.gilad.fp.utils.Vals;

import java.util.List;

public class WelcomeScreen extends AppCompatActivity {

    private Vals.Types nextType;
    private int stage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        nextType = (Vals.Types) getIntent().getSerializableExtra("type");
        stage = getIntent().getIntExtra(getString(R.string.stage), 0);
        //TODO: Add time incentive calculation.

        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = null;
                switch (nextType)
                {
                    case TRIPLE_STORY:
                        next = new Intent(WelcomeScreen.this, StoryActivity.class);
                        break;
                    case LIST:
                        next = new Intent(WelcomeScreen.this, ListActivity.class);
                        break;
                    case PIN:
                        next = new Intent(WelcomeScreen.this, PinActivity.class);
                        break;
                    case PATTERN:
                        next = new Intent(WelcomeScreen.this, PatternActivity.class);
                        break;
                }

                next.putExtra(getString(R.string.stage), stage);
                startActivity(next);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome_screen, menu);
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
}
