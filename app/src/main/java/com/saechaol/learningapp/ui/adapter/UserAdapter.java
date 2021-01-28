package com.saechaol.learningapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.UserWithCheckbox;

import java.util.List;

public class UserAdapter extends BaseAdapter {

    List<UserWithCheckbox> list ;
    Context mContext;

    public UserAdapter(Context context, List<UserWithCheckbox> list) {
        this.list=list;
        this.mContext=context;
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
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.user_item_layout,parent,false);
            dataAdapter = new DataAdapter();
            dataAdapter.txtName = (TextView) rowView.findViewById(R.id.userDisplayTextName);
            dataAdapter.txtEmail = (TextView) rowView.findViewById(R.id.userDisplayTextEmail);
            rowView.setTag(dataAdapter);
            rowView.setTag(R.id.userDisplayTextName, dataAdapter.txtName);
            rowView.setTag(R.id.userDisplayTextEmail, dataAdapter.txtEmail);
        } else  {
            dataAdapter = (DataAdapter) rowView.getTag();
        }

        UserWithCheckbox userDisplayCheckbxProvider;
        userDisplayCheckbxProvider = (UserWithCheckbox) this.getItem(position);
        dataAdapter.txtName.setText(userDisplayCheckbxProvider.getUserName());
        dataAdapter.txtEmail.setText(userDisplayCheckbxProvider.getEmailId());

        return rowView;
    }

    static class DataAdapter
    {
        TextView txtName;
        TextView txtEmail;
    }

}
