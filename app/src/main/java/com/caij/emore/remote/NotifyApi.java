package com.caij.emore.remote;

import com.caij.emore.bean.response.Response;
import com.caij.emore.database.bean.UnReadMessage;

import rx.Observable;

/**
 * Created by Caij on 2016/10/9.
 */

public interface NotifyApi {

    public Observable<UnReadMessage> getUnReadMessage(long uid);

    public Observable<Response> resetUnReadMessage(long uid, String type, int value);
}
