package com.caij.emore.present;

import com.caij.emore.bean.Comment;

/**
 * Created by Caij on 2016/7/5.
 */
public interface PublishCommentsPresent extends RefreshListPresent {

    public void deleteComment(Comment comment, int position);
}
