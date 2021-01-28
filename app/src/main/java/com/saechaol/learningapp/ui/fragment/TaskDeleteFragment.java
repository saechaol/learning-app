package com.saechaol.learningapp.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
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
import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.SubjectDetails;
import com.saechaol.learningapp.service.AlertTaskIntentService;
import com.saechaol.learningapp.ui.activity.HomeActivity;
import com.saechaol.learningapp.ui.adapter.OnItemClickListener;
import com.saechaol.learningapp.ui.adapter.SubjectAdapter;
import com.saechaol.learningapp.ui.view.EmptyRecyclerView;
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

public class TaskDeleteFragment extends Fragment {

    EmptyRecyclerView listView;
    List<SubjectDetails> subjectDetails = new ArrayList<SubjectDetails>();
    SubjectAdapter subjectDisplayAdapter;

    PreferenceManager prefsManager;
    int index = -1;
    String selectedSubject = "";

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tasksdelete, container, false);
        prefsManager=new PreferenceManager(getActivity());
        listView = (EmptyRecyclerView) view.findViewById(R.id.subjectDisplayListView);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dividerList)));

        GetAllSubjectWithTaskAPI getAllSubjectWithTaskAPI = new GetAllSubjectWithTaskAPI(this.getActivity());
        getAllSubjectWithTaskAPI.execute();

        return view;
    }


    class PostTaskRmvAPI extends AsyncTask<Void, Void, String> {
        Context context;

        public PostTaskRmvAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {

            ((HomeActivity) getActivity()).showProgressDialog("Removing task for Subject...");
        }

        @Override
        protected void onPostExecute(String statusCode) {

            ((HomeActivity) getActivity()).hideProgressDialog();
            if (statusCode.equals("302")) //the tasks are deleted
            {
                final Intent intentService = new Intent(getActivity(), AlertTaskIntentService.class);
                getActivity().startService(intentService);
                ((HomeActivity) getActivity()).showSnackBar("Removed all tasks for Subject:"+selectedSubject.toString(), getView().findViewById(R.id.fragment_tasks_delete_coordinatorLayout));
                subjectDetails.remove(index);
                index = -1;
                listView = (EmptyRecyclerView) view.findViewById(R.id.subjectDisplayListView);
                listView.setLayoutManager(new LinearLayoutManager(getActivity()));

            } else {
                ((HomeActivity) getActivity()).showSnackBar(getString(R.string.serverError), getView().findViewById(R.id.fragment_tasks_delete_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            Call<String> callDelete = Api.getClient().removeTasks(selectedSubject.toString());
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

    class GetAllSubjectWithTaskAPI extends AsyncTask<Void, Void, List<SubjectDetails>> {
        Context context;

        public GetAllSubjectWithTaskAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((HomeActivity) getActivity()).showProgressDialog("Getting Subjects...");
        }

        @Override
        protected void onPostExecute(List<SubjectDetails> userDetails2) {
            ((HomeActivity) getActivity()).hideProgressDialog();

            if (userDetails2 != null) {
                subjectDetails = new ArrayList<>();

                subjectDetails = filter(userDetails2, filterPredicate);


                subjectDisplayAdapter = new SubjectAdapter(context, subjectDetails, false, new OnItemClickListener<SubjectDetails>() {
                    @Override
                    public void onItemClick(SubjectDetails item, int resourceId) {
                        Log.d("OnItemClick", "resource:" + resourceId);
                        if (resourceId == R.id.subject_item_display_layout_swipeParent) {
                            selectedSubject = item.subjectId;
                            index = subjectDetails.indexOf(item);

                            AlertDialog.Builder builder=new AlertDialog.Builder(TaskDeleteFragment.this.getActivity());
                            builder.setTitle(getString(R.string.app_name));
                            builder.setMessage("Are you sure want to delete tasks for Subject:"+selectedSubject+"?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    PostTaskRmvAPI postTaskRmvAPI = new PostTaskRmvAPI(TaskDeleteFragment.this.getActivity());
                                    postTaskRmvAPI.execute();
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    index = -1;
                                }
                            });
                            builder.show();
                        }


                    }
                });
                subjectDisplayAdapter.setMode(Attributes.Mode.Single);
                listView.setAdapter(subjectDisplayAdapter);

            }
        }

        @Override
        protected List<SubjectDetails> doInBackground(Void... params) {
            try {
                Call<List<SubjectDetails>> callSubjectData = Api.getClient().getAllSubjectWithTask("true"); // True key is used, it indicates that we need to fetch all the subjects which has a schedule. So, we can remove a schedule for it.dis

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


    Predicate<SubjectDetails> filterPredicate = new Predicate<SubjectDetails>() {
        public boolean apply(SubjectDetails obj) {
            if(prefsManager.getStringData("userType").equals(UserTypeData.ADMIN)){
                return true;
            }
            else if (prefsManager.getStringData("userType").equals(UserTypeData.INSTRUCTOR) && obj.getInstructorId().equals(prefsManager.getStringData("userName"))) {
                return true;
            }
            return false;
        }

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
