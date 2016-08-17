package com.caij.emore.ui.view;


import com.caij.emore.bean.Comment;

import java.util.List;

/**
 * Created by Caij on 2016/5/31.
 */
public interface WeiboCommentsView extends ListView<Comment> {

    void onDeleteSuccess(Comment comment);

    void onCommentSuccess(List<Comment> comments);
}
