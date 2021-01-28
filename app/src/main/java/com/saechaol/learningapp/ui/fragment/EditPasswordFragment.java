package com.saechaol.learningapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.ui.activity.HomeActivity;
import com.saechaol.learningapp.util.CommonUtil;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class EditPasswordFragment extends Fragment {

    EditText txtNewPassword;
    EditText txtConfirmPassword;
    View view;
    String userName, password;//the current user name in the system

    void getUserName() {
        Intent previous = this.getActivity().getIntent();
        Bundle bundle = previous.getExtras();
        if (bundle != null) {
            userName = (String) bundle.get("userName");
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_update_password, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_save:
                ((HomeActivity)getActivity()).hideKeyboard();

                if(txtNewPassword.getText().toString().isEmpty() || txtConfirmPassword.getText().toString().isEmpty()){
                    ((HomeActivity) getActivity()).showSnackBar("All fields are mandatory.", getView().findViewById(R.id.fragmentUpdatePasswordCoordinatorLayout));
                } else if (txtNewPassword.getText().toString().equals(txtConfirmPassword.getText().toString())) {
                    //passwords match, update the database
                    password = txtNewPassword.getText().toString();
                    if (CommonUtil.checkInternetConnection(getActivity())) {
                        UpdatePasswordAPI updatePasswordAPI = new UpdatePasswordAPI(EditPasswordFragment.this.getActivity());
                        updatePasswordAPI.execute();
                    }else{
                        ((HomeActivity) getActivity()).showSnackBar(getString(R.string.checkConnection), getView().findViewById(R.id.fragmentUpdatePasswordCoordinatorLayout));
                    }
                } else {

                    ((HomeActivity) getActivity()).showSnackBar("Both Passwords do not match", getView().findViewById(R.id.fragmentUpdatePasswordCoordinatorLayout));
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_changepassword, container, false);
        txtNewPassword = (EditText) view.findViewById(R.id.editPasswordTextNewPassword);
        txtConfirmPassword = (EditText) view.findViewById(R.id.editPasswordTextConfirmPassword);
        getUserName();
        return view;
    }

    //this class is needed to update the password without blocking the UI. once update the password, it will show poup message
    class UpdatePasswordAPI extends AsyncTask<Void, Void, String> {
        Context context;

        public UpdatePasswordAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {

            ((HomeActivity) getActivity()).showProgressDialog("Updating Password...");
        }

        @Override
        protected void onPostExecute(String statusCode) {
            if (statusCode.equals("302")) //the password is updated
            {
                txtNewPassword.setText("");
                txtConfirmPassword.setText("");
                ((HomeActivity) getActivity()).hideProgressDialog();
                ((HomeActivity) getActivity()).showSnackBar("Password has been changed.", getView().findViewById(R.id.fragmentUpdatePasswordCoordinatorLayout));

            } else {
                ((HomeActivity) getActivity()).hideProgressDialog();
                ((HomeActivity) getActivity()).showSnackBar("Password has not been updated.Please try again.", getView().findViewById(R.id.fragmentUpdatePasswordCoordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Call<String> callUpdatePass = Api.getClient().changePassword(userName, password);
                Response<String> response=callUpdatePass.execute();
                return "" + response.code();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
    }

}
