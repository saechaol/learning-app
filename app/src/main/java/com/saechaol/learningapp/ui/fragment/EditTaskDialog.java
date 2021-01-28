package com.saechaol.learningapp.ui.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.ui.activity.HomeActivity;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;

import retrofit2.Call;
import retrofit2.Response;

public class EditTaskDialog extends DialogFragment {

    View view;
    Button btnSave;
    String idTask;
    EditText topic;
    EditText description;
    static EditTaskFragment taskFragment;

    public static final EditTaskDialog newInstance(EditTaskFragment taskFragmentobj, String idTask, String topic, String description) {
        EditTaskDialog fragment = new EditTaskDialog();
        taskFragment = taskFragmentobj;
        Bundle bundle = new Bundle(2);
        bundle.putString("idTask", idTask);
        bundle.putString("topic", topic);
        bundle.putString("description", description);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edittask, null);
        topic = (EditText) view.findViewById(R.id.updateBoxTextTopic);
        description = (EditText) view.findViewById(R.id.updateBoxTextDescription);
        idTask = getArguments().getString("idTask");
        topic.setText(getArguments().getString("topic"));
        description.setText(getArguments().getString("description"));


        btnSave = (Button) view.findViewById(R.id.updateBoxButtonSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View focusedView=null;
                if (EditTaskDialog.this.topic.hasFocus()) {
                    focusedView = EditTaskDialog.this.topic;
                } else if (EditTaskDialog.this.description.hasFocus()) {
                    focusedView = EditTaskDialog.this.description;
                }
                if (focusedView != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }
                UpdateTaskAPI updateTaskAPI = new UpdateTaskAPI(EditTaskDialog.this.getActivity());
                updateTaskAPI.execute();

            }
        });

        return view;

    }

    class UpdateTaskAPI extends AsyncTask<Void, Void, String> {
        Context context;
        String topic, desc;

        public UpdateTaskAPI(Context ctx) {
            context = ctx;
            topic = EditTaskDialog.this.topic.getText().toString();
            desc = EditTaskDialog.this.description.getText().toString();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String statusCode) {
            if (statusCode != null && statusCode.equals("302")) //the password is updated
            {
                ((HomeActivity) EditTaskDialog.this.getActivity()).showSnackBar("Task has been updated", view.findViewById(R.id.fragment_update_task_dialog_coordinatorLayout));
            } else {
                ((HomeActivity) EditTaskDialog.this.getActivity()).showSnackBar("Task has not been updated", view.findViewById(R.id.fragment_update_task_dialog_coordinatorLayout));

            }

            taskFragment.refresh();
            dismiss();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Call<String> callUpdateTask = Api.getClient().updateTaskData(idTask, topic, desc);
                Response<String> responseUpdatetask = callUpdateTask.execute();
                if (responseUpdatetask.isSuccessful() && responseUpdatetask.body() != null) {
                    return responseUpdatetask.code() + "";
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
