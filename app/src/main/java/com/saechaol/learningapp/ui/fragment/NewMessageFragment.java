package com.saechaol.learningapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.pchmn.materialchips.ChipsInput;
import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.AdminDetails;
import com.saechaol.learningapp.model.ContactChip;
import com.saechaol.learningapp.model.InstructorDetails;
import com.saechaol.learningapp.model.StudentDetails;
import com.saechaol.learningapp.ui.activity.HomeActivity;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class NewMessageFragment extends Fragment {

    View view;
    ChipsInput chipsInput;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_send, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_send) {
            List<ContactChip> contactsSelected = (List<ContactChip>) chipsInput.getSelectedChipList();

            if (contactsSelected == null || contactsSelected.size() == 0) {
                ((HomeActivity) getActivity()).showSnackBar("Please select a recipient to send a message.", getView().findViewById(R.id.fragment_new_message_coordinatorLayout));
            } else {
                String[] emaiiArray = new String[contactsSelected.size()];
                for (int i = 0; i < contactsSelected.size(); i++) {
                    emaiiArray[i] = contactsSelected.get(i).getEmail();
                }
                final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                final PackageManager pm = getActivity().getPackageManager();
                final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
                ResolveInfo best = null;
                for (final ResolveInfo info : matches)
                    if (info.activityInfo.packageName.endsWith(".gm") ||
                            info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
                if (best != null)
                    intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);

                intent.putExtra(Intent.EXTRA_EMAIL, emaiiArray);
                startActivity(intent);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_newmessage, container, false);
        setHasOptionsMenu(true);
        chipsInput = (ChipsInput) view.findViewById(R.id.chips_input);
        GetAllUsersDetailsAPI getUserDetails = new GetAllUsersDetailsAPI(getActivity());
        getUserDetails.execute();
        return view;
    }

    class GetAllUsersDetailsAPI extends AsyncTask<Void, Void, List<ContactChip>> {
        Context context;

        public GetAllUsersDetailsAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((HomeActivity) getActivity()).showProgressDialog("Getting User Details...");
        }

        @Override
        protected void onPostExecute(List<ContactChip> userDetails) {
            ((HomeActivity) getActivity()).hideProgressDialog();
            //check if the call to api passed
            if (userDetails != null) {
                chipsInput.setFilterableList(userDetails);
            } else {
                ((HomeActivity) getActivity()).showSnackBar(getString(R.string.serverError), getView().findViewById(R.id.fragment_new_message_coordinatorLayout));
            }
        }

        @Override
        protected List<ContactChip> doInBackground(Void... params) {
            List<ContactChip> listContactChip = new ArrayList<ContactChip>();

            try {
                Call<List<AdminDetails>> callAdminUserData = Api.getClient().getAdmins();
                Response<List<AdminDetails>> responseAdminUser = callAdminUserData.execute();
                if (responseAdminUser.isSuccessful() && responseAdminUser.body() != null) {
                    for (AdminDetails adminUserDetail : responseAdminUser.body()
                    ) {
                        ContactChip contactChip = new ContactChip(adminUserDetail.getAdminId(), adminUserDetail.getEmail(), adminUserDetail.getFirstName() + " " +adminUserDetail.getLastName(), "Admin");
                        listContactChip.add(contactChip);
                    }
                }

                Call<List<InstructorDetails>> callInstUserData = Api.getClient().getInstructors();
                Response<List<InstructorDetails>> responseInstUser = callInstUserData.execute();
                if (responseInstUser.isSuccessful() && responseInstUser.body() != null) {
                    for (InstructorDetails instUserDetail : responseInstUser.body()
                    ) {
                        ContactChip contactChip = new ContactChip(instUserDetail.getInstructorId(), instUserDetail.getEmail(), instUserDetail.getFirstName() +" " + instUserDetail.getLastName(), "Instructor");
                        listContactChip.add(contactChip);
                    }
                }

                Call<List<StudentDetails>> callStudentUserData = Api.getClient().getStudents();
                Response<List<StudentDetails>> responseStudentUser = callStudentUserData.execute();
                if (responseStudentUser.isSuccessful() && responseStudentUser.body() != null) {
                    for (StudentDetails studentUserDetail : responseStudentUser.body()
                    ) {
                        ContactChip contactChip = new ContactChip(studentUserDetail.getStudentId(), studentUserDetail.getEmail(), studentUserDetail.getFirstName()+" " + studentUserDetail.getLastName(), "Student");
                        listContactChip.add(contactChip);
                    }
                }
                return listContactChip;
            } catch (MalformedURLException e) {
                return null;

            } catch (IOException e) {
                return null;
            }
        }

    }

}
