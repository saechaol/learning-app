package com.saechaol.learningapp.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.sinch.LoginActivity;

/**
 * Creates and publishes notification to the user
 */
public class NotificationPublisher extends BroadcastReceiver {

    public static String ID = "notification-id";
    public static String NOTIFICATION = "notification";
    public static String TITLE = "notification_title";
    public static String DESCRIPTION = "notification_desc";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onReceive", "show notification: " + intent.getStringExtra(TITLE));
        Log.d("onReceive", "show notification: " + intent.getStringExtra(DESCRIPTION));

        int id = intent.getIntExtra(ID, 0);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent notificationIntent = new Intent(context, LoginActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(LoginActivity.class);
        taskStackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingItent = taskStackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder
                .setContentTitle("Learning Application")
                .setContentText("Your class is going to start. Please open the application and begin the task.")
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingItent)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }
}
