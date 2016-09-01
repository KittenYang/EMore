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
public class WeiboListVideoItemView extends WeiboListItemView {

    @BindView(R.id.iv_video)
    ImageView mIvVideo;

    public WeiboListVideoItemView(Context context) {
        super(context);
    }

    public WeiboListVideoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeiboListVideoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WeiboListVideoItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_weibo_video_item;
    }

    @Override
    public void setWeibo(Weibo weibo) {
        super.setWeibo(weibo);
        ImageLoader.loadUrl(getContext(), mIvVideo, );
    }
}
