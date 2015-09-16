package com.example.gilad.fp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class SuccMsg extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_succ_msg);
        boolean succCode = getIntent().getBooleanExtra("succCode", false);
        long time = getIntent().getLongExtra("time", 0);
        Float timeInSecs = time / 1000.0f;

        if (succCode)
        {
            ((AutoResizeTextView) findViewById(R.id.succText)).setText("Correct");
            ((AutoResizeTextView) findViewById(R.id.succText)).setTextColor(Color.GREEN);
            ((AutoResizeTextView) findViewById(R.id.time)).setText("Your time is: " + timeInSecs.toString() + "s");
            ((Button) findViewById(R.id.backButton)).setText("Back");
        }
        else
        {
            ((AutoResizeTextView) findViewById(R.id.succText)).setText("Incorrect");
            ((AutoResizeTextView) findViewById(R.id.succText)).setTextColor(Color.RED);
            ((Button) findViewById(R.id.backButton)).setText("Back");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_succ_msg, menu);
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

    public void buttonOnClick(View v)
    {
        finish();
    }
}
