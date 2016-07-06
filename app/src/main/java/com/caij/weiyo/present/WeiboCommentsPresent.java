package com.caij.weiyo.present;

import com.caij.weiyo.bean.Comment;

/**
 * Created by Caij on 2016/6/16.
 */
public interface WeiboCommentsPresent extends ListPresent{

    void deleteComment(Comment comment);
}
