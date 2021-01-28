package com.saechaol.learningapp.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.ui.activity.HomeActivity;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_mla_myinformation,container,false);

        View view = inflater.inflate(R.layout.fragment_home,container,false);

        final Button btnCourse=(Button)view.findViewById(R.id.fragment_home_btnCourses);
        btnCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity)getActivity()).openCourse();
            }
        });


        final Button btnInstruction=(Button)view.findViewById(R.id.fragment_home_btnInstructions);

        btnInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity)getActivity()).openInstruction();

            }
        });

        final Button btnStudents=(Button)view.findViewById(R.id.fragment_home_btnStudents);
        btnStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity)getActivity()).openStudent();

            }
        });

        return view;

    }

}
