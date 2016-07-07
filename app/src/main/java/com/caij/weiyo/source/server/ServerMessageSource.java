package com.caij.weiyo.source.server;

import com.caij.weiyo.api.WeiBoService;
import com.caij.weiyo.bean.UnreadMessage;
import com.caij.weiyo.source.MessageSource;

import rx.Observable;

/**
 * Created by Caij on 2016/7/7.
 */
public class ServerMessageSource implements MessageSource {

    private WeiBoService mWeiBoService;

    public ServerMessageSource() {
        mWeiBoService = WeiBoService.Factory.create();
    }

    @Override
    public Observable<UnreadMessage> getUnReadMessage(String accessToken, long uid) {
        return mWeiBoService.getUnReadMessage("https://rm.api.weibo.com/2/remind/unread_count.json", accessToken, uid);
    }
}
