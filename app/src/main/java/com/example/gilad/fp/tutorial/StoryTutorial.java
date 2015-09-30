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
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gilad.fp.MainActivity;
import com.example.gilad.fp.PassGenerate;
import com.example.gilad.fp.R;
import com.example.gilad.fp.StoryActivity;
import com.example.gilad.fp.utils.DiagonalStoryFP;
import com.example.gilad.fp.utils.FastPhrase;
import com.example.gilad.fp.utils.Overlay;
import com.example.gilad.fp.utils.TouchData;
import com.example.gilad.fp.utils.Vals;
import com.example.gilad.fp.utils.WideNoLinesFP;

import java.util.ArrayList;

public class StoryTutorial extends AppCompatActivity {

    Vals.Types type = Vals.Types.TRIPLE_STORY;
    String password[] = new String[6];
    String labels[] = new String[6];
    TextView topInstructions;
//    TextView leftInstructions;
//    TextView middleInstructions;
//    TextView rightInstructions;
//    TextView leftLabel;
//    TextView middleLabel;
//    TextView rightLabel;
    DiagonalStoryFP fp;
    RelativeLayout layout;
    float scale;
    DiagonalStoryFP.Batch screen = DiagonalStoryFP.Batch.FIRST;
    DiagonalStoryFP.Batch batch = DiagonalStoryFP.Batch.FIRST;
    Typeface emojiFont;
    Typeface characterFont;
    Typeface buildingFont;
    boolean prevEqual;
    boolean consecEqual;
    int timesEqual = 0;
    Overlay overlay;
    boolean prevWrong = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_tutorial);

        Intent passIntent = new Intent(this, PassGenerate.class);
        passIntent.putExtra(getString(R.string.pass_type), type);
        passIntent.putExtra(getString(R.string.generate), getIntent().getBooleanExtra(getString(R.string.generate), true));
        startActivity(passIntent);

        scale = getResources().getDisplayMetrics().density;
        emojiFont = Typeface.createFromAsset(getAssets(), "fonts/androidemoji.ttf");
        characterFont = Typeface.createFromAsset(getAssets(), "fonts/cartoon.ttf");
        buildingFont = Typeface.createFromAsset(getAssets(), "fonts/FamousBuildings.ttf");

        topInstructions = (TextView) findViewById(R.id.top_instructions);
//        leftInstructions = (TextView) findViewById(R.id.left_instructions);
//        middleInstructions = (TextView) findViewById(R.id.middle_instructions);
//        rightInstructions = (TextView) findViewById(R.id.right_instructions);
//        leftLabel = (TextView) findViewById(R.id.left_label);
//        middleLabel = (TextView) findViewById(R.id.middle_label);
//        rightLabel = (TextView) findViewById(R.id.right_label);
        fp = (DiagonalStoryFP) findViewById(R.id.story_fp);
        layout = (RelativeLayout) findViewById(R.id.layout);
        overlay = (Overlay) findViewById(R.id.overlay);

