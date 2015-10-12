package com.example.gilad.fp.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import com.example.gilad.fp.DispatchActivity;
import com.example.gilad.fp.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by Gilad on 9/6/2015.
 */

public class Alarm extends WakefulBroadcastReceiver
{
    static final String ALARM_ACTION = "com.example.gilad.playground.action.Alarm";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            long time = context.getSharedPreferences(context.getString(R.string.stage_file), Context.MODE_PRIVATE).getLong(context.getString(R.string.next_alarm), 0);
            if (time != 0)
            {
                AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                Intent i = new Intent(context, Alarm.class);
                i.setAction(ALARM_ACTION);
                PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
                am.set(AlarmManager.RTC_WAKEUP, time, pi);
            }
            Intent serviceIntent = new Intent(context, SendService.class);
            context.startService(serviceIntent);
        }
        else if (intent.getAction().equals(ALARM_ACTION)){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.notification_template_icon_bg)
                    .setContentTitle("Fast Phrase")
                    .setContentText("Time to enter passcode.")
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL);
            Intent resultIntent = new Intent(context, DispatchActivity.class);
            resultIntent.setAction("");
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());
        }
    }

    static public void set(Context context, long time)
    {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("times", Context.MODE_PRIVATE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        i.setAction(ALARM_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
//        sharedPreferences.edit().putLong("nextAlarm", time).commit();
        am.set(AlarmManager.RTC_WAKEUP, time, pi);
    }

    static public void cancel(Context context)
    {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
