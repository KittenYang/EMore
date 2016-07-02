package com.orientationchangedemo.caij.weiyo;

import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.bean.response.QueryWeiboCommentResponse;
import com.caij.weiyo.source.CommentSource;
import com.caij.weiyo.source.server.ServerCommentSource;

import org.junit.Test;

import java.util.List;

import rx.Subscriber;

import static org.junit.Assert.assertFalse;

/**
 * Created by Caij on 2016/6/16.
 */
public class CommentTest {

    @Test
    public void getCommentsByWeibo() {
        CommentSource source = new ServerCommentSource();
        source.getCommentsByWeibo(Key.token, 3987011179777802L, 0, 0 , 20, 1)
        .subscribe(new Subscriber<List<Comment>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Comment> comments) {

            }
        });
    }
}
