package com.lzq.keyboard;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * 自定义设置网格布局管理器禁止滑动(是否生效不能确定。最大程度的保障不让他滑动)
 *
 * @author LZQ
 */
public class DisableScrollLayoutManager extends GridLayoutManager {
    /**
     * 是否允许滑动  false：不允许
     */
    private boolean isScrollEabled = false;
    /**
     * 横向滑动
     */
    private boolean isHorizontal = false;


    public DisableScrollLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DisableScrollLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public DisableScrollLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

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
