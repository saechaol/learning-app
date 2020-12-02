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
import com.saechaol.learningapp.model.StudentDetails;
import com.saechaol.learningapp.ui.view.CustomSwipeLayout;

import java.util.List;

public class StudentAdapter extends RecyclerSwipeAdapter<StudentAdapter.SimpleViewHolder> {
    
    boolean isSwipable = false;

    public  class SimpleViewHolder extends RecyclerView.ViewHolder {
        CustomSwipeLayout swipeLayout;
        TextView textUserName, textName, textEmailId;
        ImageView imgDelete, imgEdit;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (CustomSwipeLayout) itemView.findViewById(R.id.userItemDisplayLayoutSwipeParent);
            textName = (TextView) itemView.findViewById(R.id.userItemDisplayLayoutTextName);

            textUserName = (TextView) itemView.findViewById(R.id.userItemDisplayLayoutTextUserName);

            textEmailId = (TextView) itemView.findViewById(R.id.userItemDisplayLayoutTextEmail);
            imgDelete = (ImageView) itemView.findViewById(R.id.userItemDisplayLayoutImgDeleteUser);

            imgEdit = (ImageView) itemView.findViewById(R.id.userItemDisplayLayoutImgEditUser);


        }

        public void bind(final StudentDetails item, final OnItemClickListener listener) {
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
                    listener.onItemClick(item, R.id.userItemDisplayLayoutImgDeleteUser);
                }
            });


            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swipeLayout.close();

                    listener.onItemClick(item, R.id.userItemDisplayLayoutImgEditUser);
                }
            });
            swipeLayout.setOnClickItemListener(new CustomSwipeLayout.OnClickItemListener() {
                @Override
                public void onClick(View view) {

                    listener.onItemClick(item, R.id.userItemDisplayLayoutSwipeParent);
                }
            });
            textName.setText(item.getFirstName() + " " + item.getLastName());

            textUserName.setText(item.getStudentId());

            textEmailId.setText(item.getEmail());
        }
    }

    private Context mContext;
    private List<StudentDetails> mDataset;

    OnItemClickListener<StudentDetails> listener;

    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public StudentAdapter(Context context, List<StudentDetails> objects, boolean isSwipable, OnItemClickListener<StudentDetails> onItemClickListenerener) {
        this.mContext = context;
        this.isSwipable = isSwipable;
        this.mDataset = objects;
        this.listener = onItemClickListenerener;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout_displayusers, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        StudentDetails item = mDataset.get(position);
        viewHolder.bind(item, listener);
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.userItemDisplayLayoutSwipeParent;
    }

}
