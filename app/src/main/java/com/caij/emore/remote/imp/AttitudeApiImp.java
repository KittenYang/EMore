package com.caij.emore.remote.imp;

import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.response.AttitudeResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.bean.response.StatusAttitudesResponse;
import com.caij.emore.remote.AttitudeApi;

import rx.Observable;

/**
 * Created by Caij on 2016/10/9.
 */

public class AttitudeApiImp implements AttitudeApi {

    private WeiCoService mWeiCoService;

    public AttitudeApiImp() {
        mWeiCoService = WeiCoService.WeiCoFactory.create();
    }

    @Override
    public Observable<Attitude> attitudesToStatus(String attitude, long statusId) {
        return mWeiCoService.attitudesWeibo(attitude, statusId);
    }

    @Override
    public Observable<Response> destroyAttitudesStatus(String attitude, long statusId) {
        return mWeiCoService.destoryAttitudesWeibo(attitude, statusId);
    }

    @Override
    public Observable<StatusAttitudesResponse> getStatusAttitudes(long id, int page, int count) {
        return mWeiCoService.getWeiboAttitudes(id, page, count);
    }

    @Override
    public Observable<AttitudeResponse> getToMeAttitudes(long maxId, long sinceId, int page, int count) {
        return mWeiCoService.getToMeAttitudes(sinceId, maxId, page, count);
    }
}
