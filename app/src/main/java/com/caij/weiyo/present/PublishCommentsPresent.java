package com.caij.weiyo.present;

import com.caij.weiyo.bean.Comment;

/**
 * Created by Caij on 2016/7/5.
 */
public interface PublishCommentsPresent extends MentionPresent {

    public void deleteComment(Comment comment, int position);
}
