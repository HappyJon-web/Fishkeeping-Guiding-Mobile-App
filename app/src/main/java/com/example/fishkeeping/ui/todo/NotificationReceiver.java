package com.example.fishkeeping.ui.todo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.fishkeeping.MainActivity;
import com.example.fishkeeping.R;


public class NotificationReceiver extends BroadcastReceiver {

    String title, date, time;

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent notify_intent = new Intent(context, MainActivity.class);
        notify_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, notify_intent, 0);

        Bundle extras = intent.getExtras();
        if (extras != null) {
            //Getting Data from Intent
            title = extras.getString("TASK");
            date = extras.getString("DEADLINE");
            time = extras.getString("TIME");
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "foxandroid")
                .setSmallIcon(R.drawable.unnamed)
                .setContentTitle("Task: " + title)
                .setContentText("Your task deadline is up. Date: " + date + " " + time)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());
    }
}
