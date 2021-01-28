package com.saechaol.learningapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.daimajia.swipe.util.Attributes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.SubjectDetails;
import com.saechaol.learningapp.ui.activity.HomeActivity;
import com.saechaol.learningapp.ui.activity.ViewSubjectActivity;
import com.saechaol.learningapp.ui.adapter.OnItemClickListener;
import com.saechaol.learningapp.ui.adapter.SubjectAdapter;
import com.saechaol.learningapp.util.CommonUtil;
import com.saechaol.learningapp.util.PreferenceManager;
import com.saechaol.learningapp.util.UserTypeData;
import com.saechaol.learningapp.util.VerticalSpaceItemDecoration;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class SubjectViewFragment extends Fragment {

    com.saechaol.learningapp.ui.view.EmptyRecyclerView recyclerViewSubjects;
    SubjectAdapter subjectDisplayAdapter;
    private PreferenceManager manager;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_subjectslist, container, false);
        recyclerViewSubjects = (com.saechaol.learningapp.ui.view.EmptyRecyclerView) view.findViewById(R.id.displaySubjectRecyclerView);
        recyclerViewSubjects.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewSubjects.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dividerList)));
        manager = new PreferenceManager(getActivity());
        FloatingActionButton btnAddStudent = (FloatingActionButton) view.findViewById(R.id.fragment_display_subject_fabAddUser);

        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getActivity(), ViewSubjectActivity.class);
                intent.putExtra(CommonUtil.EXTRA_IS_TO_ADD, true);
                intent.putExtra(CommonUtil.EXTRA_EDIT_MODE, false);
                startActivity(intent);
            }
        });

        if (manager.getStringData("userType").equals(UserTypeData.STUDENT)) {
            btnAddStudent.setVisibility(View.GONE);
        } else {
            btnAddStudent.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CommonUtil.checkInternetConnection(getActivity())) {
            GetAllSubjectDetailsAPI getSubjectDetails = new GetAllSubjectDetailsAPI(this.getActivity());
            getSubjectDetails.execute();
        } else {
            ((HomeActivity) getActivity()).showSnackBar(getString(R.string.checkConnection), view.findViewById(R.id.fragment_display_subject_coordinatorLayout));
        }

    }

    class GetAllSubjectDetailsAPI extends AsyncTask<Void, Void, List<SubjectDetails>> {
        Context context;

        public GetAllSubjectDetailsAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((HomeActivity) getActivity()).showProgressDialog("Getting Subject Data...");
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

            subjectDisplayAdapter = new SubjectAdapter(context, listSubjectDetails, manager.getStringData("userType").equals(UserTypeData.ADMIN), new OnItemClickListener<SubjectDetails>() {
                @Override
                public void onItemClick(SubjectDetails item, int resourceId) {
                    Log.d("OnItemClick", "resource:" + resourceId);
                    if (resourceId == R.id.subject_item_display_layout_imgEditUser) {
                        final Intent intent = new Intent(getActivity(), ViewSubjectActivity.class);
                        intent.putExtra(CommonUtil.EXTRA_IS_TO_ADD, false);
                        intent.putExtra(CommonUtil.EXTRA_EDIT_MODE, true);
                        intent.putExtra(CommonUtil.EXTRA_USER_ADMIN_DATA, item);
                        startActivity(intent);
                    } else if (resourceId == R.id.subject_item_display_layout_imgDeleteUser) {
                        if (CommonUtil.checkInternetConnection(getActivity())) {
                            DeleteSubjectAPI deleteSubjectTask = new DeleteSubjectAPI(SubjectViewFragment
                                    .this.getActivity());
                            deleteSubjectTask.execute(item.getSubjectId());
                        } else {
                            ((HomeActivity) getActivity()).showSnackBar(getString(R.string.checkConnection), view.findViewById(R.id.fragment_display_subject_coordinatorLayout));
                        }

                    } else if (resourceId == R.id.subject_item_display_layout_swipeParent) {
                        final Intent intent = new Intent(getActivity(), ViewSubjectActivity.class);
                        intent.putExtra(CommonUtil.EXTRA_IS_TO_ADD, false);
                        intent.putExtra(CommonUtil.EXTRA_EDIT_MODE, false);
                        intent.putExtra(CommonUtil.EXTRA_USER_ADMIN_DATA, item);
                        startActivity(intent);
                    }
                }
            });
            subjectDisplayAdapter.setMode(Attributes.Mode.Single);
            recyclerViewSubjects.setAdapter(subjectDisplayAdapter);
            recyclerViewSubjects.setEmptyView(getView().findViewById(R.id.fragment_display_subject_relEmptyView));
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

    class DeleteSubjectAPI extends AsyncTask<String, Void, String> {
        Context context;

        public DeleteSubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((HomeActivity) getActivity()).showProgressDialog("Removing Subject...");
        }

        @Override
        protected void onPostExecute(String statusCode) {
            ((HomeActivity) getActivity()).hideProgressDialog();

            if (statusCode != null && statusCode.equals("302")) //the tasks are deleted
            {
                ((HomeActivity) getActivity()).showSnackBar("The subject has been removed.", getView().findViewById(R.id.fragment_display_subject_coordinatorLayout));
                GetAllSubjectDetailsAPI getUserDetails = new GetAllSubjectDetailsAPI(SubjectViewFragment.this.getActivity());
                getUserDetails.execute();
            } else {
                ((HomeActivity) getActivity()).showSnackBar(getString(R.string.serverError), getView().findViewById(R.id.fragment_display_subject_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Call<String> callDelete = Api.getClient().removeSubject(params[0]);
            try {
                Response<String> responseDelete = callDelete.execute();
                if (responseDelete != null) {
                    return responseDelete.code() + "";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
