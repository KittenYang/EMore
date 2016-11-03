package com.caij.emore.repository;

import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.bean.GroupResponse;
import com.caij.emore.database.bean.Group;
import com.caij.emore.manager.GroupManager;
import com.caij.emore.remote.GroupApi;
import com.caij.emore.utils.rxjava.RxUtil;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/11/2.
 */

public class GroupRepository {

    private GroupApi mGroupApi;
    private GroupManager mGroupManager;

    public GroupRepository(GroupApi groupApi, GroupManager groupManager) {
        mGroupApi = groupApi;
        mGroupManager = groupManager;
    }

    public Observable<List<Group>> getGroups() {
        return Observable.<List<Group>>concat(getCacheGroups(), mGroupApi.getGroups()
                .compose(ErrorCheckerTransformer.<GroupResponse>create())
                .flatMap(new Func1<GroupResponse, Observable<List<Group>>>() {
                    @Override
                    public Observable<List<Group>> call(GroupResponse groupResponse) {
                        return Observable.just(groupResponse.getLists());
                    }
                }).doOnNext(new Action1<List<Group>>() {
                    @Override
                    public void call(List<Group> groups) {
                        mGroupManager.clear();
                        mGroupManager.saveGroups(groups);
                    }
                }));
    }

    private Observable<List<Group>> getCacheGroups() {
        return RxUtil.createDataObservable(new RxUtil.Provider<List<Group>>() {
            @Override
            public List<Group> getData() throws Exception {
                return mGroupManager.getGroups();
            }
        });
    }
}
