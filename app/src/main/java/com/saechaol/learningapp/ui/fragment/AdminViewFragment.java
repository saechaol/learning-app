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
import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.AdminDetails;
import com.saechaol.learningapp.ui.activity.HomeActivity;
import com.saechaol.learningapp.ui.activity.ViewAdminActivity;
import com.saechaol.learningapp.ui.adapter.AdminAdapter;
import com.saechaol.learningapp.ui.adapter.OnItemClickListener;
import com.saechaol.learningapp.util.CommonUtil;
import com.saechaol.learningapp.util.VerticalSpaceItemDecoration;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class AdminViewFragment extends Fragment {

    com.saechaol.learningapp.ui.view.EmptyRecyclerView recyclerViewUsers;
    AdminAdapter userDisplayAdapter;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_adminlist, container, false);
        recyclerViewUsers = (com.saechaol.learningapp.ui.view.EmptyRecyclerView) view.findViewById(R.id.adminDisplayRecyclerView);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewUsers.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dividerList)));

        view.findViewById(R.id.fragment_display_admin_fabAddUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getActivity(), ViewAdminActivity.class);
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
            GetAllAdminAPI getUserDetails = new GetAllAdminAPI(this.getActivity());
            getUserDetails.execute();
        } else {
            ((HomeActivity) getActivity()).showSnackBar(getString(R.string.checkConnection), view.findViewById(R.id.fragment_display_admin_coordinatorLayout));
        }
    }

    class GetAllAdminAPI extends AsyncTask<Void, Void, List<AdminDetails>> {
        Context context;

        public GetAllAdminAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((HomeActivity) getActivity()).showProgressDialog("Fetching all Admin...");
        }

        @Override
        protected void onPostExecute(List<AdminDetails> userDetails) {

            ((HomeActivity) getActivity()).hideProgressDialog();
            List<AdminDetails> listUserDetails = new ArrayList<AdminDetails>();
            if (userDetails != null) {
                listUserDetails = userDetails;
            } else {
                ((HomeActivity) getActivity()).showSnackBar(getString(R.string.serverError), getView().findViewById(R.id.fragment_display_admin_coordinatorLayout));
            }

            userDisplayAdapter = new AdminAdapter(context, listUserDetails, new OnItemClickListener<AdminDetails>() {
                @Override
                public void onItemClick(AdminDetails item, int resourceId) {
                    Log.d("OnItemClick", "resource:" + resourceId);
                    if (resourceId == R.id.userItemDisplayLayoutImgEditUser) {
                        final Intent intent = new Intent(getActivity(), ViewAdminActivity.class);
                        intent.putExtra(CommonUtil.EXTRA_IS_TO_ADD, false);
                        intent.putExtra(CommonUtil.EXTRA_EDIT_MODE, true);
                        intent.putExtra(CommonUtil.EXTRA_USER_ADMIN_DATA, item);
                        startActivity(intent);
                    } else if (resourceId == R.id.userItemDisplayLayoutImgDeleteUser) {
                        if (CommonUtil.checkInternetConnection(getActivity())) {
                            PostAdminRmvAPI postAdminRmvAPI = new PostAdminRmvAPI(AdminViewFragment.this.getActivity());
                            postAdminRmvAPI.execute(item.getAdminId());
                        } else {
                            ((HomeActivity) getActivity()).showSnackBar(getString(R.string.checkConnection), view.findViewById(R.id.fragment_display_admin_coordinatorLayout));
                        }

                    } else if (resourceId == R.id.userItemDisplayLayoutSwipeParent) {
                        final Intent intent = new Intent(getActivity(), ViewAdminActivity.class);
                        intent.putExtra(CommonUtil.EXTRA_IS_TO_ADD, false);
                        intent.putExtra(CommonUtil.EXTRA_EDIT_MODE, false);

                        intent.putExtra(CommonUtil.EXTRA_USER_ADMIN_DATA, item);
                        startActivity(intent);
                    }

                }
            });
            userDisplayAdapter.setMode(Attributes.Mode.Single);
            recyclerViewUsers.setAdapter(userDisplayAdapter);
            recyclerViewUsers.setEmptyView(getView().findViewById(R.id.fragment_display_admin_relEmptyView));
        }

        @Override
        protected List<AdminDetails> doInBackground(Void... params) {

            try {
                Call<List<AdminDetails>> callAdminUserData = Api.getClient().getAdmins();
                Response<List<AdminDetails>> responseAdminUser = callAdminUserData.execute();
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

    class PostAdminRmvAPI extends AsyncTask<String, Void, String> {
        Context context;

        public PostAdminRmvAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((HomeActivity) getActivity()).showProgressDialog("Removing Admin...");
        }

        @Override
        protected void onPostExecute(String statusCode) {
            ((HomeActivity) getActivity()).hideProgressDialog();

            if (statusCode != null && statusCode.equals("302")) //the tasks are deleted
            {
                ((HomeActivity) getActivity()).showSnackBar("The admin has been removed.", getView().findViewById(R.id.fragment_display_admin_coordinatorLayout));
                GetAllAdminAPI getUserDetails = new GetAllAdminAPI(AdminViewFragment.this.getActivity());
                getUserDetails.execute();

            } else {
                ((HomeActivity) getActivity()).showSnackBar(getString(R.string.serverError), getView().findViewById(R.id.fragment_display_admin_coordinatorLayout));

            }
        }

        @Override
        protected String doInBackground(String... params) {
            Call<String> callDelete = Api.getClient().removeAdmin(params[0]);
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
