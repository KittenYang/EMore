package com.caij.emore.remote;

import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.response.AttitudeResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.bean.response.StatusAttitudesResponse;

import rx.Observable;

/**
 * Created by Caij on 2016/10/9.
 */

public interface AttitudeApi {

    public Observable<Attitude> attitudesToWeibo(String attitude, long weiboId);

    public Observable<Response> destoryAttitudesWeibo(String attitude, long weiboId);

    public Observable<StatusAttitudesResponse> getWeiboAttiyudes(long id, int page, int count);

    public Observable<AttitudeResponse> getToMeAttitudes(long maxId, long sinceId, int page, int count);
}
