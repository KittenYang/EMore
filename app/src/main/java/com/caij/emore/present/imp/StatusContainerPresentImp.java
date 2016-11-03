package com.caij.emore.present.imp;

import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.bean.GroupResponse;
import com.caij.emore.database.bean.Group;
import com.caij.emore.present.StatusContainerPresent;
import com.caij.emore.repository.GroupRepository;
import com.caij.emore.ui.view.StatusContainerView;

import java.util.List;

import rx.Subscription;

/**
 * Created by Caij on 2016/11/2.
 */

public class StatusContainerPresentImp extends AbsBasePresent implements StatusContainerPresent {

    private StatusContainerView mStatusContainerView;
    private GroupRepository mGroupRepository;

    public StatusContainerPresentImp(StatusContainerView statusContainerView, GroupRepository groupRepository) {
        mStatusContainerView = statusContainerView;
        mGroupRepository = groupRepository;
    }


    @Override
    public void onCreate() {
        Subscription subscription = mGroupRepository.getGroups().compose(SchedulerTransformer.<List<Group>>create())
                .subscribe(new ResponseSubscriber<List<Group>>(mStatusContainerView) {
                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Group> groups) {
                        mStatusContainerView.onGetGroups(groups);
                    }
                });
        addSubscription(subscription);
    }

}
