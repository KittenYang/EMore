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
public class RepostWeiboListVideoItemView extends RepostWeiboListItemView {

    private CompositePatternVideo mCompositePatternVideo;

    public RepostWeiboListVideoItemView(Context context) {
        super(context);
        initSelf();
    }

    public RepostWeiboListVideoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSelf();
    }

    public RepostWeiboListVideoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSelf();
    }

    public RepostWeiboListVideoItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
    public void setWeibo(Weibo weibo) {
        super.setWeibo(weibo);
        mCompositePatternVideo.setWeibo(weibo, getContext());
    }

}
