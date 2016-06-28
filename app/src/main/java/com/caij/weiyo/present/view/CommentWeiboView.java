package com.caij.weiyo.present.view;

import com.caij.weiyo.bean.Comment;

/**
 * Created by Caij on 2016/6/27.
 */
public interface CommentWeiboView extends BaseView{

    public void onCommentSuccess(Comment comment);

    void showLoading(boolean isShow);
}
