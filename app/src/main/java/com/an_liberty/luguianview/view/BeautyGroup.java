package com.an_liberty.luguianview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.an_liberty.luguianview.R;

/**
 * Created by liberty on 2018/2/11.
 */

public class BeautyGroup extends ViewGroup {

    private static final String TAG = BeautyGroup.class.getSimpleName();

    private float btnAngle;

    public BeautyGroup(Context context) {
        this(context, null);
    }

    public BeautyGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BeautyGroup);

        btnAngle = typedArray.getFloat(R.styleable.BeautyGroup_btnAngle, 90);
        typedArray.recycle();


    }

    private void init() {
        if (getChildCount() > 2) {
            throw new IllegalStateException("the count of view child could not be more than 2.");
        }

        float[] angleArr = {btnAngle, 180 - btnAngle};
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (!(v instanceof BeautyView)) {
                throw new IllegalStateException("the child can only be BeautyView");
            }
            BeautyView beautyView = (BeautyView) v;
            beautyView.setBtnChildAngle(angleArr[i]);
            beautyView.btnIndex = i;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        this.measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);

        Log.d(TAG, "measuredWidth : " + width);
        Log.d(TAG, "measuredHeight : " + height);

        setMeasuredDimension(width, height);

    }

    private int measureWidth(int widthMeasure) {
        int size = MeasureSpec.getSize(widthMeasure);
        int mode = MeasureSpec.getMode(widthMeasure);


        int width = 0;

        switch (mode) {
            case MeasureSpec.EXACTLY: {
                width = size;
            }
            break;
            case MeasureSpec.AT_MOST: {
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    width += child.getMeasuredWidth();
                }
                if (width>size){
                    width=size;
                }
                width+=getPaddingLeft()+getPaddingRight();
            }
            break;
        }


        return width;
    }

    private int measureHeight(int heightMeasure) {
        int size = MeasureSpec.getSize(heightMeasure);
        int mode = MeasureSpec.getMode(heightMeasure);

        int height = 0;

        switch (mode) {
            case MeasureSpec.EXACTLY: {
                height = size;
            }
            break;
            case MeasureSpec.AT_MOST: {
                int childHeight1 = getChildAt(0).getMeasuredHeight();
                int childHeight2 = getChildAt(1).getMeasuredHeight();
                height = Math.max(childHeight1, childHeight2);
                if (height>size){
                    height=size;
                }
                height+=getPaddingTop()+getPaddingBottom();
            }
            break;
        }
        return height;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int topPadding = getPaddingTop();
        int bottomPadding = getPaddingBottom();
        int leftPadding = getPaddingLeft();
        int rightPadding = getPaddingRight();

        double distance = 0.0;
        for (int i = 0; i < getChildCount(); i++) {

            BeautyView child = (BeautyView) getChildAt(i);
            double cot = Math.cos(child.getBtnChildAngle() / 180 * Math.PI) / Math.sin(child.getBtnChildAngle() / 180 * Math.PI);
            if (cot == 0) {
                if (i == 0) {
                    layoutChildFrame(child,leftPadding,topPadding,child.getMeasuredWidth()+leftPadding,child.getMeasuredHeight() +topPadding);
//                    child.layout(leftPadding, topPadding, child.getMeasuredWidth(), child.getMeasuredHeight() - bottomPadding);
                } else if (i == 1) {
                    layoutChildFrame(child,getChildAt(i - 1).getRight()+leftPadding,topPadding,getChildAt(i - 1).getRight() + child.getMeasuredWidth()+leftPadding,child.getMeasuredHeight() +topPadding);
//                    child.layout(getChildAt(i - 1).getWidth(), topPadding, getChildAt(i - 1).getWidth() + child.getMeasuredWidth() - rightPadding, child.getMeasuredHeight() - bottomPadding);
                }
            } else {
                if (distance > 0) {
                    distance = Math.min(distance, Math.abs(cot * child.getMeasuredHeight()));
                } else {
                    distance = Math.abs(cot * child.getMeasuredHeight());
                }
//                distance = Math.abs(cot * child.getMeasuredHeight()) ;
                if (i == 0) {
                    layoutChildFrame(child,leftPadding, topPadding,(int) (child.getMeasuredWidth() + distance / 2)+leftPadding, child.getMeasuredHeight() +topPadding);
//                    child.layout(leftPadding, topPadding, (int) (child.getMeasuredWidth() + distance / 2), child.getMeasuredHeight() - bottomPadding);
                } else if (i == 1) {
                    layoutChildFrame(child,(int) (getChildAt(i - 1).getRight() - distance)+leftPadding,topPadding,(int) (getChildAt(i - 1).getRight() + child.getMeasuredWidth() - distance / 2), child.getMeasuredHeight() +topPadding);
//                    child.layout((int) (getChildAt(i - 1).getRight() - distance), topPadding, (int) (getChildAt(i - 1).getRight() + child.getMeasuredWidth() - distance / 2) - rightPadding, child.getMeasuredHeight() - bottomPadding);
                }
            }
//            if (i==0){
//                setChildFrame(getChildAt(i),0,0);
//            }else if (i==1){
//                setChildFrame(getChildAt(i),getChildAt(i-1).getWidth(),0);
//            }
        }

    }

    private void layoutChildFrame(View child,int l,int t,int r,int b){
        child.layout(l,t,r,b);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();

    }


}
