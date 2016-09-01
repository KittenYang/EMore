package com.caij.emore.widget.weibo.list;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.caij.emore.R;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.utils.ImageLoader;

import butterknife.BindView;

/**
 * Created by Caij on 2016/9/1.
 */
public class RepostWeiboListVideoItemView extends RepostWeiboListItemView {

    @BindView(R.id.iv_video)
    ImageView mIvVideo;

    ImageLoader.ImageConfig mVideoImageConfig;

    public RepostWeiboListVideoItemView(Context context) {
        super(context);
        init();
    }

    public RepostWeiboListVideoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RepostWeiboListVideoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RepostWeiboListVideoItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        mVideoImageConfig = new ImageLoader.ImageConfigBuild()
                .setScaleType(ImageLoader.ScaleType.CENTER_CROP)
                .build();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_weibo_repost_video_item;
    }

    @Override
    public void setWeibo(Weibo weibo) {
        super.setWeibo(weibo);
        ImageLoader.loadUrl(getContext(), mIvVideo, weibo.getPage_info().getPage_pic(),
                R.drawable.weibo_image_placeholder, mVideoImageConfig);
    }
}
