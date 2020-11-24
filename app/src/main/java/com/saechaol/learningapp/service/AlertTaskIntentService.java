package com.saechaol.learningapp.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.saechaol.learningapp.model.TaskDetails;
import com.saechaol.learningapp.receiver.NotificationPublisher;
import com.saechaol.learningapp.util.PreferenceManager;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Sends alerts to the user's device for pending tasks
 */
public class AlertTaskIntentService extends IntentService {

    public AlertTaskIntentService() {
        super("AlertTaskIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.hasExtra("stop")) {
            cancelNotification("", "", 0);
        } else {
            try {
                GetTaskDetails getTaskDetails = new GetTaskDetails(this);
                getTaskDetails.doInBackground();
            } catch (Exception e) {

            }
        }
    }

    private void cancelNotification(String title, String description, int id) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.ID, id);
        notificationIntent.putExtra(NotificationPublisher.TITLE, title);
        notificationIntent.putExtra(NotificationPublisher.DESCRIPTION, description);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    private void scheduleNotification(String title, String description, int id, long time) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.ID, id);
        notificationIntent.putExtra(NotificationPublisher.TITLE, title);
        notificationIntent.putExtra(NotificationPublisher.DESCRIPTION, description);

        Log.d("schedule notification", "show notification: " + new Date(time).toString() + " id: " + id + " topic: " + title + " description: " + description);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
    }

    /**
     * Provides support for obtaining task details and adding them to the ListView
     */
    class GetTaskDetails extends AsyncTask<Void, Void, Void> {

        Context taskContext;

        public GetTaskDetails(Context context) {
            taskContext = context;
        }

        @Override
        protected Void doInBackground(Void... v) {
            try {

                PreferenceManager preferenceManager = new PreferenceManager(AlertTaskIntentService.this);
                Call<List<TaskDetails>> getCallTasks = Api.getClient().getTaskByUser(preferenceManager.getStringData("username"), preferenceManager.getStringData("userType"));

                Response<List<TaskDetails>> responseTaskDetail = getCallTasks.execute();
                if (responseTaskDetail.isSuccessful() && responseTaskDetail.body() != null) {
                    cancelNotification("", "", 0);
                    for (TaskDetails taskDetails : responseTaskDetail.body()) {
                        if (taskDetails != null && taskDetails.getScheduleStartTime() != null) {
                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                Date date = dateFormat.parse(taskDetails.getScheduleStartTime());
                                long startTimeMillis = date.getTime();
                                if (Calendar.getInstance().getTimeInMillis() < (startTimeMillis - 10 * 60 * 1000)) {
                                    scheduleNotification(taskDetails.getTitle(), taskDetails.getDescription(), taskDetails.getTaskId(), (startTimeMillis - 10 * 60 * 1000));
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
