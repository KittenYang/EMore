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

    @BindView(R.id.iv_video)
    ImageView mIvVideo;
    @BindView(R.id.tv_video_view_count)
    TextView mTvVideoViewCount;
    @BindView(R.id.tv_video_during)
    TextView mTvVideoDuring;
    @BindView(R.id.rl_video)
    RatioRelativeLayout mRlVideo;

    ImageLoader.ImageConfig mVideoImageConfig;


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
        mVideoImageConfig = new ImageLoader.ImageConfigBuild()
                .setScaleType(ImageLoader.ScaleType.CENTER_CROP)
                .build();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_weibo_video_item;
    }

    @Override
    public void setWeibo(Weibo weibo) {
        super.setWeibo(weibo);
        PageInfo pageInfo = weibo.getPage_info();
        ImageLoader.loadUrl(getContext(), mIvVideo, pageInfo.getPage_pic(),
                R.drawable.weibo_image_placeholder, mVideoImageConfig);
        mTvVideoViewCount.setText(pageInfo.getMedia_info().getOnline_users());
        if (pageInfo.getMedia_info().getDuration() > 0) {
            mTvVideoDuring.setVisibility(VISIBLE);
            mTvVideoDuring.setText(DateUtil.formatSeconds(pageInfo.getMedia_info().getDuration()));
        } else {
            mTvVideoDuring.setVisibility(GONE);
        }
        mRlVideo.setTag(weibo);
    }

    @OnClick(R.id.rl_video)
    public void onVideoClick() {
        Weibo weibo = (Weibo) mRlVideo.getTag();
        PageInfo.MediaInfo mediaInfo = weibo.getPage_info().getMedia_info();
        Intent intent = VideoViewPlayingActivity.newIntent(getContext(), mediaInfo.getStream_url());
        getContext().startActivity(intent);
    }
}
