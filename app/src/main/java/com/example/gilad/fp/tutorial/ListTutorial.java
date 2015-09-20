package com.example.gilad.fp.tutorial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gilad.fp.DispatchActivity;
import com.example.gilad.fp.MainActivity;
import com.example.gilad.fp.PassGenerate;
import com.example.gilad.fp.R;
import com.example.gilad.fp.utils.FastPhrase;
import com.example.gilad.fp.utils.Vals;
import com.example.gilad.fp.utils.WideNoLinesFP;

import java.lang.reflect.Type;
import java.util.List;

public class ListTutorial extends AppCompatActivity {

    Vals.Types type = Vals.Types.LIST;

    String password[] = new String[6];
    String labels[] = new String[6];
    TextView topInstructions;
    TextView leftInstructions;
    TextView rightInstructions;
    TextView rightLabel;
    WideNoLinesFP fp;
    RelativeLayout layout;
    float scale;
    WideNoLinesFP.Batch batch = WideNoLinesFP.Batch.FIRST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tutorial);

        getSharedPreferences(getString(R.string.filename), MODE_PRIVATE).edit().putString("char0", "").commit();

        Intent passIntent = new Intent(this, PassGenerate.class);
        passIntent.putExtra("type", type);
        startActivity(passIntent);

        scale = getResources().getDisplayMetrics().density;
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/androidemoji.ttf");

        topInstructions = (TextView) findViewById(R.id.top_instructions);
        leftInstructions = (TextView) findViewById(R.id.left_instructions);
        rightInstructions = (TextView) findViewById(R.id.right_instructions);
        rightLabel = (TextView) findViewById(R.id.right_label);
        fp = (WideNoLinesFP) findViewById(R.id.list_fp);
        layout = (RelativeLayout) findViewById(R.id.layout);

        rightInstructions.setTypeface(font);

        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = (layout.getWidth() - layout.getPaddingLeft() - layout.getPaddingRight() )/ 2;
                leftInstructions.getLayoutParams().width = width;
                rightInstructions.getLayoutParams().width = width;
                rightLabel.getLayoutParams().width = width;

            }
        });

        fp.setOnFirstCompleteListener(new WideNoLinesFP.OnFirstCompleteListener() {
            @Override
            public boolean onFirstComplete(String[] pass) {
                Intent next = new Intent(ListTutorial.this, TutorialSuccess.class);
                if (password[0].equals(pass[0]) && password[1].equals(pass[1])) {
                    next.putExtra(getString(R.string.success), true);
                    startActivity(next);
                    batch = WideNoLinesFP.Batch.SECOND;
                    return true;
                } else {
                    next.putExtra(getString(R.string.success), false);
                    startActivity(next);
                    return false;
                }
            }
        });

        fp.setOnSecondCompleteListener(new WideNoLinesFP.OnSecondCompleteListener() {
            @Override
            public boolean onSecondComplete(String[] pass) {
                Intent next = new Intent(ListTutorial.this, TutorialSuccess.class);
                if (password[2].equals(pass[0]) && password[3].equals(pass[1])) {
                    next.putExtra(getString(R.string.success), true);
                    startActivity(next);
                    batch = WideNoLinesFP.Batch.THIRD;
                    return true;
                } else {
                    next.putExtra(getString(R.string.success), false);
                    startActivity(next);
                    return false;
                }
            }
        });

        fp.setOnThirdCompleteListener(new WideNoLinesFP.OnThirdCompleteListener() {
            @Override
            public boolean onThirdComplete(String[] pass) {
                Intent next = new Intent(ListTutorial.this, TutorialSuccess.class);
                if (password[4].equals(pass[0]) && password[5].equals(pass[1])) {
                    next.putExtra(getString(R.string.success), true);
                    startActivity(next);
                    batch = WideNoLinesFP.Batch.DONE;
                    return true;
                } else {
                    next.putExtra(getString(R.string.success), false);
                    startActivity(next);
                    return false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_tutorial, menu);
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
        switch(batch)
        {
            case FIRST:
                if (password[0] == null || password[0].equals(""))
                {
                    SharedPreferences prefs = getSharedPreferences(getString(R.string.filename), MODE_PRIVATE);
                    for (int i = 0 ; i < 6 ; i++)
                    {
                        password[i] = prefs.getString(String.format("char%d", i), "");
                        labels[i] = prefs.getString(String.format("label%d", i), "");
                    }
                }

                topInstructions.getLayoutParams().height =((int) (100 * scale + 0.5f));
                topInstructions.setText("Try tapping:");
                leftInstructions.setText(password[0]);
                rightInstructions.setText(password[1]);
                rightLabel.setText(labels[1]);
                break;
            case SECOND:
                topInstructions.getLayoutParams().height = ((int) (200 * scale + 0.5f));
                topInstructions.setText("You can also enter your code without lifting your finger.\nTry dragging your finger across:");
                leftInstructions.setText(password[2]);
                rightInstructions.setText(password[3]);
                rightLabel.setText(labels[3]);
                break;
            case THIRD:
                topInstructions.getLayoutParams().height =((int) (100 * scale + 0.5f));
                topInstructions.setText("Use either method to enter:");
                leftInstructions.setText(password[4]);
                rightInstructions.setText(password[5]);
                rightLabel.setText(labels[5]);
                break;
            case DONE:
                finish();
                break;
        }
    }
}
