package com.lzq.keyboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LZQ
 * @content 小键盘view
 */
public class KeyboardView extends LinearLayout implements View.OnClickListener, KeyboardAdapter.OnItemClickListener {
    private static final String TAG = "KeyboardView";
    private Context mContext;
    private boolean isShow = false;
    private List<String> mNumberList = new ArrayList<>();
    private OnItemClickListener mItemClickListener;
    private static int MATCH_PARENT = LayoutParams.MATCH_PARENT, WRAP_CONTENT = LayoutParams.WRAP_CONTENT;

    public KeyboardView(Context context) {
        super(context);
        mContext = context;
        initData();
        initView();
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initData();
        initView();
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initData();
        initView();
    }

    private void initView() {
        KeyboardView.this.setAlpha(0);
        KeyboardView.this.setVisibility(GONE);
        ImageView mHideView = new ImageView(mContext);
        mHideView.setBackgroundResource(R.drawable.selector_keyboard_hide);
        mHideView.setTag("hide");
        mHideView.setImageResource(R.drawable.keyboard_hide);
        mHideView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mHideView.setPadding(0, 12, 0, 12);
        mHideView.setOnClickListener(this);
        addView(mHideView, new LayoutParams(MATCH_PARENT, ConvertUtils.dp2px(32)));

        View mLineView = new View(mContext);
        mLineView.setBackgroundColor(Color.parseColor("#DADADA"));
        addView(mLineView, new LinearLayoutCompat.LayoutParams(MATCH_PARENT, ConvertUtils.dp2px(1)));

        RecyclerView mRecyclerView = new RecyclerView(mContext);
        mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(new DisableScrollLayoutManager(getContext(), 3));
        KeyboardAdapter mKeyboardAdapter = new KeyboardAdapter(mNumberList);
        mKeyboardAdapter.setParentHeight((ScreenUtils.getScreenHeight() / 13) * 5);
        mRecyclerView.setAdapter(mKeyboardAdapter);
        mRecyclerView.addItemDecoration(new GridItemDecoration(mContext, Color.parseColor("#707070"), ConvertUtils.dp2px(1), GridItemDecoration.ALL));
        mKeyboardAdapter.setItemClickListener(this);

        addView(mRecyclerView, new LayoutParams(MATCH_PARENT, (ScreenUtils.getScreenHeight() / 13) * 5));
    }

    private void initData() {
        for (int i = 0; i < 12; i++) {
            if (i < 9) {
                mNumberList.add(String.valueOf(i + 1));
            } else if (i == 9) {
                mNumberList.add(".");
            } else if (i == 10) {
                mNumberList.add("0");
            } else {
                mNumberList.add("删除");
            }
        }
    }

