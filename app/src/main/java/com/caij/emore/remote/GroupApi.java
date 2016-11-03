package com.caij.emore.remote;

import com.caij.emore.bean.GroupResponse;

import rx.Observable;

/**
 * Created by Caij on 2016/11/2.
 */

public interface GroupApi {

    Observable<GroupResponse> getGroups();
}
