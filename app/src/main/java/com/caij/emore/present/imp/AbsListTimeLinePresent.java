package com.caij.emore.present.imp;


import com.caij.emore.bean.event.StatusActionCountUpdateEvent;
import com.caij.emore.bean.event.StatusAttitudeEvent;
import com.caij.emore.manager.StatusManager;
import com.caij.emore.database.bean.Status;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.ListView;
import com.caij.emore.ui.view.WeiboActionView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Caij on 2016/8/12.
 */
public abstract class AbsListTimeLinePresent<V extends WeiboActionView & ListView<Status>> extends AbsTimeLinePresent<V> {

    protected List<Status> mStatuses;

    public AbsListTimeLinePresent(V view, StatusApi statusApi, StatusManager statusManager, AttitudeApi attitudeApi) {
        super(view, statusApi, statusManager, attitudeApi);
        mStatuses = new ArrayList<>();
    }

    @Override
    protected void onStatusAttitudeCountUpdate(StatusActionCountUpdateEvent event) {
        for (int index = 0; index < mStatuses.size(); index++) {
            Status status = mStatuses.get(index);
            if (status.getId() == event.statusId) {
                status.setAttitudes_count(event.count);
                mView.notifyItemChanged(mStatuses, index);
                break;
            }
        }
    }

    @Override
    protected void onStatusCommentCountUpdate(StatusActionCountUpdateEvent event) {
        for (int index = 0; index < mStatuses.size(); index++) {
            Status status = mStatuses.get(index);
            if (status.getId() == event.statusId) {
                status.setComments_count(event.count);
                mView.notifyItemChanged(mStatuses, index);
                break;
            }
        }
    }

    @Override
    protected void onStatusRelayCountUpdate(StatusActionCountUpdateEvent event) {
        for (int index = 0; index < mStatuses.size(); index++) {
            Status status = mStatuses.get(index);
            if (status.getId() == event.statusId) {
                status.setReposts_count(event.count);
                mView.notifyItemChanged(mStatuses, index);
                break;
            }
        }
    }

    @Override
    protected void onStatusAttitudeUpdate(StatusAttitudeEvent event) {
        for (int index = 0; index < mStatuses.size(); index++) {
            Status status = mStatuses.get(index);
            if (status.getId() == event.statusId) {
                if (event.isAttitude) {
                    status.setAttitudes_status(1);
                }else {
                    status.setAttitudes_status(0);
                }
                mView.notifyItemChanged(mStatuses, index);
                break;
            }
        }
    }
}
