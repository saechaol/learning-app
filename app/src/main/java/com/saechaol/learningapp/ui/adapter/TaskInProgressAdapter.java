package com.saechaol.learningapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.TaskDetails;
import com.saechaol.learningapp.sinch.PlaceCallActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskInProgressAdapter extends BaseAdapter {

    List<TaskDetails> listTasks = new ArrayList<>();
    Context context;

    public TaskInProgressAdapter(Context context, List<TaskDetails> listTasks) {
        this.context = context;
        this.listTasks = listTasks;

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = convertView;
        DataAdapter dataAdapter;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(R.layout.rowlayout_task_inprocess, parent, false);
            dataAdapter = new DataAdapter();
            dataAdapter.txtTime = (TextView) rowView.findViewById(R.id.taskInProgressTextTime);
            dataAdapter.txtTopic = (TextView) rowView.findViewById(R.id.taskInProgressTextTopic);
            dataAdapter.txtDescription = (TextView) rowView.findViewById(R.id.taskInProgressTextDescription);
            dataAdapter.btnPause = (Button) rowView.findViewById(R.id.task_item_progress_layout_btnFinish);
            dataAdapter.btnStart = (Button) rowView.findViewById(R.id.task_item_progress_layout_btnStart);
            dataAdapter.imgAudioCall = (ImageView) rowView.findViewById(R.id.task_item_progress_layout_imgAudioCall);
            dataAdapter.imgVideoCall = (ImageView) rowView.findViewById(R.id.task_item_progress_layout_imgVideoCall);
            dataAdapter.progressBar = (ProgressBar) rowView.findViewById(R.id.task_item_progress_layout_Progress);

            rowView.setTag(dataAdapter);
            rowView.setTag(R.id.taskInProgressTextTime, dataAdapter.txtTime);
            rowView.setTag(R.id.taskInProgressTextTopic, dataAdapter.txtTopic);
            rowView.setTag(R.id.taskInProgressTextDescription, dataAdapter.txtDescription);
            rowView.setTag(R.id.task_item_progress_layout_btnFinish, dataAdapter.btnPause);
            rowView.setTag(R.id.task_item_progress_layout_btnStart, dataAdapter.btnStart);
            rowView.setTag(R.id.task_item_progress_layout_imgAudioCall, dataAdapter.imgAudioCall);
            rowView.setTag(R.id.task_item_progress_layout_imgVideoCall, dataAdapter.imgVideoCall);
            rowView.setTag(R.id.task_item_progress_layout_Progress, dataAdapter.progressBar);

        } else {
            dataAdapter = (DataAdapter) rowView.getTag();
        }
        final TaskDetails taskUpdateProvider;
        taskUpdateProvider = (TaskDetails) this.getItem(position);

        if (taskUpdateProvider.isInProgress()) {
            Log.d("test", "taskUpdateProvider.getLastProgress()" + taskUpdateProvider.getProgressMade());
            dataAdapter.btnPause.setEnabled(true);
            dataAdapter.btnStart.setEnabled(false);
            dataAdapter.progressBar.setProgress(taskUpdateProvider.getProgressMade());

        } else {
            dataAdapter.btnPause.setEnabled(false);
            dataAdapter.btnStart.setEnabled(true);

        }

        dataAdapter.imgAudioCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent=new Intent(context, PlaceCallActivity.class);
                intent.putExtra(PlaceCallActivity.CALL_ID,listTasks.get(position).getInstructorId());

                intent.putExtra(PlaceCallActivity.VIDEO_CALL,false);
                context.startActivity(intent);

            }
        });
        dataAdapter.imgVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent=new Intent(context, PlaceCallActivity.class);
                intent.putExtra(PlaceCallActivity.CALL_ID,listTasks.get(position).getInstructorId());

                intent.putExtra(PlaceCallActivity.VIDEO_CALL,true);
                context.startActivity(intent);


            }
        });
        dataAdapter.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listTasks.get(position).isInProgress = true;
            }
        });

        dataAdapter.btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listTasks.get(position).isInProgress = false;
            }
        });

        try {
            dataAdapter.txtTime.setText(getDateString(taskUpdateProvider.getScheduleStartTime(), taskUpdateProvider.getScheduleEndTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }


        dataAdapter.txtTopic.setText(TextUtils.isEmpty(taskUpdateProvider.getTitle()) ? "Not Available" : taskUpdateProvider.getTitle());
        dataAdapter.txtDescription.setText(TextUtils.isEmpty(taskUpdateProvider.getDescription()) ? "Not Available" : taskUpdateProvider.getDescription());

        return rowView;
    }

    static class DataAdapter {
        TextView txtTime;
        TextView txtTopic;
        TextView txtDescription;
        Button btnStart, btnPause;

        ImageView imgAudioCall, imgVideoCall;
        ProgressBar progressBar;

    }

    @Override
    public Object getItem(int position) {
        return this.listTasks.get(position);
    }

    @Override
    public int getCount() {
        return this.listTasks.size();
    }


    private String getDateString(String startDateString, String endDateString) {
        Calendar calendar = null;
        String durationString = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Date date = dateFormat.parse(startDateString);

            calendar = Calendar.getInstance();
            calendar.setTime(date);
            String dateString = calendar.get(Calendar.DATE) > 9 ? calendar.get(Calendar.DATE) + "" : "0" + calendar.get(Calendar.DATE);
            String monthString = (calendar.get(Calendar.MONTH) + 1) > 9 ? ((calendar.get(Calendar.MONTH) + 1) + "") : ("0" + (calendar.get(Calendar.MONTH) + 1)
            );
            String hourString = calendar.get(Calendar.HOUR_OF_DAY) > 9 ? calendar.get(Calendar.HOUR_OF_DAY) + "" : ("0" + calendar.get(Calendar.HOUR_OF_DAY));
            String minuteString = calendar.get(Calendar.MINUTE) > 9 ? calendar.get(Calendar.MINUTE) + "" : ("0" + calendar.get(Calendar.MINUTE));
            Date endDate = dateFormat.parse(endDateString);
            calendar.setTime(endDate);
            String endHourString = calendar.get(Calendar.HOUR_OF_DAY) > 9 ? calendar.get(Calendar.HOUR_OF_DAY) + "" : ("0" + calendar.get(Calendar.HOUR_OF_DAY));
            String endMinuteString = calendar.get(Calendar.MINUTE) > 9 ? calendar.get(Calendar.MINUTE) + "" : ("0" + calendar.get(Calendar.MINUTE));

            durationString = monthString + "/" + dateString + "/" + calendar.get(Calendar.YEAR) + "," + hourString + ":" + minuteString + "-" + endHourString + ":" + endMinuteString;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return durationString;

    }

    public void updateTaskProgress() {
        if (listTasks != null && listTasks.size() > 0) {
            for (int i = 0; i < listTasks.size(); i++) {
                listTasks.get(i).setProgressMade(calculateProgress(listTasks.get(i).getScheduleStartTime(), listTasks.get(i).getScheduleEndTime()));
            }
            this.notifyDataSetChanged();

        }
    }

    private int calculateProgress(String startDateString, String endDateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Date startDate = dateFormat.parse(startDateString);

            Date endDate = dateFormat.parse(endDateString);

            Date currentDate = new Date();
            long totalTime = (endDate.getTime() - startDate.getTime());
            long currentTime = currentDate.getTime() - startDate.getTime();

            long diff = (currentTime * 100L) / totalTime;

            int percentage = ((int) diff);
            return percentage > 100 ? 100 : percentage;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;

    }

}
