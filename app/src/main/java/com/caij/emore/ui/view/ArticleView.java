package com.caij.emore.ui.view;

import com.caij.emore.bean.Article;

/**
 * Created by Caij on 2016/9/3.
 */
public interface ArticleView extends BaseView{

    void onArticleLoadSuccess(Article article);
}
