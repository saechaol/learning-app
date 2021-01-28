package com.saechaol.learningapp.ui.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.AdminDetails;
import com.saechaol.learningapp.model.InstructorDetails;
import com.saechaol.learningapp.model.StudentDetails;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MyInformationFragment extends Fragment {


    //    TextView txtUserId;
    TextView txtUserName;
    TextView txtFirstName;
    TextView txtLastName;
    TextView txtEmailId;
    TextView txtTelephone;
    TextView txtAliasMailId;
    TextView txtAddress;
//    TextView txtHangoutId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myinformation,container,false);

        txtUserName = (TextView) view.findViewById(R.id.myInfoTextUserName);
        txtFirstName= (TextView) view.findViewById(R.id.myInfoTextFirstName);
        txtLastName=(TextView) view.findViewById(R.id.myInfoTextLastName);
        txtEmailId = (TextView) view.findViewById(R.id.myInfoTextLastName);
        txtTelephone = (TextView) view.findViewById(R.id.myInfoTextTelephone);
        txtAliasMailId = (TextView) view.findViewById(R.id.myInfoTextAliasMailId);
        txtAddress = (TextView) view.findViewById(R.id.myInfoTextAddress);

        GetUserInformationAPI getUserDetails=new GetUserInformationAPI();
        Bundle bundle = getActivity().getIntent().getExtras();
        getUserDetails.execute(bundle.get("userName").toString(),bundle.get("userType").toString());
        return view;

    }
    //GetStudentByUserName
    //GetAdminByUserName  Admin
    class GetUserInformationAPI extends AsyncTask<String,Void,Void>
    {
        AdminDetails adminUserDetails =new AdminDetails();
        InstructorDetails instructorDetails=new InstructorDetails();
        StudentDetails studentDetails=new StudentDetails();
        String userType;
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void result) {
            if(userType != null  && userType.equals("admin") ) {

                txtUserName.setText(adminUserDetails.getAdminId());
                txtFirstName.setText(adminUserDetails.firstName);
                txtLastName.setText(adminUserDetails.lastName);
                txtEmailId.setText(adminUserDetails.email);
                txtTelephone.setText(adminUserDetails.getPhone());
                txtAliasMailId.setText(adminUserDetails.aliasMailId);
                txtAddress.setText(adminUserDetails.address);

            }else if(userType != null  && userType.equals("student") ) {

                txtUserName.setText(studentDetails.getStudentId());
                txtFirstName.setText(studentDetails.firstName);
                txtLastName.setText(studentDetails.lastName);
                txtEmailId.setText(studentDetails.email);
                txtTelephone.setText(studentDetails.getPhone());
                txtAliasMailId.setText(studentDetails.aliasMailId);
                txtAddress.setText(studentDetails.address);
            }else if(userType != null  && userType.equals("instructor") ) {

                txtUserName.setText(instructorDetails.getInstructorId());
                txtFirstName.setText(instructorDetails.firstName);
                txtLastName.setText(instructorDetails.lastName);
                txtEmailId.setText(instructorDetails.email);
                txtTelephone.setText(instructorDetails.getPhone());
                txtAliasMailId.setText(instructorDetails.aliasMailId);
                txtAddress.setText(instructorDetails.address);
                //txtHangoutId.setText(instructorDetails.skypeId);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            userType=params[1];
            if(params[1].equals("admin")) {
                try {
                    Call<List<AdminDetails>> callAdminData = Api.getClient().getAdminInfo(params[0]);
                    Response<List<AdminDetails>> responseAdminData = callAdminData.execute();
                    if (responseAdminData.isSuccessful() && responseAdminData.body() != null&&responseAdminData.body() .size()>0) {
                        adminUserDetails= responseAdminData.body().get(0);
                    }

                } catch (MalformedURLException e) {
                    return null;

                } catch (IOException e) {
                    return null;
                }
            }
            else if(params[1].equals("student"))
            {
                try {
                    Call<List<StudentDetails>> callStudentData = Api.getClient().getStudentInfo(params[0]);
                    Response<List<StudentDetails>> responseStudentData = callStudentData.execute();
                    if (responseStudentData.isSuccessful() && responseStudentData.body() != null&&responseStudentData.body() .size()>0) {
                        studentDetails= responseStudentData.body().get(0);
                    }

                } catch (MalformedURLException e) {
                    return null;

                } catch (IOException e) {
                    return null;
                }
            }
            else if(params[1].equals("instructor"))
            {
                try {
                    Call<List<InstructorDetails>> callInstData = Api.getClient().getInstructorInfo(params[0]);
                    Response<List<InstructorDetails>> responseInstData = callInstData.execute();
                    if (responseInstData.isSuccessful() && responseInstData.body() != null&&responseInstData.body() .size()>0) {
                        instructorDetails= responseInstData.body().get(0);
                    }

                } catch (MalformedURLException e) {
                    return null;

                } catch (IOException e) {
                    return null;
                }
            }

            return null;
        }
    }

}
