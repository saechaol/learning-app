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
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.daimajia.swipe.util.Attributes;
import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.InstructorDetails;
import com.saechaol.learningapp.ui.activity.HomeActivity;
import com.saechaol.learningapp.ui.activity.ViewInstructorActivity;
import com.saechaol.learningapp.ui.adapter.InstructorAdapter;
import com.saechaol.learningapp.ui.adapter.OnItemClickListener;
import com.saechaol.learningapp.ui.view.EmptyRecyclerView;
import com.saechaol.learningapp.util.CommonUtil;
import com.saechaol.learningapp.util.VerticalSpaceItemDecoration;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class InstructorViewFragment extends Fragment {

    EmptyRecyclerView recyclerViewUsers;
    ArrayAdapter<String> adapter;
    InstructorAdapter userDisplayAdapter;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_instructorlist, container, false);
        recyclerViewUsers = (EmptyRecyclerView) view.findViewById(R.id.instructorDisplayRecyclerView);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewUsers.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dividerList)));

        view.findViewById(R.id.fragment_display_instructor_fabAddUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getActivity(), ViewInstructorActivity.class);
                intent.putExtra(CommonUtil.EXTRA_IS_TO_ADD, true);
                intent.putExtra(CommonUtil.EXTRA_EDIT_MODE, false);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CommonUtil.checkInternetConnection(getActivity())) {
            GetAllInstructorAPI getUserDetails = new GetAllInstructorAPI(this.getActivity());
            getUserDetails.execute();
        } else {
            ((HomeActivity) getActivity()).showSnackBar(getString(R.string.checkConnection), view.findViewById(R.id.fragment_display_instructor_coordinatorLayout));
        }
    }

    class GetAllInstructorAPI extends AsyncTask<Void, Void, List<InstructorDetails>> {
        Context context;

        public GetAllInstructorAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((HomeActivity) getActivity()).showProgressDialog("Getting Instructor User Data...");
        }

        @Override
        protected void onPostExecute(List<InstructorDetails> userDetails) {

            ((HomeActivity) getActivity()).hideProgressDialog();
            List<InstructorDetails> listUserDetails = new ArrayList<InstructorDetails>();
            if (userDetails != null) {
                listUserDetails = userDetails;
            } else {
                ((HomeActivity) getActivity()).showSnackBar(getString(R.string.serverError), getView().findViewById(R.id.fragment_display_instructor_coordinatorLayout));
            }
            userDisplayAdapter = new InstructorAdapter(context, listUserDetails, new OnItemClickListener<InstructorDetails>() {
                @Override
                public void onItemClick(InstructorDetails item, int resourceId) {
                    Log.d("OnItemClick", "resource:" + resourceId);
                    if (resourceId == R.id.userItemDisplayLayoutImgEditUser) {
                        final Intent intent = new Intent(getActivity(), ViewInstructorActivity.class);
                        intent.putExtra(CommonUtil.EXTRA_IS_TO_ADD, false);
                        intent.putExtra(CommonUtil.EXTRA_EDIT_MODE, true);
                        intent.putExtra(CommonUtil.EXTRA_USER_ADMIN_DATA, item);
                        startActivity(intent);
                    } else if (resourceId == R.id.userItemDisplayLayoutImgDeleteUser) {
                        if (CommonUtil.checkInternetConnection(getActivity())) {
                            DeleteInstructorAPI deleteInstructorTask = new DeleteInstructorAPI(InstructorViewFragment.this.getActivity());
                            deleteInstructorTask.execute(item.getInstructorId());
                        } else {
                            ((HomeActivity) getActivity()).showSnackBar(getString(R.string.checkConnection), view.findViewById(R.id.fragment_display_admin_coordinatorLayout));
                        }

                    } else if (resourceId == R.id.userItemDisplayLayoutSwipeParent) {
                        final Intent intent = new Intent(getActivity(), ViewInstructorActivity.class);
                        intent.putExtra(CommonUtil.EXTRA_IS_TO_ADD, false);
                        intent.putExtra(CommonUtil.EXTRA_EDIT_MODE, false);
                        intent.putExtra(CommonUtil.EXTRA_USER_ADMIN_DATA, item);
                        startActivity(intent);
                    }

                }
            });
            userDisplayAdapter.setMode(Attributes.Mode.Single);
            recyclerViewUsers.setAdapter(userDisplayAdapter);
            recyclerViewUsers.setEmptyView(getView().findViewById(R.id.fragment_display_instructor_relEmptyView));
        }

        @Override
        protected List<InstructorDetails> doInBackground(Void... params) {

            try {
                Call<List<InstructorDetails>> callAdminUserData = Api.getClient().getInstructors();
                Response<List<InstructorDetails>> responseAdminUser = callAdminUserData.execute();
                if (responseAdminUser.isSuccessful() && responseAdminUser.body() != null) {
                    return responseAdminUser.body();
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

    class DeleteInstructorAPI extends AsyncTask<String, Void, String> {
        Context context;

        public DeleteInstructorAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((HomeActivity) getActivity()).showProgressDialog("Removing Instructor...");

        }

        @Override
        protected void onPostExecute(String statusCode) {
            ((HomeActivity) getActivity()).hideProgressDialog();

            if (statusCode != null && statusCode.equals("302")) //the tasks are deleted
            {
                ((HomeActivity) getActivity()).showSnackBar("Instructor User has been removed.", getView().findViewById(R.id.fragment_display_instructor_coordinatorLayout));
                GetAllInstructorAPI getUserDetails = new GetAllInstructorAPI(InstructorViewFragment.this.getActivity());
                getUserDetails.execute();

            } else {
                ((HomeActivity) getActivity()).showSnackBar(getString(R.string.serverError), getView().findViewById(R.id.fragment_display_instructor_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Call<String> callDelete = Api.getClient().removeInstructor(params[0]);
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
