package com.example.gilad.fp.tutorial;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import com.example.gilad.fp.ListActivity;
import com.example.gilad.fp.MainActivity;
import com.example.gilad.fp.PassGenerate;
import com.example.gilad.fp.R;
import com.example.gilad.fp.utils.DiagonalStoryFP;
import com.example.gilad.fp.utils.FastPhrase;
import com.example.gilad.fp.utils.Overlay;
import com.example.gilad.fp.utils.TouchData;
import com.example.gilad.fp.utils.Vals;
import com.example.gilad.fp.utils.WideNoLinesFP;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListTutorial extends AppCompatActivity {

    Vals.Types type = Vals.Types.LIST;

    String password[] = new String[6];
    String labels[] = new String[6];
    TextView topInstructions;
//    TextView leftInstructions;
//    TextView rightInstructions;
//    TextView rightLabel;
    WideNoLinesFP fp;
    RelativeLayout layout;
    float scale;
    WideNoLinesFP.Batch batch = WideNoLinesFP.Batch.FIRST;
    WideNoLinesFP.Batch screen = WideNoLinesFP.Batch.FIRST;
    Overlay overlay;
    boolean prevWrong = false;
    boolean prevEqual = false;
    boolean consecEqual = false;
    int timesEqual = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tutorial);

        Intent passIntent = new Intent(this, PassGenerate.class);
        passIntent.putExtra(getString(R.string.pass_type), type);
        passIntent.putExtra(getString(R.string.generate), getIntent().getBooleanExtra(getString(R.string.generate), true));
        startActivity(passIntent);

        scale = getResources().getDisplayMetrics().density;
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/androidemoji.ttf");

        topInstructions = (TextView) findViewById(R.id.top_instructions);
//        leftInstructions = (TextView) findViewById(R.id.left_instructions);
//        rightInstructions = (TextView) findViewById(R.id.right_instructions);
//        rightLabel = (TextView) findViewById(R.id.right_label);
        fp = (WideNoLinesFP) findViewById(R.id.list_fp);
        layout = (RelativeLayout) findViewById(R.id.layout);
        overlay = (Overlay) findViewById(R.id.overlay);

//        rightInstructions.setTypeface(font);

//        ViewTreeObserver vto = layout.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int width = (layout.getWidth() - layout.getPaddingLeft() - layout.getPaddingRight() )/ 3;
//                leftInstructions.getLayoutParams().width = width;
//                rightInstructions.getLayoutParams().width = width;
//                rightLabel.getLayoutParams().width = width;
//                findViewById(R.id.arrow).getLayoutParams().width = width;
//            }
//        });

        fp.setOnFirstCompleteListener(new WideNoLinesFP.OnFirstCompleteListener() {
            @Override
            public boolean onFirstComplete(String[] pass) {
//                Intent next = new Intent(ListTutorial.this, TutorialSuccess.class);
//                if (password[0].equals(pass[0]) && password[1].equals(pass[1])) {
//                    next.putExtra(getString(R.string.success), true);
//                    startActivity(next);
//                    batch = WideNoLinesFP.Batch.SECOND;
//                    return true;
//                } else {
//                    next.putExtra(getString(R.string.success), false);
//                    startActivity(next);
//                    return false;
//                }
                String message = null;
                boolean success;
                if (password[0].equals(pass[0]) && password[1].equals(pass[1])) {
                    success = true;
                    screen = WideNoLinesFP.Batch.SECOND;
                } else {
                    success = false;
                    message = "Try again.";
                }
                alert(success, message);
                return success;
            }
        });

        fp.setOnSecondCompleteListener(new WideNoLinesFP.OnSecondCompleteListener() {
            @Override
            public boolean onSecondComplete(String[] pass) {
                String message = null;
                boolean success;
                if (password[2].equals(pass[0]) && password[3].equals(pass[1])) {
                    success = true;
                    screen = WideNoLinesFP.Batch.THIRD;
                } else {
                    success = false;
                    message = "Try again.";
                }
                alert(success, message);
                return success;
            }
        });

        fp.setOnThirdCompleteListener(new WideNoLinesFP.OnThirdCompleteListener() {
            @Override
            public boolean onThirdComplete(String[] pass) {
                String message = null;
                boolean success;
                if (password[4].equals(pass[0]) && password[5].equals(pass[1])) {
                    success = true;
                    screen = WideNoLinesFP.Batch.FIRST;

                    if (batch == WideNoLinesFP.Batch.FIRST)
                    {
                        batch = WideNoLinesFP.Batch.SECOND;

                    }

                    else {
                        batch = WideNoLinesFP.Batch.DONE;
                        fp.setOnFirstCompleteListener(null);
                        fp.setOnSecondCompleteListener(null);
                        fp.setOnThirdCompleteListener(null);
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
                                    }
                                    prevEqual = true;
                                    prevWrong = false;
                                    timesEqual++;
                                    if (!consecEqual || timesEqual < 3)
                                    {
                                        message = "Once more!";
                                    }
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
//                rightInstructions.setText(password[1]);
//                rightLabel.setText(labels[1]);
                break;
            case SECOND:
                topInstructions.setText("Drag your finger across the highlighted icons");
                chooseHighlight();
//                leftInstructions.setText(password[2]);
//                rightInstructions.setText(password[3]);
//                rightLabel.setText(labels[3]);
                break;
            case DONE:
                topInstructions.setText("Enter your code");
                if (consecEqual && timesEqual >= 3)
                {
                    startActivity(new Intent(this, ListActivity.class));
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
                            Intent intent = new Intent(ListTutorial.this, PassGenerate.class);
                            intent.putExtra(getString(R.string.generate), false);
                            intent.putExtra(getString(R.string.pass_type), Vals.Types.LIST);
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
        if(password[0] != null && !password[0].equals(""))
        {
            Resources res = getResources();
            int topId = 0, bottomId = 0;
            if (screen == WideNoLinesFP.Batch.FIRST) {
                topId = Integer.parseInt(password[0]) - 1;
                int[] icons = res.getIntArray(R.array.vehicles);

                for (int i = 0; i < 5; i++) {
                    if (new String(Character.toChars(icons[i])).equals(password[1])) {
                        bottomId = i;
                    }
                }
            }
            else if (screen == WideNoLinesFP.Batch.SECOND)
            {
                topId = Integer.parseInt(password[2]) - 1;
                int[] icons = res.getIntArray(R.array.animals);

                for (int i = 0; i < 5; i++) {
                    if (new String(Character.toChars(icons[i])).equals(password[3])) {
                        bottomId = i;
                    }
                }
            }
            else if(screen == WideNoLinesFP.Batch.THIRD)
            {
                topId = Integer.parseInt(password[4]) - 1;
                int[] icons = res.getIntArray(R.array.clothes);

                for (int i = 0; i < 5; i++) {
                    if (new String(Character.toChars(icons[i])).equals(password[5])) {
                        bottomId = i;
                    }
                }
            }
            overlay.highlight(topId, bottomId);
        }
    }
}