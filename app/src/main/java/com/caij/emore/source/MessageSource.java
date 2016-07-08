package com.caij.emore.source;

import com.caij.emore.bean.UnreadMessage;

import rx.Observable;

/**
 * Created by Caij on 2016/7/7.
 */
public interface MessageSource {
    Observable<UnreadMessage> getUnReadMessage(String accessToken, long uid) ;
}
