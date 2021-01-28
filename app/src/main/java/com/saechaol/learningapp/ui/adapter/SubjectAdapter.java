package com.saechaol.learningapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.SubjectDetails;
import com.saechaol.learningapp.ui.view.CustomSwipeLayout;

import java.util.List;

public class SubjectAdapter extends RecyclerSwipeAdapter<SubjectAdapter.SimpleViewHolder> {

    boolean isSwipable = false;

    public class SimpleViewHolder extends RecyclerView.ViewHolder {
        CustomSwipeLayout swipeLayout;
        TextView txtDesc, txtTitle;
        ImageView imgDelete, imgEdit;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (CustomSwipeLayout) itemView.findViewById(R.id.subject_item_display_layout_swipeParent);
            txtDesc = (TextView) itemView.findViewById(R.id.subject_item_display_layout_txtDesc);
            txtTitle = (TextView) itemView.findViewById(R.id.subject_item_display_layout_txtTitle);
            imgDelete = (ImageView) itemView.findViewById(R.id.subject_item_display_layout_imgDeleteUser);
            imgEdit = (ImageView) itemView.findViewById(R.id.subject_item_display_layout_imgEditUser);
        }

        public void bind(final SubjectDetails item, final OnItemClickListener listener) {
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            swipeLayout.setRightSwipeEnabled(isSwipable);
            swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                @Override
                public void onOpen(SwipeLayout layout) {
                }
            });
            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swipeLayout.close();
                    listener.onItemClick(item, R.id.subject_item_display_layout_imgDeleteUser);
                }
            });


            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swipeLayout.close();

                    listener.onItemClick(item, R.id.subject_item_display_layout_imgEditUser);
                }
            });
            swipeLayout.setOnClickItemListener(new CustomSwipeLayout.OnClickItemListener() {
                @Override
                public void onClick(View view) {

                    listener.onItemClick(item, R.id.subject_item_display_layout_swipeParent);
                }
            });
            txtDesc.setText(item.getDescription());

            txtTitle.setText(item.getSubjectId() + " " + item.getTitle());

        }
    }

    private Context context;
    private List<SubjectDetails> dataset;
    OnItemClickListener<SubjectDetails> subjectListener;

    public SubjectAdapter(Context context, List<SubjectDetails> objects, boolean isSwipable, OnItemClickListener<SubjectDetails> onItemClickListener) {
        this.context = context;
        this.dataset = objects;
        this.subjectListener = onItemClickListener;
        this.isSwipable = isSwipable;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout_subject, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {

        SubjectDetails item = dataset.get(position);
        viewHolder.bind(item, subjectListener);
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.subject_item_display_layout_swipeParent;
    }

}
