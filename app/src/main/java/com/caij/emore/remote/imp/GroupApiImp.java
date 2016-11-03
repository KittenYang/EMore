package com.caij.emore.remote.imp;

import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.GroupResponse;
import com.caij.emore.remote.GroupApi;

import rx.Observable;

/**
 * Created by Caij on 2016/11/2.
 */

public class GroupApiImp implements GroupApi {

    private WeiCoService mWeiCoService;

    public GroupApiImp() {
        mWeiCoService = WeiCoService.WeiCoFactory.create();
    }

    @Override
    public Observable<GroupResponse> getGroups() {
        return mWeiCoService.getGroups();
    }
}
