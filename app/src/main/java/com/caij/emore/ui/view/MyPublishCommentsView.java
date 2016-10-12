package com.caij.emore.ui.view;

import com.caij.emore.bean.Comment;


/**
 * Created by Caij on 2016/7/4.
 */
public interface MyPublishCommentsView extends RefreshListView<Comment> {

    void onDeleteCommentSuccess(Comment comment, int position);

}
