package com.example.gilad.fp.tutorial;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gilad.fp.MainActivity;
import com.example.gilad.fp.PassGenerate;
import com.example.gilad.fp.R;
import com.example.gilad.fp.utils.TouchData;
import com.example.gilad.fp.utils.Vals;

import java.util.ArrayList;

public class PinTutorial extends AppCompatActivity {

    Vals.Types type = Vals.Types.PIN;
    String[] userEntered;
    int curIndex;
    final int PIN_LENGTH = 5;
    Context appContext;
    TextView titleView;
    TextView[] pinBoxArray;
    Button[] buttons;
    Button deleteButton;
    Button clearButton;
    String[] password;
    ArrayList<TouchData> touchLog = new ArrayList<>();
    boolean finished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_tutorial);

        getSharedPreferences(getString(R.string.filename), MODE_PRIVATE).edit().putString("char0", "").commit();

        Intent next = new Intent(this, PassGenerate.class);
        next.putExtra("type", type);
        startActivity(next);

        appContext = this;
        userEntered = new String[PIN_LENGTH];
        password = new String[PIN_LENGTH];
        buttons = new Button[12];
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
        pinBoxArray[4] = (TextView) findViewById(R.id.pinBox4);


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

                    //TODO: check password.
                    Intent next = new Intent(PinTutorial.this, TutorialSuccess.class);
                    boolean equal = true;
                    for (int i = 0; i < PIN_LENGTH; i++) {
                        if (!userEntered[i].equals(password[i])) {
                            equal = false;
                            break;
                        }
                    }
                    if (equal) {
                        next.putExtra(getString(R.string.success), true);
                        finished = true;
                    } else {
                        next.putExtra(getString(R.string.success), false);
                    }

                    touchLog.clear();

                    for (int i = 0; i < PIN_LENGTH; i++)
                    {
                        pinBoxArray[i].setText("");
                        userEntered[i] = "";
                    }
                    curIndex = 0;
                    startActivity(next);
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
        buttons[10] = (Button) findViewById(R.id.buttonAstrix);
        buttons[11] = (Button) findViewById(R.id.buttonPound);

        for (int i = 0; i < 12; i++)
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
        getMenuInflater().inflate(R.menu.menu_pin_tutorial, menu);
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
        if (finished)
        {
            finish();
        }
        else
        {
            SharedPreferences prefs = getSharedPreferences(getString(R.string.filename), MODE_PRIVATE);
            for (int i = 0; i < PIN_LENGTH; i++) {
                password[i] = prefs.getString(String.format("char%d", i), "");
            }

            String instructions = "Enter the following code using the numpad:\n";

            for (int i = 0; i < PIN_LENGTH; i++)
            {
                pinBoxArray[i].setText("");
                userEntered[i] = "";
                instructions += password[i] + " ";
            }

            titleView.setText(instructions);
            curIndex = 0;
            touchLog.clear();
        }
    }
}
