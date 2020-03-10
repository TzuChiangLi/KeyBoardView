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
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 在布局文件引用本控件后，不设定style会使用默认数字键盘布局，设定后会根据style值显示相应的布局
 *
 * @author LZQ
 */
public class KeyboardView extends LinearLayout implements View.OnClickListener, KeyboardAdapter.OnItemClickListener {
    private static final String TAG = "KeyboardView";
    protected Context mContext;
    private boolean isShow = false;
    private List<String> mNumberList = new ArrayList<>();
    private EditText mEdt;
    private OnItemClickListener mItemClickListener;
    private static int MATCH_PARENT = LayoutParams.MATCH_PARENT, WRAP_CONTENT = LayoutParams.WRAP_CONTENT;
    /**
     * 样式，0为普通仅数字小键盘，1为折扣键盘
     */
    private int style = 0;


    public KeyboardView(Context context) {
        super(context);
        mContext = context;
        initData();
        initView();
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.KeyboardView);
        //获取属性
        style = typedArray.getInt(R.styleable.KeyboardView_style, 0);
        //回收变量
        typedArray.recycle();
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
        //初始化样式
        KeyboardView.this.setOrientation(VERTICAL);
        KeyboardView.this.setAlpha(0);
        KeyboardView.this.setVisibility(GONE);
        //添加顶部隐藏按钮
        ImageView mHideView = new ImageView(mContext);
        mHideView.setBackgroundResource(R.drawable.keyboard_selector_hide);
        mHideView.setTag("hide");
        mHideView.setImageResource(R.drawable.keyboard_hide);
        mHideView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mHideView.setPadding(0, 12, 0, 12);
        mHideView.setOnClickListener(this);
        addView(mHideView, new LayoutParams(MATCH_PARENT, ConvertUtils.dp2px(32)));
        //添加折扣优惠区域
        addDiscountView(style);


        //添加按钮与键盘的分割线
        View mLineView = new View(mContext);
        mLineView.setBackgroundColor(Color.parseColor("#DADADA"));
        addView(mLineView, new LinearLayoutCompat.LayoutParams(MATCH_PARENT, ConvertUtils.dp2px(1)));


        //添加键盘布局
        RecyclerView mRecyclerView = new RecyclerView(mContext);
        mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(new DisableScrollLayoutManager(getContext(), 4));
//        mRecyclerView.setLayoutManager(new DisableScrollLayoutManager(getContext(), 3));
        KeyboardAdapter mKeyboardAdapter = new KeyboardAdapter(mNumberList);
        //根据屏幕大小动态设置按键高度
        mKeyboardAdapter.setParentHeight((ScreenUtils.getScreenHeight() / 13) * 4);
        mRecyclerView.setAdapter(mKeyboardAdapter);
        mRecyclerView.addItemDecoration(new GridItemDecoration(mContext, Color.parseColor("#707070"), ConvertUtils.dp2px(1), GridItemDecoration.ALL));
        mKeyboardAdapter.setItemClickListener(this);
        addView(mRecyclerView, new LayoutParams(MATCH_PARENT, (ScreenUtils.getScreenHeight() / 13) * 4));
    }

    public void addDiscountView(int style) {
        if (style == 1) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.keyboard_discount_view, KeyboardView.this, false);
            mEdt = view.findViewById(R.id.keyboard_edt);
            mEdt.setInputType(InputType.TYPE_NULL);
            addView(view);
            //在这个地方插入想要的布局
