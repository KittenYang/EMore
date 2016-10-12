package com.caij.emore.present;

import com.caij.emore.bean.Comment;

/**
 * Created by Caij on 2016/6/16.
 */
public interface StatusCommentsPresent extends ListPresent{

    void deleteComment(Comment comment);
}
