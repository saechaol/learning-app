package com.saechaol.learningapp.util;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Creates an Item object decoration for the vertical space
 */
public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int verticalHeight;

    public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
        this.verticalHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect border, View view, RecyclerView parent, RecyclerView.State state) {
        border.bottom = verticalHeight;
    }

}
