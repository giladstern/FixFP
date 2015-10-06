package com.example.gilad.fp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.gilad.fp.utils.TouchData;
import com.example.gilad.fp.utils.Vals;

public class PinActivity extends AppCompatActivity {

    Vals.Types type = Vals.Types.PIN;
    String[] userEntered;
    int curIndex;
    public final static int PIN_LENGTH = 4;
    TextView titleView;
    TextView[] pinBoxArray;
    Button[] buttons;
    Button deleteButton;
    Button clearButton;
    String[] password;
    ArrayList<TouchData> touchLog = new ArrayList<>();
    int stage;
    int timesLeft;
    int passShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        stage = getIntent().getIntExtra("stage", 0);
        timesLeft = Vals.ITERATIONS[stage];

        if (stage == 0)
        {
            Intent pass = new Intent(this, PassGenerate.class);
            pass.putExtra(getString(R.string.pass_type), type);
            pass.putExtra(getString(R.string.message), "Now that you know how to enter your code, memorize it again for future use. \n" +
                    "Don’t write it down.\n" +
                    "If you forget the code, we will remind you.");
            startActivity(pass);
        }

        userEntered = new String[PIN_LENGTH];
        password = new String[PIN_LENGTH];
        buttons = new Button[11];
        curIndex = 0;

        //Typeface xpressive = Typeface.createFromAsset(getAssets(), "fonts/XpressiveBold.ttf");

//        buttonDelete = (Button) findViewById(R.id.buttonDeleteBack);
//        buttonDelete.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//
//                                                if (keyPadLockedFlag) return;
//
//                                                if (userEntered.length() > 0) {
//                                                    userEntered = userEntered.substring(0, userEntered.length() - 1);
//                                                    pinBoxArray[userEntered.length()].setText("");
//                                                }
//
//
//                                            }
//                                        }
//        );

        titleView = (TextView) findViewById(R.id.titleBox);
//		titleView.setTypeface(xpressive);

        pinBoxArray = new TextView[PIN_LENGTH];

        pinBoxArray[0] = (TextView) findViewById(R.id.pinBox0);
        pinBoxArray[1] = (TextView) findViewById(R.id.pinBox1);
        pinBoxArray[2] = (TextView) findViewById(R.id.pinBox2);
        pinBoxArray[3] = (TextView) findViewById(R.id.pinBox3);


//		statusView.setTypeface(xpressive);

        View.OnClickListener pinButtonHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button pressedButton = (Button) v;

                userEntered[curIndex] = (String)(pressedButton.getText());

                //Update pin boxes
                pinBoxArray[curIndex].setText(userEntered[curIndex]);

                touchLog.add(new TouchData(System.currentTimeMillis(), MotionEvent.ACTION_DOWN, v.getId() - 1, userEntered[curIndex]));

                curIndex++;

//                    if (userEntered.length() == PIN_LENGTH) {
//                        statusView.setText("initializing.. please wait");
//                    }
                if (curIndex == PIN_LENGTH){

                    boolean equal = true;
                    for (int i = 0; i < PIN_LENGTH; i++) {
                        if (!userEntered[i].equals(password[i])) {
                            equal = false;
                            break;
                        }
                    }

                    touchLog.clear();

                    for (int i = 0; i < PIN_LENGTH; i++)
                    {
                        pinBoxArray[i].setText("");
                        userEntered[i] = "";
                    }
                    curIndex = 0;

                    if (equal)
                    {
                        timesLeft--;
                        titleView.setText(String.format(getString(R.string.num_left_msg), timesLeft));
                    }

                    else
                    {
                        alert();
                    }

                    if (timesLeft == 0)
                    {
                        Intent intent = new Intent(PinActivity.this, AlarmSetActivity.class);
                        intent.putExtra(getString(R.string.stage), stage);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };

        buttons[0] = (Button) findViewById(R.id.button0);
        buttons[1] = (Button) findViewById(R.id.button1);
        buttons[2] = (Button) findViewById(R.id.button2);
        buttons[3] = (Button) findViewById(R.id.button3);
        buttons[4] = (Button) findViewById(R.id.button4);
        buttons[5] = (Button) findViewById(R.id.button5);
        buttons[6] = (Button) findViewById(R.id.button6);
        buttons[7] = (Button) findViewById(R.id.button7);
        buttons[8] = (Button) findViewById(R.id.button8);
        buttons[9] = (Button) findViewById(R.id.button9);
        buttons[10] = (Button) findViewById(R.id.buttonA);

        for (int i = 0; i < 11; i++)
        {
            buttons[i].setOnClickListener(pinButtonHandler);
            buttons[i].setId(i + 1);
        }

        deleteButton = (Button) findViewById(R.id.buttonDelete);
        clearButton = (Button) findViewById(R.id.buttonClear);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curIndex != 0)
                {
                    curIndex--;
                    userEntered[curIndex] = "";
                    pinBoxArray[curIndex].setText("");
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < curIndex; i++)
                {
                    userEntered[i] = "";
                    pinBoxArray[i].setText("");
                }

                curIndex = 0;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pin, menu);
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

    protected void onResume()
    {
        super.onResume();
        if (timesLeft != 0) {
            if (password[0] == null) {
                SharedPreferences prefs = getSharedPreferences(getString(R.string.filename), MODE_PRIVATE);
                for (int i = 0; i < PIN_LENGTH; i++) {
                    password[i] = prefs.getString(String.format("char%d", i), "");
                }
            }
            for (int i = 0; i < PIN_LENGTH; i++) {
                pinBoxArray[i].setText("");
                userEntered[i] = "";
            }
            curIndex = 0;
            titleView.setText(String.format(getString(R.string.num_left_msg), timesLeft));
            touchLog.clear();
        }
        else
        {
            Intent intent = new Intent(this, AlarmSetActivity.class);
            intent.putExtra(getString(R.string.stage), stage);
            startActivity(intent);
            finish();
        }
    }

    private void alert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.incorrect)
                .setNeutralButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResume();
                    }
                }).setPositiveButton(R.string.forgot, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        passShown++;
                        Intent intent = new Intent(PinActivity.this, PassGenerate.class);
                        intent.putExtra(getString(R.string.generate), false);
                        intent.putExtra(getString(R.string.pass_type), type);
                        startActivity(intent);
                    }
                }

        ).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onResume();
            }
        });
        builder.show();
    }
}
