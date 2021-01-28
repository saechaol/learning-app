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
import com.saechaol.learningapp.model.InstructorDetails;
import com.saechaol.learningapp.ui.view.CustomSwipeLayout;

import java.util.List;

public class InstructorAdapter extends RecyclerSwipeAdapter<InstructorAdapter.SimpleViewHolder>  {

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        CustomSwipeLayout swipeLayout;
        TextView txtUserName, txtName, txtEmailId;
        ImageView imgDelete, imgEdit;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (CustomSwipeLayout) itemView.findViewById(R.id.userItemDisplayLayoutSwipeParent);
            txtName = (TextView) itemView.findViewById(R.id.userItemDisplayLayoutTextName);

            txtUserName = (TextView) itemView.findViewById(R.id.userItemDisplayLayoutTextUserName);

            txtEmailId = (TextView) itemView.findViewById(R.id.userItemDisplayLayoutTextEmail);
            imgDelete = (ImageView) itemView.findViewById(R.id.userItemDisplayLayoutImgDeleteUser);

            imgEdit = (ImageView) itemView.findViewById(R.id.userItemDisplayLayoutImgEditUser);


        }

        public void bind(final InstructorDetails item, final OnItemClickListener listener) {
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

            swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                @Override
                public void onOpen(SwipeLayout layout) {
                }
            });
            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swipeLayout.close();
                    listener.onItemClick(item,R.id.userItemDisplayLayoutImgDeleteUser);
                }
            });


            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swipeLayout.close();

                    listener.onItemClick(item,R.id.userItemDisplayLayoutImgEditUser);
                }
            });
            swipeLayout.setOnClickItemListener(new CustomSwipeLayout.OnClickItemListener() {
                @Override
                public void onClick(View view) {

                    listener.onItemClick(item,R.id.userItemDisplayLayoutSwipeParent);
                }
            });
            txtName.setText(item.getFirstName() + " " + item.getLastName());

            txtUserName.setText(item.getInstructorId());

            txtEmailId.setText(item.getEmail());
        }
    }

    private Context context;
    private List<InstructorDetails> dataset;

    OnItemClickListener<InstructorDetails> instructorListener;

    public InstructorAdapter(Context context, List<InstructorDetails> objects, OnItemClickListener<InstructorDetails> onItemClickListener) {
        this.context = context;
        this.dataset = objects;
        this.instructorListener = onItemClickListener;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout_displayusers, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        InstructorDetails item = dataset.get(position);
        viewHolder.bind(item, instructorListener);
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.userItemDisplayLayoutSwipeParent;
    }

}
