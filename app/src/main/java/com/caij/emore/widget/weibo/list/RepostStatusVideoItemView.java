package com.caij.emore.widget.weibo.list;

import android.content.Context;
import android.util.AttributeSet;

import com.caij.emore.R;
import com.caij.emore.database.bean.Status;

/**
 * Created by Caij on 2016/9/1.
 */
public class RepostStatusVideoItemView extends RepostStatusItemView {

    private CompositePatternVideo mCompositePatternVideo;

    public RepostStatusVideoItemView(Context context) {
        super(context);
        initSelf();
    }

    public RepostStatusVideoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSelf();
    }

    public RepostStatusVideoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSelf();
    }

    public RepostStatusVideoItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSelf();
    }

    private void initSelf(){
        mCompositePatternVideo = new CompositePatternVideo();
        mCompositePatternVideo.setUp(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_weibo_repost_video_item;
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
        mCompositePatternVideo.setWeibo(status, getContext());
    }

}
