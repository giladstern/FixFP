package com.example.gilad.fp.tutorial;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gilad.fp.ListActivity;
import com.example.gilad.fp.PassGenerate;
import com.example.gilad.fp.R;
import com.example.gilad.fp.utils.FastPhrase;
import com.example.gilad.fp.utils.Overlay;
import com.example.gilad.fp.utils.TouchData;
import com.example.gilad.fp.utils.Vals;
import com.example.gilad.fp.utils.WideNoLinesFP;

import java.util.ArrayList;

public class ListTutorial extends AppCompatActivity {

    Vals.Types type = Vals.Types.LIST;

    String password[] = new String[6];
    String labels[] = new String[6];
    TextView topInstructions;
    WideNoLinesFP fp;
    RelativeLayout layout;
    float scale;
    WideNoLinesFP.Batch batch = WideNoLinesFP.Batch.FIRST;
    Overlay overlay;
    boolean prevWrong = false;
    boolean prevEqual = false;
    boolean consecEqual = false;
    int timesEqual = 0;
    Resources res;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tutorial);

        Intent passIntent = new Intent(this, PassGenerate.class);
        passIntent.putExtra(getString(R.string.pass_type), type);
        passIntent.putExtra(getString(R.string.generate), getIntent().getBooleanExtra(getString(R.string.generate), true));
        passIntent.putExtra(getString(R.string.message), "Your code is comprised of the following symbols.\n" +
                "For now, take a look at it, but don’t try to memorize it.");
        startActivity(passIntent);
        res = getResources();

        scale = res.getDisplayMetrics().density;

        topInstructions = (TextView) findViewById(R.id.top_instructions);

        fp = (WideNoLinesFP) findViewById(R.id.list_fp);
        layout = (RelativeLayout) findViewById(R.id.layout);
        overlay = (Overlay) findViewById(R.id.overlay);


        fp.setOnFirstCompleteListener(new WideNoLinesFP.OnFirstCompleteListener() {
            @Override
            public boolean onFirstComplete(String[] pass) {

                secondHighlight();
                return true;
            }
        });

        fp.setOnSecondCompleteListener(new WideNoLinesFP.OnSecondCompleteListener() {
            @Override
            public boolean onSecondComplete(String[] pass) {
                thirdHighlight();
                return true;
            }
        });

        fp.setOnThirdCompleteListener(new WideNoLinesFP.OnThirdCompleteListener() {
            @Override
            public boolean onThirdComplete(String[] pass) {
                firstHighlight();
                return true;
            }
        });

        fp.setOnCompleteListener(new FastPhrase.OnCompleteListener() {
            @Override
            public void onComplete(String[] pass, ArrayList<TouchData> touchLog) {
                boolean equal = true;
                for (int i = 0; i < 6; i++) {
                    if (!(pass[i].equals(password[i]))) {
                        equal = false;
                        break;
                    }
                }

                if (equal) {
                    if (batch == WideNoLinesFP.Batch.FIRST) {
                        batch = WideNoLinesFP.Batch.SECOND;
                    } else if (batch == WideNoLinesFP.Batch.SECOND) {
                        batch = WideNoLinesFP.Batch.DONE;
                        overlay.off();
                        fp.setOnFirstCompleteListener(null);
                        fp.setOnSecondCompleteListener(null);
                        midwayAlert();
                        return;
                    } else {
                        if (prevEqual) {
                            consecEqual = true;
                        }
                        prevEqual = true;
                        prevWrong = false;
                        timesEqual++;
                    }

                } else {
                    prevEqual = false;
                    if (batch == WideNoLinesFP.Batch.DONE) {
                        prevWrong = true;
                    }
                }

                alert(equal);
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
        fp.reset();
        switch (batch) {
            case FIRST:
                if (password[0] == null || password[0].equals("")) {
                    SharedPreferences prefs = getSharedPreferences(getString(R.string.filename), MODE_PRIVATE);
                    for (int i = 0; i < 6; i++) {
                        password[i] = prefs.getString(String.format("char%d", i), "");
                        labels[i] = prefs.getString(String.format("label%d", i), "");
                    }
                }

                topInstructions.setText("Enter your code by tapping the highlighted symbols.");
                firstHighlight();
                break;
            case SECOND:
                topInstructions.setText("You can also enter your code by dragging your finger across the highlighted symbols.\n" +
                        "Give it a try!");
                firstHighlight();
                break;
            case DONE:
                topInstructions.setText("Try entering your code (without assistance).\n" +
                        "If you don’t remember the code, give it your best shot.");
                if (consecEqual && timesEqual >= 3) {
                    startActivity(new Intent(this, ListActivity.class));
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
            if (consecEqual && timesEqual >= 3 || batch != WideNoLinesFP.Batch.DONE) {
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
        if (!success)
        {
            builder.setPositiveButton(getString(R.string.forgot), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ListTutorial.this, PassGenerate.class);
                    intent.putExtra(getString(R.string.generate), false);
                    intent.putExtra(getString(R.string.pass_type), Vals.Types.LIST);
                    startActivity(intent);
                }
            });
        }
        builder.show();
    }

    private void midwayAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.correct))
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResume();

                        Intent intent = new Intent(ListTutorial.this, PassGenerate.class);
                        intent.putExtra(getString(R.string.generate), false);
                        intent.putExtra(getString(R.string.pass_type), Vals.Types.LIST);
                        intent.putExtra(getString(R.string.message), "Here is your code once more.\n" +
                                "This time try remembering it, but don’t write it down.\n" +
                                "If you forget it, we will remind you.");
                        startActivity(intent);
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onResume();
                Intent intent = new Intent(ListTutorial.this, PassGenerate.class);
                intent.putExtra(getString(R.string.generate), false);
                intent.putExtra(getString(R.string.pass_type), Vals.Types.LIST);
                intent.putExtra(getString(R.string.message), "Here is your code once more.\n" +
                                "This time try remembering it, but don’t write it down.\n" +
                                "If you forget it, we will remind you.");
                startActivity(intent);

            }
        });
        builder.show();
    }

    private void firstHighlight() {
        if (password[0] != null && !password[0].equals("")) {
            int topId, bottomId = 0;
            topId = Integer.parseInt(password[0]) - 1;
            int[] icons = res.getIntArray(R.array.vehicles);

            for (int i = 0; i < 5; i++) {
                if (new String(Character.toChars(icons[i])).equals(password[1])) {
                    bottomId = i;
                }
            }

            overlay.highlight(topId, bottomId);
        }
    }

    private void secondHighlight() {
        if (password[0] != null && !password[0].equals("")) {
            int topId, bottomId = 0;

            topId = Integer.parseInt(password[2]) - 1;
            int[] icons = res.getIntArray(R.array.animals);

            for (int i = 0; i < 5; i++) {
                if (new String(Character.toChars(icons[i])).equals(password[3])) {
                    bottomId = i;
                }
            }

            overlay.highlight(topId, bottomId);
        }
    }

    private void thirdHighlight() {
        if (password[0] != null && !password[0].equals("")) {
            int topId, bottomId = 0;
            topId = Integer.parseInt(password[4]) - 1;
            int[] icons = res.getIntArray(R.array.clothes);

            for (int i = 0; i < 5; i++) {
                if (new String(Character.toChars(icons[i])).equals(password[5])) {
                    bottomId = i;
                }
            }
            overlay.highlight(topId, bottomId);
        }
    }
}