package com.saechaol.learningapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.UserWithCheckbox;

import java.util.List;

public class UserCheckboxAdapter extends BaseAdapter {

    List<UserWithCheckbox> list;
    Context context;

    public UserCheckboxAdapter(Context context, List<UserWithCheckbox> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = convertView;
        DataAdapter dataAdapter;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.rowlayout_checkboxusers, parent, false);
            dataAdapter = new DataAdapter();
            dataAdapter.txtName = (TextView) rowView.findViewById(R.id.checkBoxUsersTextName);
            dataAdapter.txtEmail = (TextView) rowView.findViewById(R.id.checkBoxUsersTextEmail);
            dataAdapter.checkBox = (CheckBox) rowView.findViewById(R.id.checkBoxUsersCheckBox);
            rowView.setTag(dataAdapter);
            rowView.setTag(R.id.checkBoxUsersTextName, dataAdapter.txtName);
            rowView.setTag(R.id.checkBoxUsersTextEmail, dataAdapter.txtEmail);
            rowView.setTag(R.id.checkBoxUsersCheckBox, dataAdapter.checkBox);

            dataAdapter.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();
                    UserWithCheckbox userDisplayCheckbxProvider;
                    userDisplayCheckbxProvider =  list.get(getPosition);
                    userDisplayCheckbxProvider.setCheck(buttonView.isChecked());
                }
            });

        } else {
            dataAdapter = (DataAdapter) rowView.getTag();
        }
        dataAdapter.checkBox.setTag(position);

        UserWithCheckbox userDisplayCheckboxProvider;
        userDisplayCheckboxProvider = (UserWithCheckbox) this.getItem(position);
        dataAdapter.txtName.setText(userDisplayCheckboxProvider.getUserName());
        dataAdapter.txtEmail.setText(userDisplayCheckboxProvider.getEmailId());

        dataAdapter.checkBox.setChecked(userDisplayCheckboxProvider.getCheck());

        return rowView;
    }

    static class DataAdapter {
        TextView txtName;
        TextView txtEmail;
        CheckBox checkBox;
    }

}
