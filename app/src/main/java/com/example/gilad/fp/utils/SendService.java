package com.example.gilad.fp.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by Gilad on 10/8/2015.
 */
public class SendService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Testing");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        stopSelf();
                    } else {
                        for (ParseObject elem : list) {
                            elem.saveEventually(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Testing");
                                        query.fromLocalDatastore();
                                        query.findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(List<ParseObject> list, ParseException e) {
                                                if (e == null) {
                                                    if (list.size() == 0) {
                                                        stopSelf();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    else
                                    {
                                        stopSelf();
                                    }
                                }
                            });
                        }
                    }
                } else {
                    stopSelf();
                }
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}