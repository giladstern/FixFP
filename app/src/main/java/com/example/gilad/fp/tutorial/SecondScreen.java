package com.example.gilad.fp.tutorial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.gilad.fp.R;
import com.example.gilad.fp.utils.Vals;

public class SecondScreen extends AppCompatActivity {

    private Vals.Types nextType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);
        nextType = (Vals.Types) getIntent().getSerializableExtra(getString(R.string.pass_type));

        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = null;
                switch (nextType) {
                    case TRIPLE_STORY:
                        next = new Intent(SecondScreen.this, StoryTutorial.class);
                        break;
                    case LIST:
                        next = new Intent(SecondScreen.this, ListTutorial.class);
                        break;
                    case PIN:
                        next = new Intent(SecondScreen.this, PinTutorial.class);
                        break;
                    case PATTERN:
                        next = new Intent(SecondScreen.this, PatternTutorial.class);
                        break;
                }

                next.putExtra(getString(R.string.generate), true);

                startActivity(next);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second_screen, menu);
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
