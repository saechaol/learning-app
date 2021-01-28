package com.saechaol.learningapp.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
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
import com.saechaol.learningapp.model.GradeTask;
import com.saechaol.learningapp.ui.view.CustomSwipeLayout;

import java.util.List;

public class GradeAdapter extends RecyclerSwipeAdapter<GradeAdapter.SimpleViewHolder> {

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        CustomSwipeLayout swipeLayout;
        TextView txtName, txtGrade;
        ImageView imgEdit;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (CustomSwipeLayout) itemView.findViewById(R.id.row_display_grade_swipeParent);
            txtName = (TextView) itemView.findViewById(R.id.row_display_grade_txtName);
            txtGrade = (TextView) itemView.findViewById(R.id.row_display_grade_txtGrade);
            imgEdit = (ImageView) itemView.findViewById(R.id.row_display_grade_imgEditUser);

        }

        public void bind(final GradeTask item, final OnItemClickListener listener) {
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

            swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                @Override
                public void onOpen(SwipeLayout layout) {
                }
            });

            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swipeLayout.close();

                    listener.onItemClick(item, R.id.row_display_grade_imgEditUser);
                }
            });
            swipeLayout.setOnClickItemListener(new CustomSwipeLayout.OnClickItemListener() {
                @Override
                public void onClick(View view) {

                    listener.onItemClick(item, R.id.row_display_grade_swipeParent);
                }
            });
            txtName.setText(item.getStudentId());

            if (TextUtils.isEmpty(item.getInstructorGrade())) {
                txtGrade.setText("Not Graded");
            } else {
                txtGrade.setText(item.getInstructorGrade());
            }
        }
    }

    private Context context;
    private List<GradeTask> dataset;

    OnItemClickListener<GradeTask> gradeListener;

    public GradeAdapter(Context context, List<GradeTask> objects, OnItemClickListener<GradeTask> onItemClickListener) {
        this.context = context;

        this.dataset = objects;
        this.gradeListener = onItemClickListener;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout_gradedisplay, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        GradeTask item = dataset.get(position);
        viewHolder.bind(item, gradeListener);
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.row_display_grade_swipeParent;
    }
}
