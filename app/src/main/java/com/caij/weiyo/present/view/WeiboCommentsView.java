package com.caij.weiyo.present.view;


import android.content.Context;

import com.caij.weiyo.bean.Comment;

import java.util.List;

/**
 * Created by Caij on 2016/5/31.
 */
public interface WeiboCommentsView extends BaseListView<Comment> {

    Context getContent();

    void onDeleteSuccess(Comment comment);
}
