package com.example.gilad.fp.tutorial;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gilad.fp.PassGenerate;
import com.example.gilad.fp.R;
import com.example.gilad.fp.StoryActivity;
import com.example.gilad.fp.utils.DiagonalStoryFP;
import com.example.gilad.fp.utils.FastPhrase;
import com.example.gilad.fp.utils.Overlay;
import com.example.gilad.fp.utils.TouchData;
import com.example.gilad.fp.utils.Vals;

import java.util.ArrayList;

public class StoryTutorial extends AppCompatActivity {

    Vals.Types type = Vals.Types.TRIPLE_STORY;
    String password[] = new String[6];
    String labels[] = new String[6];
    TextView topInstructions;

    DiagonalStoryFP fp;
    RelativeLayout layout;
    float scale;
    DiagonalStoryFP.Batch batch = DiagonalStoryFP.Batch.FIRST;
    Typeface emojiFont;
    Typeface characterFont;
    Typeface buildingFont;
    boolean prevEqual;
    boolean consecEqual;
    int timesEqual = 0;
    Overlay overlay;
    boolean prevWrong = false;
    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_tutorial);

        res = getResources();
        Intent passIntent = new Intent(this, PassGenerate.class);
        passIntent.putExtra(getString(R.string.pass_type), type);
        passIntent.putExtra(getString(R.string.generate), getIntent().getBooleanExtra(getString(R.string.generate), true));
        passIntent.putExtra(getString(R.string.message), "Your code is comprised of the following symbols. " +
                "Note that the symbols make up a story. " +
                "For now, take a look at it, but don’t try to memorize it.");
        startActivity(passIntent);

        scale = getResources().getDisplayMetrics().density;
        emojiFont = Typeface.createFromAsset(getAssets(), "fonts/androidemoji.ttf");
        characterFont = Typeface.createFromAsset(getAssets(), "fonts/cartoon.ttf");
        buildingFont = Typeface.createFromAsset(getAssets(), "fonts/FamousBuildings.ttf");

        topInstructions = (TextView) findViewById(R.id.top_instructions);

        fp = (DiagonalStoryFP) findViewById(R.id.story_fp);
        layout = (RelativeLayout) findViewById(R.id.layout);
        overlay = (Overlay) findViewById(R.id.overlay);

        fp.setOnCompleteListener(new FastPhrase.OnCompleteListener() {
            @Override
            public void onComplete(String[] pass, ArrayList<TouchData> touchLog) {
                boolean equal = true;
                for (int i = 0 ; i < 6 ; i++)
                {
                    if (!(pass[i].equals(password[i])))
                    {
                        equal = false;
                        break;
                    }
                }

                if(equal)
                {
                    if (batch == DiagonalStoryFP.Batch.FIRST)
                    {
                        batch = DiagonalStoryFP.Batch.SECOND;

                    }

                    else if (batch == DiagonalStoryFP.Batch.SECOND)
                    {
                        batch = DiagonalStoryFP.Batch.DONE;
                        overlay.off();
                        fp.setOnFirstCompleteListener(null);
                        fp.setOnSecondCompleteListener(null);
                        midwayAlert();
                        return;
                    }

                    else
                    {
                        if(prevEqual)
                        {
                            consecEqual = true;
                        }
                        prevEqual = true;
                        prevWrong = false;
                        timesEqual++;
                    }

                }

                else
                {
                    prevEqual = false;
                    if (batch == DiagonalStoryFP.Batch.DONE)
                    {
                        prevWrong = true;
                    }
                }

                alert(equal);
            }
        });

        fp.setOnFirstCompleteListener(new DiagonalStoryFP.OnFirstCompleteListener() {
            @Override
            public boolean onFirstComplete(String[] pass) {
                secondScreenHighlight();
                return true;
            }
        });

        fp.setOnSecondCompleteListener(new DiagonalStoryFP.OnSecondCompleteListener() {
            @Override
            public boolean onSecondComplete(String[] pass) {
                firstScreenHighlight();
                return true;
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

                SharedPreferences prefs = getSharedPreferences(getString(R.string.filename), MODE_PRIVATE);
                for (int i = 0 ; i < 6 ; i++)
                {
                    password[i] = prefs.getString(String.format("story_pass%d", i), "");
                    labels[i] = prefs.getString(String.format("story_label%d", i), "");
                }

                topInstructions.setText("Enter your code by tapping the highlighted symbols.");
                firstScreenHighlight();
                break;
            case SECOND:
                topInstructions.setText("You can also enter your code by dragging your finger across the highlighted symbols.\n" +
                        "Give it a try!");
                firstScreenHighlight();
                break;
            case DONE:
                topInstructions.setText("Try entering your code (without assistance). " +
                        "If you don’t remember the code, give it your best shot.");
                if (consecEqual && timesEqual >= 3)
                {
                    startActivity(new Intent(this, StoryActivity.class));
                    finish();
                }
                break;
        }
    }

    private void alert(boolean success)
    {
        String title;
        String message;
        if (success) {
            title = getString(R.string.correct);
            if (consecEqual && timesEqual >= 3 || batch != DiagonalStoryFP.Batch.DONE) {
                message = getString(R.string.ok);
            }
            else {
                message = getString(R.string.again);
            }
        } else {
            title = getString(R.string.incorrect);
            message = getString(R.string.retry);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setNeutralButton(message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResume();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onResume();
            }
        });
        if (!success && batch == DiagonalStoryFP.Batch.DONE)
        {
            builder.setPositiveButton(getString(R.string.forgot), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(StoryTutorial.this, PassGenerate.class);
                    intent.putExtra(getString(R.string.generate), false);
                    intent.putExtra(getString(R.string.pass_type), Vals.Types.TRIPLE_STORY);
                    startActivity(intent);
                }
            });
        }
        builder.show();
    }

    private void midwayAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.correct))
                .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResume();

                        Intent intent = new Intent(StoryTutorial.this, PassGenerate.class);
                        intent.putExtra(getString(R.string.generate), false);
                        intent.putExtra(getString(R.string.pass_type), Vals.Types.TRIPLE_STORY);
                        intent.putExtra(getString(R.string.message), "Here is your code once more. " +
                                "This time try remembering it, but don’t write it down. " +
                                "If you forget your code, we will remind you.\n" +
                                "Note: Visualizing the suggested story might help you remember the code.");
                        startActivity(intent);
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onResume();
                Intent intent = new Intent(StoryTutorial.this, PassGenerate.class);
                intent.putExtra(getString(R.string.generate), false);
                intent.putExtra(getString(R.string.pass_type), Vals.Types.TRIPLE_STORY);
                intent.putExtra(getString(R.string.message), "Here is your code once more. " +
                        "This time try remembering it, but don’t write it down. " +
                        "If you forget your code, we will remind you.\n" +
                        "Note: Visualizing the suggested story might help you remember the code.");
                startActivity(intent);

            }
        });
        builder.show();
    }

    private void firstScreenHighlight()
    {
        int topId = 0, middleId = 0, bottomId = 0;
        String[] top = res.getStringArray(R.array.cartoons);
        int[] middle = res.getIntArray(R.array.vehicles);
        String[] bottom = res.getStringArray(R.array.locations);

        for (int i = 0; i < 5; i++) {
            if (top[i].equals(password[0])) {
                topId = i;
            }
            if (new String(Character.toChars(middle[i])).equals(password[1])) {
                middleId = i;
            }
            if (bottom[i].equals(password[2])) {
                bottomId = i;
            }
        }
        overlay.highlight(topId, middleId, bottomId);
    }

    private void secondScreenHighlight()
    {
        int topId = 0, middleId = 0, bottomId = 0;
        String[] top = res.getStringArray(R.array.amounts);
        int[] middle = res.getIntArray(R.array.animals);
        int[] bottom = res.getIntArray(R.array.food);

        for (int i = 0; i < 5; i++) {
            if (top[i].equals(password[3])) {
                topId = i;
            }
            if (new String(Character.toChars(middle[i])).equals(password[4])) {
                middleId = i;
            }
            if (new String(Character.toChars(bottom[i])).equals(password[5])) {
                bottomId = i;
            }
        }
        overlay.highlight(topId, middleId, bottomId);
    }

}
