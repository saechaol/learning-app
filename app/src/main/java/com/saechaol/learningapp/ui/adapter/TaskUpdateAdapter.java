package com.saechaol.learningapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.TaskDetails;
import com.saechaol.learningapp.sinch.PlaceCallActivity;
import com.saechaol.learningapp.ui.activity.CallStudentActivity;
import com.saechaol.learningapp.util.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskUpdateAdapter extends BaseAdapter {

    List<TaskDetails> listTasks = new ArrayList<>();
    Context context;
    boolean showCallOptions=false;
    PreferenceManager prefsManager;
    public TaskUpdateAdapter(Context context,boolean showCallOptions, List<TaskDetails> listTaks) {
        this.context = context;
        this.listTasks = listTaks;
        this.showCallOptions = showCallOptions;
        prefsManager = new PreferenceManager(context);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View rowView;
        rowView = convertView;
        DataAdapter dataAdapter;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(R.layout.rowlayout_taskmodified, parent, false);
            dataAdapter = new DataAdapter();
            dataAdapter.txtTime = (TextView) rowView.findViewById(R.id.taskUpdateTextTime);
            dataAdapter.txtTopic = (TextView) rowView.findViewById(R.id.taskUpdateTextTopic);
            dataAdapter.txtDescription = (TextView) rowView.findViewById(R.id.taskUpdateTextDescription);
            dataAdapter.imgAudio = (ImageView) rowView.findViewById(R.id.task_item_update_layout_imgAudioCall);
            dataAdapter.imgVideo = (ImageView) rowView.findViewById(R.id.task_item_update_layout_imgVideoCall);


            rowView.setTag(dataAdapter);
            rowView.setTag(R.id.taskUpdateTextTime, dataAdapter.txtTime);
            rowView.setTag(R.id.taskUpdateTextTopic, dataAdapter.txtTopic);
            rowView.setTag(R.id.taskUpdateTextDescription, dataAdapter.txtDescription);
            rowView.setTag(R.id.task_item_update_layout_imgVideoCall, dataAdapter.imgVideo);
            rowView.setTag(R.id.task_item_update_layout_imgAudioCall, dataAdapter.imgAudio);

        } else {
            dataAdapter = (DataAdapter) rowView.getTag();
        }
        final TaskDetails taskUpdateProvider;
        taskUpdateProvider = (TaskDetails) this.getItem(position);

        try {

            dataAdapter.txtTime.setText(getDateString(taskUpdateProvider.getScheduleStartTime(),taskUpdateProvider.getScheduleEndTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(showCallOptions
        ){
            dataAdapter.imgAudio.setVisibility(View.VISIBLE);
            dataAdapter.imgVideo.setVisibility(View.VISIBLE);
        }else{
            dataAdapter.imgAudio.setVisibility(View.GONE
            );
            dataAdapter.imgVideo.setVisibility(View.GONE);

        }
        dataAdapter.imgAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent=new Intent(context, CallStudentActivity.class);
                intent.putExtra(CallStudentActivity.SUBJ_ID,taskUpdateProvider.getSubjectId());
                intent.putExtra(PlaceCallActivity.VIDEO_CALL,false);

                context.startActivity(intent);
            }
        });
        dataAdapter.imgVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(context, CallStudentActivity.class);
                intent.putExtra(CallStudentActivity.SUBJ_ID,taskUpdateProvider.getSubjectId());
                intent.putExtra(PlaceCallActivity.VIDEO_CALL,true);

                context.startActivity(intent);
            }
        });

        dataAdapter.txtTopic.setText(TextUtils.isEmpty(taskUpdateProvider.getTitle()) ? "Not Available" : taskUpdateProvider.getTitle());
        dataAdapter.txtDescription.setText(TextUtils.isEmpty(taskUpdateProvider.getDescription()) ? "Not Available" : taskUpdateProvider.getDescription());

        return rowView;
    }

    static class DataAdapter {
        TextView txtTime;
        TextView txtTopic;
        ImageView imgAudio,imgVideo;
        TextView txtDescription;
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
            String monthString = (calendar.get(Calendar.MONTH)+1 )> 9 ? ((calendar.get(Calendar.MONTH)+1) + "") : ("0" + (calendar.get(Calendar.MONTH)+1)
            );
            String hourString = calendar.get(Calendar.HOUR_OF_DAY) > 9 ? calendar.get(Calendar.HOUR_OF_DAY) + "" : ("0" + calendar.get(Calendar.HOUR_OF_DAY));
            String minuteString = calendar.get(Calendar.MINUTE) > 9 ? calendar.get(Calendar.MINUTE) + "" : ("0" + calendar.get(Calendar.MINUTE));
            Date endDate = dateFormat.parse(endDateString);
            calendar.setTime(endDate);
            String endHourString = calendar.get(Calendar.HOUR_OF_DAY) > 9 ? calendar.get(Calendar.HOUR_OF_DAY) + "" : ("0" + calendar.get(Calendar.HOUR_OF_DAY));
            String endMinuteString = calendar.get(Calendar.MINUTE) > 9 ? calendar.get(Calendar.MINUTE) + "" : ("0" + calendar.get(Calendar.MINUTE));

            durationString = monthString+"/"+dateString  + "/" + calendar.get(Calendar.YEAR) + "," + hourString + ":" + minuteString + "-" + endHourString + ":" + endMinuteString;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return durationString;

    }

}
