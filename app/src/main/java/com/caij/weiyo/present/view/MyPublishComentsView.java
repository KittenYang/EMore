package com.caij.weiyo.present.view;

import com.caij.weiyo.bean.Comment;


/**
 * Created by Caij on 2016/7/4.
 */
public interface MyPublishComentsView extends MentionView<Comment> {

    void onDeleteCommentSuccess(Comment comment, int position);

    void showDialogLoading(boolean isShow);
}
