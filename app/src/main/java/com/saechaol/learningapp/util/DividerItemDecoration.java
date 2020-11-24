package com.saechaol.learningapp.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable drawableDivider;
    private boolean showFirstDivider = false;
    private boolean showLastDivider = false;

    public DividerItemDecoration(Context context, AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, new int[] {
                android.R.attr.listDivider
        });
        drawableDivider = typedArray.getDrawable(0);
        typedArray.recycle();
    }

    public DividerItemDecoration(Context context, AttributeSet attrs, boolean firstDivider, boolean lastDivider) {
        this(context, attrs);
        showFirstDivider = firstDivider;
        showLastDivider = lastDivider;
    }

    public DividerItemDecoration(Drawable divider) {
        drawableDivider = divider;
    }

    public DividerItemDecoration(Drawable drawableDivider, boolean firstDivider, boolean lastDivider) {
        this(drawableDivider);
        showFirstDivider = firstDivider;
        showLastDivider = lastDivider;
    }

    @Override
    public void getItemOffsets(Rect border, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(border, view, parent, state);
        if (drawableDivider == null) {
            return;
        }
        if (parent.getChildAdapterPosition(view) < 1) {
            return;
        }
        if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
            border.top = drawableDivider.getIntrinsicHeight();
        } else {
            border.left = drawableDivider.getIntrinsicWidth();
        }
    }

    private int getOrientation(RecyclerView parent) {
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            return layoutManager.getOrientation();
        } else {
            throw new IllegalStateException("DividerItemDecoration can only be used with a LinearLayoutManager");
        }
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (drawableDivider == null) {
            super.onDrawOver(canvas, parent, state);
            return;
        }

        int left = 0, right = 0, top = 0, bottom = 0, size;
        int orientation = getOrientation(parent);
        int childCount = parent.getChildCount();

        if (orientation == LinearLayoutManager.VERTICAL) {
            size = drawableDivider.getIntrinsicHeight();
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
        } else {
            size = drawableDivider.getIntrinsicWidth();
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
        }

        for (int i = showFirstDivider ? 0 : 1; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();

            if (orientation == LinearLayoutManager.VERTICAL) {
                top = childView.getTop() - params.topMargin;
                bottom = top + size;
            } else {
                left = childView.getLeft() - params.leftMargin;
                right = left + size;
            }
            drawableDivider.setBounds(left, top, right, bottom);
            drawableDivider.draw(canvas);
        }

        if (showLastDivider && childCount > 0) {
            View childView = parent.getChildAt(childCount - 1);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();
            if (orientation == LinearLayoutManager.VERTICAL) {
                top = childView.getBottom() + params.bottomMargin;
                bottom = top + size;
            } else {
                left = childView.getRight() + params.rightMargin;
                right = left + size;
            }

            drawableDivider.setBounds(left, top, right, bottom);
            drawableDivider.draw(canvas);
        }


    }

}