    public void show() {
        //展示动画
        KeyboardView.this.setVisibility(VISIBLE);
        ObjectAnimator showAlpha = ObjectAnimator.ofFloat(this, "alpha", 0, 1);
        ObjectAnimator showTranslationY = ObjectAnimator.ofFloat(this, "translationY", 150, 0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(showAlpha, showTranslationY);
        animatorSet.setDuration(200).start();
    }

    public void dismiss() {
        //隐藏动画
        ObjectAnimator hideAlpha = ObjectAnimator.ofFloat(this, "alpha", 1, 0);
        ObjectAnimator hideTranslationY = ObjectAnimator.ofFloat(this, "translationY", 0, 150);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(hideAlpha, hideTranslationY);
        animatorSet.setDuration(200).start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                KeyboardView.this.setVisibility(GONE);
//                KeyboardView.this.removeAllViews();
                System.gc();
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v instanceof ImageView && "hide".equals(v.getTag())) {
            isShow = false;
            dismiss();
            if (mItemClickListener != null) {
                mItemClickListener.onHideClick(v);
            }
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        if (mItemClickListener != null) {
//            mItemClickListener.onKeyClick(v, ((int) v.getTag() == 9 || (int) v.getTag() == 11) ?
//                    (int) v.getTag() == 9 ? "." : "del" : (int) v.getTag() + 1);
            if (((int) v.getTag() == 9 || (int) v.getTag() == 11)) {
                if ((int) v.getTag() == 9) {
                    mItemClickListener.onPointClick();
                } else {
                    mItemClickListener.onDeleteClick();
                }
            } else {
                mItemClickListener.onKeyClick(v, position + 1);
            }
        }
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }


    //region 横竖分割线
    private class GridItemDecoration extends RecyclerView.ItemDecoration {
        public static final int HORIZONTAL = 0;
        public static final int VERTICAL = 1;
        public static final int ALL = 2;
        private int mSpace;
        private int mColor;
        private Paint mPaint;
        private Drawable mDivider;
        private int mOrientation;
        private final Rect mBounds = new Rect();
        private final int[] ATTRS = new int[]{16843284};

        public GridItemDecoration(Context context, int mColor, int mSpace, int orientation) {
            TypedArray a = context.obtainStyledAttributes(ATTRS);
            this.mDivider = a.getDrawable(0);
            this.mColor = mColor;
            this.mSpace = mSpace;
            mDivider.setTint(mColor);
            if (this.mDivider == null) {
                Log.w("DividerItem", "@android:attr/listDivider was not set in the theme used for this DividerItemDecoration. Please set that attribute all call setDrawable()");
            }
            a.recycle();
            this.setOrientation(orientation);
        }

        public void setOrientation(int orientation) {
            if (orientation != 0 && orientation != 1 && orientation != 2) {
                throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL");
            } else {
                this.mOrientation = orientation;
            }
        }

        public void setDrawable(@NonNull Drawable drawable) {
            if (drawable == null) {
                throw new IllegalArgumentException("Drawable cannot be null.");
            } else {
                this.mDivider = drawable;
            }
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(mColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(mSpace);
            if (parent.getLayoutManager() != null && this.mDivider != null) {
                if (this.mOrientation == 1) {
                    this.drawVertical(c, parent);
                } else if (mOrientation == 0) {
                    this.drawHorizontal(c, parent);
                } else {
                    this.drawVertical(c, parent);
                    this.drawHorizontal(c, parent);
                }

            }
        }

        private void drawVertical(Canvas canvas, RecyclerView parent) {
            canvas.save();
            int left;
            int right;
            if (parent.getClipToPadding()) {
                left = parent.getPaddingLeft();
                right = parent.getWidth() - parent.getPaddingRight();
                canvas.clipRect(left, parent.getPaddingTop(), right, parent.getHeight() - parent.getPaddingBottom());
            } else {
                left = 0;
                right = parent.getWidth();
            }

            int childCount = parent.getChildCount();

            for (int i = 0; i < childCount; ++i) {
                View child = parent.getChildAt(i);
                parent.getDecoratedBoundsWithMargins(child, this.mBounds);
                int bottom = this.mBounds.bottom + Math.round(child.getTranslationY());
                int top = bottom - this.mDivider.getIntrinsicHeight();
                this.mDivider.setBounds(left, top, right, bottom);
                this.mDivider.draw(canvas);
            }

            canvas.restore();
        }

        private void drawHorizontal(Canvas canvas, RecyclerView parent) {
            canvas.save();
            int top;
            int bottom;
            if (parent.getClipToPadding()) {
                top = parent.getPaddingTop();
                bottom = parent.getHeight() - parent.getPaddingBottom();
                canvas.clipRect(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(), bottom);
            } else {
                top = 0;
                bottom = parent.getHeight();
            }

            int childCount = parent.getChildCount();

            for (int i = 0; i < childCount; ++i) {
                View child = parent.getChildAt(i);
                parent.getLayoutManager().getDecoratedBoundsWithMargins(child, this.mBounds);
                int right = this.mBounds.right + Math.round(child.getTranslationX());
                int left = right - this.mDivider.getIntrinsicWidth();
                this.mDivider.setBounds(left, top, right, bottom);
                this.mDivider.draw(canvas);
            }

            canvas.restore();
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (this.mDivider == null) {
                outRect.set(0, 0, 0, 0);
            } else {
                if (this.mOrientation == 1) {
                    outRect.set(0, 0, 0, this.mDivider.getIntrinsicHeight());
                } else if (this.mOrientation == 0) {
                    outRect.set(0, 0, this.mDivider.getIntrinsicWidth(), 0);
                } else {
                    outRect.set(0, 0, this.mDivider.getIntrinsicWidth(), this.mDivider.getIntrinsicHeight());
                }

            }
        }
    }
    //endregion


    public interface OnItemClickListener {
        /**
         * 点击监听
         *
         * @param v   点击view
         * @param key 传入按键值
         */
        void onKeyClick(View v, int key);
        /**
         * 点击删除
         */
        void onDeleteClick();
        /**
         * 点击小数点
         */
        void onPointClick();

        /**
         * 隐藏
         *
         * @param v
         */
        void onHideClick(View v);
    }

    public void setmItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


}
