package com.saechaol.learningapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.daimajia.swipe.util.Attributes;
import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.StudentDetails;
import com.saechaol.learningapp.sinch.PlaceCallActivity;
import com.saechaol.learningapp.ui.adapter.OnItemClickListener;
import com.saechaol.learningapp.ui.adapter.StudentAdapter;
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

public class CallStudentActivity extends BaseActivity {
    EmptyRecyclerView recyclerViewUsers;
    List<StudentDetails> userDetails = new ArrayList<StudentDetails>();

    ArrayAdapter<String> adapter;
    StudentAdapter userDisplayAdapter;
    public static final String SUBJ_ID = "SUBJ_ID";

    View view;
    String subjId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_studentslist);
        subjId = getIntent().getStringExtra(SUBJ_ID);
        recyclerViewUsers = (EmptyRecyclerView) findViewById(R.id.studentDisplayRecyclerView);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dividerList)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setToolbarTitle("Call Student");
        findViewById(R.id.fragment_display_student_fabAddUser).setVisibility(View.GONE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CommonUtil.checkInternetConnection(this)) {
            GetEnrollBySubjectAPI mlaGetEnrollBySubjectAPI = new GetEnrollBySubjectAPI(this);
            mlaGetEnrollBySubjectAPI.execute();
        } else {
            showSnackBar(getString(R.string.checkConnection), view.findViewById(R.id.fragment_display_admin_coordinatorLayout));
        }

    }

    class GetEnrollBySubjectAPI extends AsyncTask<Void, Void, List<StudentDetails>> {
        Context context;

        public GetEnrollBySubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog("Getting Student User Data...");

        }

        @Override
        protected void onPostExecute(List<StudentDetails> userDetails) {

            hideProgressDialog();
            List<StudentDetails> listUserDetails = new ArrayList<StudentDetails>();
            if (userDetails != null) {
                listUserDetails = userDetails;


            } else {


                showSnackBar(getString(R.string.serverError), findViewById(R.id.fragment_display_admin_coordinatorLayout));
            }

            userDisplayAdapter = new StudentAdapter(context, listUserDetails, false, new OnItemClickListener<StudentDetails>() {
                @Override
                public void onItemClick(StudentDetails item, int resourceId) {
                    Log.d("OnItemClick", "resource:" + resourceId);
                    if (resourceId == R.id.userItemDisplayLayoutSwipeParent) {
                        final Intent intent = new Intent(CallStudentActivity.this, PlaceCallActivity.class);
                        intent.putExtra(PlaceCallActivity.CALL_ID, item.getStudentId());
                        intent.putExtra(PlaceCallActivity.VIDEO_CALL, getIntent().getBooleanExtra(PlaceCallActivity.VIDEO_CALL
                                , false));
                        startActivity(intent);
                    }

                }
            });
            ((StudentAdapter) userDisplayAdapter).setMode(Attributes.Mode.Single);
            recyclerViewUsers.setAdapter(userDisplayAdapter);
            recyclerViewUsers.setEmptyView(findViewById(R.id.fragment_display_student_relEmptyView));


        }

        @Override
        protected List<StudentDetails> doInBackground(Void... params) {

            try {
                Call<List<StudentDetails>> callAdminUserData = Api.getClient().getEnrollBySubject(subjId);
                Response<List<StudentDetails>> responseAdminUser = callAdminUserData.execute();
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

}