package com.saechaol.learningapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.ListModel;

import java.util.HashMap;
import java.util.List;

public class MenuAdapter extends BaseExpandableListAdapter {

    private Context context;
    private HashMap<String, List<String>> navDrawerMap;
    private List<String> navDrawerList;


    public MenuAdapter(Context ctx, HashMap navDrawerHashMap, List navDrawerParentList)
    {
        this.context = ctx;
        this.navDrawerMap = navDrawerHashMap;
        this.navDrawerList = navDrawerParentList;
    }
    @Override
    public int getGroupCount() {
        return navDrawerList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return navDrawerMap.get(navDrawerList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return navDrawerList.get(groupPosition) ;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return navDrawerMap.get(navDrawerList.get(groupPosition)).get(childPosition);

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupTitle = (String) getGroup(groupPosition);
        if(convertView == null)
        {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.drawer_primary,parent,false);
        }
        TextView txtPrimary = (TextView) convertView.findViewById(R.id.textParentTitle);
        txtPrimary.setText(groupTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ListModel secondaryItem = (ListModel) getChild(groupPosition,childPosition);
        String secondaryTitle = secondaryItem.getTextView();

        if(convertView == null)
        {
            LayoutInflater inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.drawer_secondary, parent,false);
        }

        TextView txtSecondary = (TextView) convertView.findViewById(R.id.textSecondaryTitle);
        txtSecondary.setText(secondaryTitle);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
