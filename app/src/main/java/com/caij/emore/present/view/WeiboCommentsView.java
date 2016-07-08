package com.caij.emore.present.view;


import android.content.Context;

import com.caij.emore.bean.Comment;

/**
 * Created by Caij on 2016/5/31.
 */
public interface WeiboCommentsView extends BaseListView<Comment> {

    Context getContent();

    void onDeleteSuccess(Comment comment);
}
