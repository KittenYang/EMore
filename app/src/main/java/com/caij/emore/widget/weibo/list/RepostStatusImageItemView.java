package com.caij.emore.widget.weibo.list;

import android.content.Context;
import android.util.AttributeSet;

import com.caij.emore.R;
import com.caij.emore.database.bean.Status;
import com.caij.emore.widget.weibo.ImageInterface;

import butterknife.BindView;

/**
 * Created by Caij on 2016/6/16.
 */
public class RepostStatusImageItemView extends RepostStatusItemView {

    @BindView(R.id.pics_view)
    ImageInterface picsView;

    public RepostStatusImageItemView(Context context) {
        super(context);
    }

    public RepostStatusImageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RepostStatusImageItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RepostStatusImageItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected int getLayoutId() {
        return R.layout.view_weibo_repost_text_and_image_item;
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
        setImages(status, picsView);
    }

    protected void setImages(Status weibo, ImageInterface picsView) {
        picsView.setPics(weibo.getRetweeted_status().getPic_ids(), weibo.getRetweeted_status().getPic_infos());
    }

}