//            RelativeLayout mRelativeLayout = new RelativeLayout(mContext);
//            mRelativeLayout.setBackgroundColor(Color.WHITE);
//            RadioGroup mRadioGroup = new RadioGroup(mContext);
//            RadioButton mDiscountRBtn = new RadioButton(mContext);
//            RadioButton mMoneyRBtn = new RadioButton(mContext);
//            mDiscountRBtn.setText("折扣优惠");
//            mMoneyRBtn.setText("现金优惠");
//            mRadioGroup.addView(mDiscountRBtn);
//            mRadioGroup.addView(mMoneyRBtn);
//            mRelativeLayout.addView(mRadioGroup);
//            addView(mRelativeLayout);
        }
    }

    private void initData() {
        for (int i = 0; i < 16; i++) {
            switch (i) {
                case 0:
                case 1:
                case 2:
                    mNumberList.add(String.valueOf(i + 1));
                    break;
                case 3:
                    mNumberList.add("下一个");
                    break;
                case 4:
                case 5:
                case 6:
                    mNumberList.add(String.valueOf(i));
                    break;
                case 7:
                    mNumberList.add("删除");
                    break;
                case 8:
                case 9:
                case 10:
                    mNumberList.add(String.valueOf(i - 1));
                    break;
                case 11:
                    mNumberList.add("清空");
                    break;
                case 12:
                    mNumberList.add(".");
                    break;
                case 13:
                    mNumberList.add("0");
                    break;
                case 14:
                    mNumberList.add("取消");
                    break;
                case 15:
                    mNumberList.add("确认");
                    break;
                default:
                    break;
            }
        }
//        for (int i = 0; i < 16; i++) {
//            if (i < 9) {
//                mNumberList.add(String.valueOf(i + 1));
//            } else if (i == 9) {
//                mNumberList.add(".");
//            } else if (i == 10) {
//                mNumberList.add("0");
//            } else {
//                mNumberList.add("删除");
//            }
//        }
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
        String hide_tag = "hide";
        if (v instanceof ImageView && hide_tag.equals(v.getTag())) {
            isShow = false;
            dismiss();
            if (mItemClickListener != null) {
                mItemClickListener.onHideClick();
            }
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        final int key_next = 3, key_delete = 7, key_clear = 11, key_point = 12, key_zero = 13;
        final int key_cancel = 14, key_enter = 15;
        //待后续样式确定后，逻辑再做优化
        if (mEdt != null) {
            if ((int) v.getTag() == key_point) {
                mEdt.getText().append('.');
            } else if ((int) v.getTag() == key_delete) {
                mEdt.setText(TextUtils.isEmpty(mEdt.getText().toString()) ? "" :
                        mEdt.getText().toString().trim().substring(0, mEdt.getText().toString().trim().length() - 1));
            } else {
                mEdt.setText(mEdt.getText().append(String.valueOf(position + 1)));
            }
        }
        if (mItemClickListener != null) {
            switch ((int) v.getTag()) {
                case 0:
                case 1:
                case 2:
                    mItemClickListener.onKeyClick(v, ((int) v.getTag()) + 1);
                    break;
                case key_next:
                    mItemClickListener.onNextClick();
                    break;
                case 4:
                case 5:
                case 6:
                    mItemClickListener.onKeyClick(v, (int) v.getTag());
                    break;
                case key_delete:
                    mItemClickListener.onDeleteClick();
                    break;
                case 8:
                case 9:
                case 10:
                    mItemClickListener.onKeyClick(v, ((int) v.getTag()) - 1);
                    break;
                case key_clear:
                    mItemClickListener.onClearClick();
                    break;
                case key_point:
                    mItemClickListener.onPointClick();
                    break;
                case key_zero:
                    mItemClickListener.onKeyClick(v, 0);
                    break;
                case key_cancel:
                    mItemClickListener.onCancelClick();
                    break;
                case key_enter:
                    mItemClickListener.onEnterClick();
                    break;
                default:
                    break;
            }


        }
//        if (mEdt != null) {
//            if ((int) v.getTag() == key_point) {
//                mEdt.getText().append('.');
//            } else if ((int) v.getTag() == key_delete) {
//                mEdt.setText(TextUtils.isEmpty(mEdt.getText().toString()) ? "" :
//                        mEdt.getText().toString().trim().substring(0, mEdt.getText().toString().trim().length() - 1));
//            } else {
//                mEdt.setText(mEdt.getText().append(String.valueOf(position + 1)));
//            }
//        }
//        if (mItemClickListener != null) {
//            if ((int) v.getTag() == key_point) {
//                if (mEdt != null) {
//                    mEdt.getText().append('.');
//                }
//                mItemClickListener.onPointClick();
//            } else if ((int) v.getTag() == key_delete) {
//                if (mEdt != null) {
//                    mEdt.setText(TextUtils.isEmpty(mEdt.getText().toString()) ? "" :
//                            mEdt.getText().toString().trim().substring(0, mEdt.getText().toString().trim().length() - 1));
//                }
//                mItemClickListener.onDeleteClick();
//            } else {
//                if (mEdt != null) {
//                    mEdt.setText(mEdt.getText().append(String.valueOf(position + 1)));
//                }
//                mItemClickListener.onKeyClick(v, position + 1);
//            }
//        }
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }


    //region 横竖分割线(不必细看)
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
         */
        void onHideClick();

        /**
         * 下一个
         */
        void onNextClick();

        void onClearClick();

        void onCancelClick();

        void onEnterClick();

    }

    public void setOnKeyboardClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    //region 设置样式
    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }
    //endregion
}
