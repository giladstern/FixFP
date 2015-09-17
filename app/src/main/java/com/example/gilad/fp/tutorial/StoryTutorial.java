package com.example.gilad.fp.tutorial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gilad.fp.MainActivity;
import com.example.gilad.fp.PassGenerate;
import com.example.gilad.fp.R;
import com.example.gilad.fp.utils.DiagonalStoryFP;
import com.example.gilad.fp.utils.WideNoLinesFP;

public class StoryTutorial extends AppCompatActivity {

    String password[] = new String[6];
    String labels[] = new String[6];
    TextView topInstructions;
    TextView leftInstructions;
    TextView middleInstructions;
    TextView rightInstructions;
    TextView leftLabel;
    TextView middleLabel;
    TextView rightLabel;
    DiagonalStoryFP fp;
    RelativeLayout layout;
    float scale;
    DiagonalStoryFP.Batch batch = DiagonalStoryFP.Batch.FIRST;
    Typeface emojiFont;
    Typeface characterFont;
    Typeface buildingFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_tutorial);

        Intent passIntent = new Intent(this, PassGenerate.class);
        passIntent.putExtra("type", MainActivity.Types.TRIPLE_STORY);
        startActivity(passIntent);

        scale = getResources().getDisplayMetrics().density;
        emojiFont = Typeface.createFromAsset(getAssets(), "fonts/androidemoji.ttf");
        characterFont = Typeface.createFromAsset(getAssets(), "fonts/cartoon.ttf");
        buildingFont = Typeface.createFromAsset(getAssets(), "fonts/FamousBuildings.ttf");

        topInstructions = (TextView) findViewById(R.id.top_instructions);
        leftInstructions = (TextView) findViewById(R.id.left_instructions);
        middleInstructions = (TextView) findViewById(R.id.middle_instructions);
        rightInstructions = (TextView) findViewById(R.id.right_instructions);
        leftLabel = (TextView) findViewById(R.id.left_label);
        middleLabel = (TextView) findViewById(R.id.middle_label);
        rightLabel = (TextView) findViewById(R.id.right_label);
        fp = (DiagonalStoryFP) findViewById(R.id.story_fp);
        layout = (RelativeLayout) findViewById(R.id.layout);

        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = (layout.getWidth() - layout.getPaddingLeft() - layout.getPaddingRight() )/ 3;
                leftInstructions.getLayoutParams().width = width;
                middleInstructions.getLayoutParams().width = width;
                rightInstructions.getLayoutParams().width = width;
                leftLabel.getLayoutParams().width = width;
                middleLabel.getLayoutParams().width = width;
                rightLabel.getLayoutParams().width = width;

            }
        });

        fp.setOnFirstCompleteListener(new DiagonalStoryFP.OnFirstCompleteListener() {
            @Override
            public boolean onFirstComplete(String[] pass) {
                Intent next = new Intent(StoryTutorial.this, TutorialSuccess.class);
                if (password[0].equals(pass[0]) && password[1].equals(pass[1]) && password[2].equals(pass[2])) {
                    next.putExtra(getString(R.string.success), true);
                    startActivity(next);
                    batch = DiagonalStoryFP.Batch.SECOND;
                    return true;
                } else {
                    next.putExtra(getString(R.string.success), false);
                    startActivity(next);
                    return false;
                }
            }
        });

        fp.setOnSecondCompleteListener(new DiagonalStoryFP.OnSecondCompleteListener() {
            @Override
            public boolean onSecondComplete(String[] pass) {
                Intent next = new Intent(StoryTutorial.this, TutorialSuccess.class);
                if (password[3].equals(pass[0]) && password[4].equals(pass[1]) && password[5].equals(pass[2])) {
                    next.putExtra(getString(R.string.success), true);
                    startActivity(next);
                    batch = DiagonalStoryFP.Batch.DONE;
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
                if (password[0] == null)
                {
                    SharedPreferences prefs = getSharedPreferences(getString(R.string.filename), MODE_PRIVATE);
                    for (int i = 0 ; i < 6 ; i++)
                    {
                        password[i] = prefs.getString(String.format("char%d", i), "");
                        labels[i] = prefs.getString(String.format("label%d", i), "");
                    }
                }

                topInstructions.getLayoutParams().height =((int) (75 * scale + 0.5f));
                topInstructions.setText("Try tapping:");
                leftInstructions.setText(password[0]);
                middleInstructions.setText(password[1]);
                rightInstructions.setText(password[2]);
                leftLabel.setText(labels[0]);
                middleLabel.setText(labels[1]);
                rightLabel.setText(labels[2]);
                leftInstructions.setTypeface(characterFont);
                middleInstructions.setTypeface(emojiFont);
                rightInstructions.setTypeface(buildingFont);
                break;
            case SECOND:
                topInstructions.getLayoutParams().height = ((int) (150 * scale + 0.5f));
                topInstructions.setText("You can also enter your code without lifting your finger.\nTry dragging your finger across:");
                leftInstructions.setText(password[3]);
                middleInstructions.setText(password[4]);
                rightInstructions.setText(password[5]);
                leftLabel.setText(labels[3]);
                middleLabel.setText(labels[4]);
                rightLabel.setText(labels[5]);
                leftInstructions.setTypeface(Typeface.DEFAULT);
                middleInstructions.setTypeface(emojiFont);
                rightInstructions.setTypeface(emojiFont);
                break;
//            case THIRD:
//                topInstructions.getLayoutParams().height =((int) (100 * scale + 0.5f));
//                topInstructions.setText("Use either method to enter:");
//                leftInstructions.setText(password[4]);
//                rightInstructions.setText(password[5]);
//                rightLabel.setText(labels[5]);
//                break;
            case DONE:
                finish();
                break;
        }
    }
}
