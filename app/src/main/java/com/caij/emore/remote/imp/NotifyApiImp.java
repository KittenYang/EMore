package com.caij.emore.remote.imp;

import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.response.Response;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.remote.NotifyApi;

import rx.Observable;

/**
 * Created by Caij on 2016/10/9.
 */

public class NotifyApiImp implements NotifyApi {

    private WeiCoService mWeiCoService;

    public NotifyApiImp() {
        mWeiCoService =  WeiCoService.WeiCoFactory.create();
    }

    @Override
    public Observable<UnReadMessage> getUnReadMessage(long uid) {
        return mWeiCoService.getUnreadMessageCount(uid);
    }

    @Override
    public Observable<Response> resetUnReadMessage(long uid, String type, int value) {
        return mWeiCoService.resetUnReadMsg(type, value);
    }
}
