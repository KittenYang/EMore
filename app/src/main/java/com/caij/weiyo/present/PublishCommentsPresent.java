package com.caij.weiyo.present;

import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.present.view.RefreshListView;

/**
 * Created by Caij on 2016/7/5.
 */
public interface PublishCommentsPresent extends RefreshListPresent {

    public void deleteComment(Comment comment, int position);
}
