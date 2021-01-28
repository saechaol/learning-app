package com.saechaol.learningapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.SubjectDetails;
import com.saechaol.learningapp.ui.activity.HomeActivity;
import com.saechaol.learningapp.ui.adapter.AssignedSubjectAdapter;
import com.saechaol.learningapp.util.CommonUtil;
import com.saechaol.learningapp.util.PreferenceManager;
import com.saechaol.learningapp.util.VerticalSpaceItemDecoration;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class StudentSubjectViewFragment extends Fragment {

    com.saechaol.learningapp.ui.view.EmptyRecyclerView recyclerViewSubjects;
    AssignedSubjectAdapter subjectDisplayAdapter;
    PreferenceManager prefsManager;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_display_assigned_subject, container, false);
        recyclerViewSubjects = (com.saechaol.learningapp.ui.view.EmptyRecyclerView) view.findViewById(R.id.fragment_display_assigned_subject_recyclerView);
        recyclerViewSubjects.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewSubjects.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dividerList)));
        prefsManager = new PreferenceManager(getActivity());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CommonUtil.checkInternetConnection(getActivity())) {
            GetSubjectDetailsWithStudentAPI getSubjectDetails = new GetSubjectDetailsWithStudentAPI(this.getActivity());
            getSubjectDetails.execute();
        } else {
            ((HomeActivity) getActivity()).showSnackBar(getString(R.string.checkConnection), view.findViewById(R.id.fragment_display_assigned_subject_coordinatorLayout));
        }

    }

    class GetSubjectDetailsWithStudentAPI extends AsyncTask<Void, Void, List<SubjectDetails>> {
        Context context;

        public GetSubjectDetailsWithStudentAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((HomeActivity) getActivity()).showProgressDialog("Getting Subject Details...");
        }

        @Override
        protected void onPostExecute(List<SubjectDetails> listSubjectDetail) {

            ((HomeActivity) getActivity()).hideProgressDialog();
            List<SubjectDetails> listSubjectDetails = new ArrayList<SubjectDetails>();
            if (listSubjectDetail != null) {
                listSubjectDetails = listSubjectDetail;
            } else {
                ((HomeActivity) getActivity()).showSnackBar(getString(R.string.serverError), getView().findViewById(R.id.fragment_display_subject_coordinatorLayout));
            }
            subjectDisplayAdapter = new AssignedSubjectAdapter(context, listSubjectDetails);
            recyclerViewSubjects.setAdapter(subjectDisplayAdapter);
            recyclerViewSubjects.setEmptyView(getView().findViewById(R.id.fragment_display_assigned_subject_relEmptyView));
        }

        @Override
        protected List<SubjectDetails> doInBackground(Void... params) {
            try {
                Call<ArrayList<SubjectDetails>> callSubjectData = Api.getClient().getSubjectForStudent(prefsManager.getStringData("userName"));
                Response<ArrayList<SubjectDetails>> responseSubjectData = callSubjectData.execute();
                if (responseSubjectData.isSuccessful() && responseSubjectData.body() != null) {
                    return responseSubjectData.body();
                } else {
                    return null;
                }

            } catch (MalformedURLException e) {
                return null;

            } catch (IOException e) {
                return null;
            }
        }
    }

}