//        ViewTreeObserver vto = layout.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int width = (layout.getWidth() - layout.getPaddingLeft() - layout.getPaddingRight()) / 5;
//                leftInstructions.getLayoutParams().width = width;
//                middleInstructions.getLayoutParams().width = width;
//                rightInstructions.getLayoutParams().width = width;
//                leftLabel.getLayoutParams().width = width;
//                middleLabel.getLayoutParams().width = width;
//                rightLabel.getLayoutParams().width = width;
//                findViewById(R.id.left_arrow).getLayoutParams().width = width;
//                findViewById(R.id.right_arrow).getLayoutParams().width = width;
//            }
//        });

        fp.setOnFirstCompleteListener(new DiagonalStoryFP.OnFirstCompleteListener() {
            @Override
            public boolean onFirstComplete(String[] pass) {
                String message = null;
                boolean success;
                if (password[0].equals(pass[0]) && password[1].equals(pass[1]) && password[2].equals(pass[2])) {
                    success = true;
                    screen = DiagonalStoryFP.Batch.SECOND;
                } else {
                    success = false;
                    message = "Try again.";
                }
                alert(success, message);
                return success;
            }
        });

        fp.setOnSecondCompleteListener(new DiagonalStoryFP.OnSecondCompleteListener() {
            @Override
            public boolean onSecondComplete(String[] pass) {
                String message = null;
                boolean success;
                if (password[3].equals(pass[0]) && password[4].equals(pass[1]) && password[5].equals(pass[2])) {
                    success = true;
                    screen = DiagonalStoryFP.Batch.FIRST;

                    if (batch == DiagonalStoryFP.Batch.FIRST)
                    {
                        batch = DiagonalStoryFP.Batch.SECOND;

                    }

                    else {
                        batch = DiagonalStoryFP.Batch.DONE;
                        fp.setOnFirstCompleteListener(null);
                        fp.setOnSecondCompleteListener(null);
                        overlay.off();
                        fp.setOnCompleteListener(new FastPhrase.OnCompleteListener() {
                            @Override
                            public void onComplete(String[] pass, ArrayList<TouchData> touchLog) {
                                String message = null;
                                boolean equal = true;
                                for (int i = 0; i < 6; i++) {
                                    if (!(pass[i].equals(password[i]))) {
                                        equal = false;
                                        message = "Try Again.";
                                        break;
                                    }
                                }
                                if (equal) {
                                    if (prevEqual) {
                                        consecEqual = true;
                                    } else {
                                        message = "Once more!";
                                    }
                                    prevEqual = true;
                                    prevWrong = false;
                                    timesEqual++;
                                } else {
                                    prevEqual = false;
                                    prevWrong = true;
                                }
                                alert(equal, message);
                            }
                        });
                    }
                } else {
                    success = false;
                    message = "Try again.";
                }
                alert(success, message);
                return success;
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
                topInstructions.setText("Enter your code by tapping the highlighted icons");
                chooseHighlight();

//                leftInstructions.setText(password[0]);
//                middleInstructions.setText(password[1]);
//                rightInstructions.setText(password[2]);
//                leftLabel.setText(labels[0]);
//                middleLabel.setText(labels[1]);
//                rightLabel.setText(labels[2]);
//                leftInstructions.setTypeface(characterFont);
//                middleInstructions.setTypeface(emojiFont);
//                rightInstructions.setTypeface(buildingFont);
                break;
            case SECOND:
                topInstructions.setText("Drag your finger across the highlighted icons");
                chooseHighlight();
//                leftInstructions.setText(password[3]);
//                middleInstructions.setText(password[4]);
//                rightInstructions.setText(password[5]);
//                leftLabel.setText(labels[3]);
//                middleLabel.setText(labels[4]);
//                rightLabel.setText(labels[5]);
//                leftInstructions.setTypeface(Typeface.DEFAULT);
//                middleInstructions.setTypeface(emojiFont);
//                rightInstructions.setTypeface(emojiFont);
                break;
            case DONE:
                topInstructions.setText("Enter your code");
                if (consecEqual && timesEqual >= 3)
                {
                    startActivity(new Intent(this, StoryActivity.class));
                    finish();
                }
                break;
        }
    }

    private void alert(boolean success, String message)
    {
        String title;
        if (success)
        {
            title = "Correct";
        }
        else
        {
            title = "Incorrect";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResume();
                        if (prevWrong)
                        {
                            Intent intent = new Intent(StoryTutorial.this, PassGenerate.class);
                            intent.putExtra(getString(R.string.generate), false);
                            intent.putExtra(getString(R.string.pass_type), Vals.Types.TRIPLE_STORY);
                            startActivity(intent);
                        }
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onResume();
            }
        });
        if (message != null)
        {
            builder.setMessage(message);
        }
        builder.show();
    }

    private void chooseHighlight()
    {
        if(password[0] != null)
        {
            Resources res = getResources();
            int topId = 0, middleId = 0, bottomId = 0;
            if (screen == DiagonalStoryFP.Batch.FIRST) {
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
            }
            else if (screen == DiagonalStoryFP.Batch.SECOND)
            {
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
            }
            overlay.highlight(topId, middleId, bottomId);
        }
    }
}
