package com.saechaol.learningapp.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.MotionEventCompat;

import com.daimajia.swipe.SwipeLayout;

public class CustomSwipeLayout extends SwipeLayout {

    float posX, posY;
    OnClickItemListener onClickItemListener;

    public CustomSwipeLayout(Context context) {
        super(context);
    }

    public CustomSwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSwipeLayout(Context context, AttributeSet attrs, int definedStyle) {
        super(context, attrs, definedStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int eventMask = MotionEventCompat.getActionMasked(e);
        switch (eventMask) {
            case MotionEvent.ACTION_DOWN:
                posX = e.getRawX();
                posY = e.getRawY();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (posY == e.getRawY() && posX == e.getRawX()) {
                    if (onClickItemListener != null)
                        onClickItemListener.onClick(CustomSwipeLayout.this);

                }
                break;
        }
        return super.onTouchEvent(e);
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    public interface OnClickItemListener {
        void onClick(View view);
    }
}
