package com.caij.emore.widget.weibo.list;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.bean.PageInfo;
import com.caij.emore.database.bean.Status;
import com.caij.emore.ui.activity.ArticleActivity;
import com.caij.emore.utils.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/9/3.
 */
public class CompositePatternArticle {

    @BindView(R.id.iv_article)
    ImageView mIvArticle;
    @BindView(R.id.iv_article_type)
    ImageView mIvArticleType;
    @BindView(R.id.tv_user_name)
    TextView mTvUserName;
    @BindView(R.id.tv_article_title)
    TextView mTvArticleTitle;
    @BindView(R.id.rl_article)
    RelativeLayout mRlArticle;

    ImageLoader.ImageConfig mVideoImageConfig;

    public void setUp(View view) {
        ButterKnife.bind(this, view);
        mVideoImageConfig = new ImageLoader.ImageConfigBuild()
                .setScaleType(ImageLoader.ScaleType.CENTER_CROP)
                .build();
    }

    public void setWeibo(Status weibo, Context context) {
        PageInfo pageInfo = weibo.getPage_info();

        PageInfo.Card card1 = pageInfo.getCards().get(0);
        PageInfo.Card card2 = pageInfo.getCards().get(1);

        ImageLoader.loadUrl(context, mIvArticle, card1.getPage_pic(),
                R.drawable.weibo_image_placeholder, mVideoImageConfig);

        ImageLoader.loadUrl(context, mIvArticleType, card1.getType_icon(),
                R.drawable.weibo_image_placeholder, mVideoImageConfig);

        mTvArticleTitle.setText(card2.getContent1());
        mTvUserName.setText(card1.getContent1());

        mRlArticle.setTag(weibo);
    }

    @OnClick(R.id.rl_article)
    public void onArticleClick() {
        Status weibo = (Status) mRlArticle.getTag();
        PageInfo pageInfo = weibo.getPage_info();
        Intent intent = ArticleActivity.newIntent(mRlArticle.getContext(), pageInfo.getPage_id());
        mRlArticle.getContext().startActivity(intent);
    }
}
