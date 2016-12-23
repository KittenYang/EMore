package com.caij.emore.widget.weibo.list;

import android.content.Context;
import android.util.AttributeSet;

import com.caij.emore.R;
import com.caij.emore.database.bean.Status;

/**
 * Created by Caij on 2016/9/1.
 */
public class RepostStatusArticleItemView extends RepostStatusItemView {

    private CompositePatternArticle mCompositePatternArticle;

    public RepostStatusArticleItemView(Context context) {
        super(context);
        init();
    }

    public RepostStatusArticleItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RepostStatusArticleItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RepostStatusArticleItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
    public void setStatus(Status status) {
        super.setStatus(status);
        mCompositePatternArticle.setWeibo(status, getContext());
    }

}
