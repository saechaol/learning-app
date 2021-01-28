package com.saechaol.learningapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.SubjectDetails;

import java.util.List;

public class AssignedSubjectAdapter extends RecyclerView.Adapter<AssignedSubjectAdapter.SimpleViewHolder>{

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView txtDesc, txtTitle;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            txtDesc = (TextView) itemView.findViewById(R.id.assigned_subject_item_display_layout_txtDesc);
            txtTitle = (TextView) itemView.findViewById(R.id.assigned_subject_item_display_layout_txtTitle);
        }

        public void bind(final SubjectDetails item) {
            txtDesc.setText(item.getDescription());
            txtTitle.setText(item.getSubjectId() + " " + item.getTitle());

        }
    }

    private Context context;
    private List<SubjectDetails> mDataset;

    public AssignedSubjectAdapter(Context context, List<SubjectDetails> objects ) {
        this.context = context;

        this.mDataset = objects;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assigned_subject_item_display_layout, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        SubjectDetails item = mDataset.get(position);
        viewHolder.bind(item);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
    
}
