package com.caij.emore.widget.weibo.list;

import android.content.Context;
import android.util.AttributeSet;

import com.caij.emore.R;
import com.caij.emore.database.bean.Status;

/**
 * Created by Caij on 2016/9/1.
 */
public class RepostWeiboListArticleItemView extends RepostWeiboListItemView {

    private CompositePatternArticle mCompositePatternArticle;

    public RepostWeiboListArticleItemView(Context context) {
        super(context);
        init();
    }

    public RepostWeiboListArticleItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RepostWeiboListArticleItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RepostWeiboListArticleItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        //这里采用组合模式 解决多继承问题
        mCompositePatternArticle = new CompositePatternArticle();
        mCompositePatternArticle.setUp(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_weibo_repost_article_item;
    }

    @Override
    public void setWeibo(Status weibo) {
        super.setWeibo(weibo);
        mCompositePatternArticle.setWeibo(weibo, getContext());
    }

}
