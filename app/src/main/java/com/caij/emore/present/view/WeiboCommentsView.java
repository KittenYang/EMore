package com.caij.emore.present.view;


import android.content.Context;

import com.caij.emore.bean.Comment;

import java.util.List;

/**
 * Created by Caij on 2016/5/31.
 */
public interface WeiboCommentsView extends BaseListView<Comment> {

    void onDeleteSuccess(Comment comment);

    void onCommentSuccess(List<Comment> comments);
}
