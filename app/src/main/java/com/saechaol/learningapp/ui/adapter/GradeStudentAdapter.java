package com.saechaol.learningapp.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.StudentGrade;

import java.util.ArrayList;
import java.util.List;

public class GradeStudentAdapter extends ArrayAdapter {

    List list = new ArrayList<>();
    public GradeStudentAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = convertView;
        DataAdapter dataAdapter;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.rowlayout_grade_student,parent,false);
            dataAdapter = new DataAdapter();
            dataAdapter.txtTopic = (TextView) rowView.findViewById(R.id.gradeStudentTextTopic);
            dataAdapter.txtGrade = (TextView) rowView.findViewById(R.id.gradeStudentTextGrade);
            rowView.setTag(dataAdapter);
            rowView.setTag(R.id.gradeStudentTextTopic, dataAdapter.txtTopic);
            rowView.setTag(R.id.gradeStudentTextGrade, dataAdapter.txtGrade);
        }
        else
        {
            dataAdapter = (DataAdapter) rowView.getTag();
        }
        StudentGrade studentGrade;
        studentGrade = (StudentGrade) this.getItem(position);
        if (TextUtils.isEmpty(studentGrade.getGrade())) {
            dataAdapter.txtGrade .setText("Not Graded");

        } else {
            dataAdapter.txtGrade .setText(studentGrade.getGrade());
        }
        if (TextUtils.isEmpty(studentGrade.getTopic())) {
            dataAdapter.txtTopic .setText("Not Available");
        } else {
            dataAdapter.txtTopic .setText(studentGrade.getTopic());
        }
        return rowView;
    }
    static class DataAdapter
    {
        TextView txtGrade;
        TextView txtTopic;
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public void add(Object object) {
        super.add(object);
        list.add(object);
    }

}
