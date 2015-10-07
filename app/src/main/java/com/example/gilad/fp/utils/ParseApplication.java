package com.example.gilad.fp.utils;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseUser;

/**
 * Created by Gilad on 10/7/2015.
 */
public class ParseApplication extends Application {
    public void onCreate()
    {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "s0XwhTqypBvDlHXLC0E3tBCVuGi4CTn19UkNApS3", "2ICs1JFUc6N1qGVDiretkKjfGwez5DmlJ50DC9n9");
        ParseUser.enableAutomaticUser();
    }
}
