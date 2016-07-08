package com.caij.emore.present.view;

import com.caij.emore.bean.Comment;


/**
 * Created by Caij on 2016/7/4.
 */
public interface MyPublishComentsView extends RefreshListView<Comment> {

    void onDeleteCommentSuccess(Comment comment, int position);

}
