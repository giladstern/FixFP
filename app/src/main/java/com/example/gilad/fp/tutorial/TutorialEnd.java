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

public class TutorialEnd extends AppCompatActivity {

    Vals.Types type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_end);

        type= (Vals.Types) getIntent().getSerializableExtra(getString(R.string.pass_type));

        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                switch(type)
                {
                    case LIST:
                        intent = new Intent(TutorialEnd.this, ListActivity.class);
                        break;
                    case TRIPLE_STORY:
                        intent = new Intent(TutorialEnd.this, StoryActivity.class);
                        break;
                    case PIN:
                        intent = new Intent(TutorialEnd.this, PinActivity.class);
                        break;
                    case PATTERN:
                        intent = new Intent(TutorialEnd.this, PatternActivity.class);
                        break;
                }

                intent.putExtra(getString(R.string.stage), 0);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutorial_end, menu);
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
