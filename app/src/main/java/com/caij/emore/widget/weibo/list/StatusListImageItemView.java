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
public class StatusListImageItemView extends StatusListItemView {

    @BindView(R.id.pics_view)
    ImageInterface picsView;

    public StatusListImageItemView(Context context) {
        super(context);
    }

    public StatusListImageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusListImageItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StatusListImageItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
        setImages(status, picsView);
    }

    protected void setImages(Status weibo, ImageInterface picsView) {
        picsView.setPics(weibo.getPic_ids(), weibo.getPic_infos());
    }

    protected int getLayoutId() {
        return R.layout.view_weibo_text_and_image_item;
    }
}
