package com.example.gilad.fp.tutorial;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.gilad.fp.PinActivity;
import com.example.gilad.fp.R;
import com.example.gilad.fp.utils.TouchData;
import com.example.gilad.fp.utils.Vals;

import java.util.ArrayList;

public class PinTutorial extends AppCompatActivity {

    Vals.Types type = Vals.Types.PIN;
    String[] userEntered;
    int curIndex;
    final int PIN_LENGTH = PinActivity.PIN_LENGTH;
    Context appContext;
    TextView titleView;
    TextView[] pinBoxArray;
    Button[] buttons;
    Button deleteButton;
    Button clearButton;
    String[] password;
    ArrayList<TouchData> touchLog = new ArrayList<>();
    boolean prevEqual = false;
    boolean prevWrong = false;
    boolean consecEqual = false;
    int timesEqual = 0;
    boolean secondPart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_tutorial);

        Intent passIntent = new Intent(this, PassGenerate.class);
        passIntent.putExtra(getString(R.string.pass_type), type);
        passIntent.putExtra(getString(R.string.generate), getIntent().getBooleanExtra(getString(R.string.generate), true));
        passIntent.putExtra(getString(R.string.message), "This is your code.\n" +
                "For now, take a look at it, but don’t try to memorize it.");
        startActivity(passIntent);

        appContext = this;
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
                if (curIndex == PIN_LENGTH) {

                    //TODO: check password.
                    boolean equal = true;
                    for (int i = 0; i < PIN_LENGTH; i++) {
                        if (!userEntered[i].equals(password[i])) {
                            equal = false;
                            break;
                        }
                    }

                    touchLog.clear();

                    for (int i = 0; i < PIN_LENGTH; i++) {
                        pinBoxArray[i].setText("");
                        userEntered[i] = "";
                    }
                    curIndex = 0;

                    if (equal) {
                        if (!secondPart) {
                            if (prevEqual) {
                                secondPart = true;
                                prevEqual = false;
                                midwayAlert();
                                return;
                            }
                            prevEqual = true;

                        }
                        else {
                            if (prevEqual) {
                                consecEqual = true;
                            }
                            prevEqual = true;
                            prevWrong = false;
                            timesEqual++;
                        }
                    }
                    else
                    {

                        if (secondPart)
                        {
                            prevEqual = false;
                            prevWrong = true;
                        }
                    }

                    alert(equal);
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
        if (secondPart)
        {
            titleView.setText("Try entering your code (without assistance).\n" +
                    "If you don’t remember the code, give it your best shot.");
            if (consecEqual && timesEqual >= 3) {
                Intent next = new Intent(this, PinActivity.class);
                startActivity(next);
                finish();
            }
        }
        else
        {
            if (password[0] == null || password[0].equals("")) {

                SharedPreferences prefs = getSharedPreferences(getString(R.string.filename), MODE_PRIVATE);
                for (int i = 0; i < PIN_LENGTH; i++) {
                    password[i] = prefs.getString(String.format("char%d", i), "");
                }

            }

            String instructions = "Enter the following code using the numpad:\n";

            for (int i = 0; i < PIN_LENGTH; i++) {
                pinBoxArray[i].setText("");
                userEntered[i] = "";
                instructions += password[i] + " ";
            }
            titleView.setText(instructions);


            curIndex = 0;
            touchLog.clear();
        }
    }

    private void alert(boolean success)
    {
        String title;
        String message;
        if (success) {
            title = getString(R.string.correct);
            if (consecEqual && timesEqual >= 3) {
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
                    Intent intent = new Intent(PinTutorial.this, PassGenerate.class);
                    intent.putExtra(getString(R.string.generate), false);
                    intent.putExtra(getString(R.string.pass_type), Vals.Types.PIN);
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
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResume();

                        Intent intent = new Intent(PinTutorial.this, PassGenerate.class);
                        intent.putExtra(getString(R.string.generate), false);
                        intent.putExtra(getString(R.string.pass_type), Vals.Types.PIN);
                        intent.putExtra(getString(R.string.message), "Here is your code once more.\n" +
                                "This time try remembering it, but don’t write it down.\n" +
                                "If you forget it, we will remind you.");
                        startActivity(intent);
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onResume();
                Intent intent = new Intent(PinTutorial.this, PassGenerate.class);
                intent.putExtra(getString(R.string.generate), false);
                intent.putExtra(getString(R.string.pass_type), Vals.Types.PIN);
                intent.putExtra(getString(R.string.message), "Here is your code once more.\n" +
                        "This time try remembering it, but don’t write it down.\n" +
                        "If you forget it, we will remind you.");
                startActivity(intent);

            }
        });
        builder.show();
    }

}
