package com.caij.emore.widget.recyclerview.scroller;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.RecyclerView;

/**
 * It's great, copy from:
 * https://github.com/apptik/MultiView/tree/master/scrollers/src/main/java/io/apptik/multiview/scrollers
 */
public class FlexibleSmoothScroller extends BaseSmoothScroller {

    private Runnable beforeScrollAction;
    private Runnable afterScrollAction;


    public FlexibleSmoothScroller(Context context) {
        super(context);
    }


    public Runnable getAfterScrollAction() {
        return afterScrollAction;
    }


    public void setAfterScrollAction(Runnable afterScrollAction) {
        this.afterScrollAction = afterScrollAction;
    }


    public Runnable getBeforeScrollAction() {
        return beforeScrollAction;
    }


    public void setBeforeScrollAction(Runnable beforeScrollAction) {
        this.beforeScrollAction = beforeScrollAction;
    }


    @Override
    protected void onStop() {
        if (afterScrollAction != null) {
            afterScrollAction.run();
        }
        super.onStop();
    }


    @Override
    protected void onStart() {
        if (beforeScrollAction != null) {
            beforeScrollAction.run();
        }
        super.onStart();
    }


    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        boolean mShouldReverseLayout = false;
        RecyclerView.LayoutManager lm = getLayoutManager();
        if (getChildCount() == 0) {
            return null;
        }
        final int firstChildPos = lm.getPosition(lm.getChildAt(0));
        final int direction = targetPosition < firstChildPos != mShouldReverseLayout ? -1 : 1;
        if (lm.canScrollHorizontally()) {
            return new PointF(direction, 0);
        } else {
            return new PointF(0, direction);
        }
    }
}
