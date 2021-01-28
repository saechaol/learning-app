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
import com.saechaol.learningapp.util.Predicate;
import com.saechaol.learningapp.util.PreferenceManager;
import com.saechaol.learningapp.util.UserTypeData;
import com.saechaol.learningapp.util.VerticalSpaceItemDecoration;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class AssignedSubjectViewFragment extends Fragment {

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
            GetAllSubjectAPI getAllSubjectAPI = new GetAllSubjectAPI(this.getActivity());
            getAllSubjectAPI.execute();
        } else {
            ((HomeActivity) getActivity()).showSnackBar(getString(R.string.checkConnection), view.findViewById(R.id.fragment_display_assigned_subject_coordinatorLayout));
        }

    }

    class GetAllSubjectAPI extends AsyncTask<Void, Void, List<SubjectDetails>> {
        Context context;

        public GetAllSubjectAPI(Context ctx) {
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
                listSubjectDetails = filter(listSubjectDetail, filterPredicate);
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
                Call<List<SubjectDetails>> callSubjectData = Api.getClient().getAllSubject();
                Response<List<SubjectDetails>> responseSubjectData = callSubjectData.execute();
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

    Predicate<SubjectDetails> filterPredicate = (obj) -> {
        if (prefsManager.getStringData("userType").equals(UserTypeData.INSTRUCTOR) && obj.getInstructorId().equals(prefsManager.getStringData("userName"))) {
            return true;
        }
        return false;
    };

    public static <T> ArrayList<T> filter(Collection<T> source, Predicate<T> predicate) {
        ArrayList<T> result = new ArrayList<T>();
        for (T element : source) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

}
