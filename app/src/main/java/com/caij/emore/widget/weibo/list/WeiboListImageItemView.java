package com.caij.emore.widget.weibo.list;

import android.content.Context;
import android.util.AttributeSet;

import com.caij.emore.R;
import com.caij.emore.database.bean.Status;
import com.caij.emore.widget.weibo.ImageInterface;

import butterknife.BindView;

/**
 * Created by Caij on 2016/9/1.
 */
public class WeiboListImageItemView extends WeiboListItemView {

    @BindView(R.id.pics_view)
    ImageInterface picsView;

    public WeiboListImageItemView(Context context) {
        super(context);
    }

    public WeiboListImageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeiboListImageItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WeiboListImageItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setWeibo(Status weibo) {
        super.setWeibo(weibo);
        setImages(weibo, picsView);
    }

    protected void setImages(Status weibo, ImageInterface picsView) {
        picsView.setPics(weibo.getPic_ids(), weibo.getPic_infos());
    }

    protected int getLayoutId() {
        return R.layout.view_weibo_text_and_image_item;
    }
}
