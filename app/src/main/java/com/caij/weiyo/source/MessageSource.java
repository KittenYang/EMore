package com.caij.weiyo.source;

import com.caij.weiyo.bean.UnreadMessage;

import rx.Observable;

/**
 * Created by Caij on 2016/7/7.
 */
public interface MessageSource {
    Observable<UnreadMessage> getUnReadMessage(String accessToken, long uid) ;
}
