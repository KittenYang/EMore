package com.caij.emore.widget.weibo.list;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.bean.PageInfo;
import com.caij.emore.database.bean.Status;
import com.caij.emore.ui.activity.VideoViewPlayingActivity;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.RatioRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/9/3.
 */
public class CompositePatternVideo {

    @BindView(R.id.iv_video)
    ImageView mIvVideo;
    @BindView(R.id.tv_video_view_count)
    TextView mTvVideoViewCount;
    @BindView(R.id.tv_video_during)
    TextView mTvVideoDuring;
    @BindView(R.id.rl_video)
    RatioRelativeLayout mRlVideo;

    ImageLoader.ImageConfig mVideoImageConfig;

    public void setUp(View view) {
        ButterKnife.bind(this, view);
        mVideoImageConfig = new ImageLoader.ImageConfigBuild()
                .setScaleType(ImageLoader.ScaleType.CENTER_CROP)
                .build();
    }

    public void setWeibo(Status weibo, Context context) {
        PageInfo pageInfo = weibo.getPage_info();
        ImageLoader.loadUrl(context, mIvVideo, pageInfo.getPage_pic(),
                R.drawable.weibo_image_placeholder, mVideoImageConfig);
        mTvVideoViewCount.setText(pageInfo.getMedia_info().getOnline_users());
        if (pageInfo.getMedia_info().getDuration() > 0) {
            mTvVideoDuring.setVisibility(View.VISIBLE);
            mTvVideoDuring.setText(DateUtil.formatSeconds(pageInfo.getMedia_info().getDuration()));
        } else {
            mTvVideoDuring.setVisibility(View.GONE);
        }
        mRlVideo.setTag(weibo);
    }


    @OnClick(R.id.rl_video)
    public void onVideoClick() {
        Status weibo = (Status) mRlVideo.getTag();
        PageInfo.MediaInfo mediaInfo = weibo.getPage_info().getMedia_info();
        Context context = mRlVideo.getContext();
        Intent intent = VideoViewPlayingActivity.newIntent(context, mediaInfo.getStream_url());
        context.startActivity(intent);
    }
}
