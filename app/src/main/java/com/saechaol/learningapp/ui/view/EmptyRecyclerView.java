package com.saechaol.learningapp.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Provides support for an empty view for handling large lists
 */
public class EmptyRecyclerView extends RecyclerView {

    private View emptyView;

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs, int definedStyle) {
        super(context, attrs, definedStyle);
    }

    final private AdapterDataObserver observer = new AdapterDataObserver() {

        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int start, int count) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int start, int count) {
            checkIfEmpty();
        }
    };

    protected void checkIfEmpty() {
        if (emptyView != null && getAdapter() != null) {
            final boolean isEmptyViewVisible = getAdapter().getItemCount() == 0;
            emptyView.setVisibility(isEmptyViewVisible ? VISIBLE : GONE);
            setVisibility(isEmptyViewVisible ? GONE : VISIBLE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        checkIfEmpty();
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        checkIfEmpty();
    }

}
