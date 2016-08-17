package com.caij.emore.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.caij.emore.utils.DensityUtil;

import java.lang.reflect.Field;

/**
 * Created by Caij on 2016/6/15.
 */
public class WeiboDetailTabLayout extends TabLayout{

    private int mTabsCount;

    public WeiboDetailTabLayout(Context context) {
        super(context);
    }

    public WeiboDetailTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeiboDetailTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addTab(@NonNull Tab tab, boolean setSelected) {
        super.addTab(tab, setSelected);
        mTabsCount ++;
        if (mTabsCount == 3) {
            try {
                Field scrollerField = Tab.class.getDeclaredField("mView");
                Field field = TabLayout.class.getDeclaredField("mTabStrip");
                field.setAccessible(true);
                final ViewGroup viewGroup = (ViewGroup) field.get(this);
                scrollerField.setAccessible(true);
                final View view = (View) scrollerField.get(tab);
                final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
                final ViewTreeObserver vto = view.getViewTreeObserver();
                vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        int parentWidth =  DensityUtil.getScreenWidth(getContext());
                        if (getMeasuredWidth() > 0) {
                            parentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
                        }
                        int leftMargin = parentWidth - viewGroup.getChildAt(0).getMeasuredWidth()
                                - viewGroup.getChildAt(1).getMeasuredWidth() - viewGroup.getChildAt(2).getMeasuredWidth();
//                        LogUtil.d(WeiboDetailTabLayout.this, "leftMargin %s view1 %s view2 %s parentWidth %s",
//                                leftMargin, viewGroup.getChildAt(0).getMeasuredWidth(),
//                                viewGroup.getChildAt(1).getMeasuredWidth(),
//                                parentWidth);
                        if (parentWidth > 0 && leftMargin > 0) {
                            if (params.leftMargin != leftMargin) {
                                params.leftMargin = leftMargin;
                                view.setLayoutParams(params);
                            }
                        }

                        if (!vto.isAlive()) {
                            return true;
                        }

                        vto.removeOnPreDrawListener(this);
                        return false;
                    }

                });
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeAllTabs(){
        super.removeAllTabs();
        mTabsCount = 0;
    }
}
