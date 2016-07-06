package com.caij.weiyo.present.view;

import com.caij.weiyo.bean.Comment;


/**
 * Created by Caij on 2016/7/4.
 */
public interface MyPublishComentsView extends RefreshListView<Comment> {

    void onDeleteCommentSuccess(Comment comment, int position);

}
