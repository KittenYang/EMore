package com.caij.emore.widget.weibo.list;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.ui.activity.WeiboDetialActivity;
import com.caij.emore.utils.ToastUtil;
import com.caij.emore.widget.weibo.ImageInterface;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/6/16.
 */
public class RepostWeiboListImageItemView extends RepostWeiboListItemView {

    @BindView(R.id.pics_view)
    ImageInterface picsView;

    public RepostWeiboListImageItemView(Context context) {
        super(context);
    }

    public RepostWeiboListImageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RepostWeiboListImageItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RepostWeiboListImageItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected int getLayoutId() {
        return R.layout.view_weibo_repost_text_and_image_item;
    }

    @Override
    public void setWeibo(Weibo weibo) {
        super.setWeibo(weibo);
        setImages(weibo, picsView);
    }

    protected void setImages(Weibo weibo, ImageInterface picsView) {
        picsView.setPics(weibo.getRetweeted_status().getPic_ids(), weibo.getRetweeted_status().getPic_infos());
    }

}
