package com.caij.emore.widget.weibo.list;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.database.bean.Status;
import com.caij.emore.ui.activity.StatusDetailActivity;
import com.caij.emore.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/6/16.
 */
public abstract class RepostWeiboListItemView extends WeiboListItemView {

    @BindView(R.id.ll_re)
    LinearLayout llRe;
    @BindView(R.id.tv_re_content)
    TextView tvReContent;

    public RepostWeiboListItemView(Context context) {
        super(context);
    }

    public RepostWeiboListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RepostWeiboListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RepostWeiboListItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setWeibo(Status weibo) {
        super.setWeibo(weibo);
        // reContent
        Status reWeibo = weibo.getRetweeted_status();
        llRe.setTag(reWeibo);

        tvReContent.setText(reWeibo.getContentSpannableString());
        tvReContent.setTag(reWeibo);
    }

    @OnClick(R.id.ll_re)
    public void onReLineaLayoutClick(View view) {
        Status weibo = (Status) view.getTag();
        if (weibo.getUser() != null) {
            Intent intent = StatusDetailActivity.newIntent(getContext(), weibo.getId());
            getContext().startActivity(intent);
        }else {
            ToastUtil.show(getContext(), getContext().getString(R.string.weibo_deleted));
        }
    }

}
