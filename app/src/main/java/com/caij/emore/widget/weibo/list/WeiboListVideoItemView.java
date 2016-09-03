package com.caij.emore.widget.weibo.list;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.bean.PageInfo;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.ui.activity.VideoViewPlayingActivity;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.RatioRelativeLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/9/1.
 */
public class WeiboListVideoItemView extends WeiboListItemView {

    private CompositePatternVideo mCompositePatternVideo;

    public WeiboListVideoItemView(Context context) {
        super(context);
        init();
    }

    public WeiboListVideoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeiboListVideoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public WeiboListVideoItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mCompositePatternVideo = new CompositePatternVideo();
        mCompositePatternVideo.setUp(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_weibo_video_item;
    }

    @Override
    public void setWeibo(Weibo weibo) {
        super.setWeibo(weibo);
        mCompositePatternVideo.setWeibo(weibo, getContext());
    }

}
