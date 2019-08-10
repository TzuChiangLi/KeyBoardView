package com.lzq.keyboard;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

public class DisableScrollLayoutManager extends GridLayoutManager {
    //是否允许滑动  false：不允许
    private boolean isScrollEabled = false;
    //横向滑动
    private boolean isHorizontal=false;

    public DisableScrollLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DisableScrollLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public DisableScrollLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

//    @Override
//    public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
//        View view = recycler.getViewForPosition(0);
//        if (view != null) {
//            measureChild(view, widthSpec, heightSpec);
//            int measuredWidth = View.MeasureSpec.getSize(widthSpec);
//            int measuredHeight = view.getMeasuredHeight() * getSpanCount();
//            setMeasuredDimension(measuredWidth, measuredHeight);
//        }
//    }

    public boolean isScrollEabled() {
        return isScrollEabled;
    }

    public void setScrollEabled(boolean scrollEabled) {
        isScrollEabled = scrollEabled;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEabled && super.canScrollVertically();
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
    }

}
