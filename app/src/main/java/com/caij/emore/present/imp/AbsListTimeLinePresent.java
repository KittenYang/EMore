package com.caij.emore.present.imp;


import com.caij.emore.bean.event.StatusAttitudeCountUpdateEvent;
import com.caij.emore.bean.event.StatusAttitudeEvent;
import com.caij.emore.dao.StatusManager;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.ListView;
import com.caij.emore.ui.view.WeiboActionView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Caij on 2016/8/12.
 */
public abstract class AbsListTimeLinePresent<V extends WeiboActionView & ListView<Weibo>> extends AbsTimeLinePresent<V> {

    protected List<Weibo> mWeibos;

    public AbsListTimeLinePresent(V view, StatusApi statusApi, StatusManager statusManager, AttitudeApi attitudeApi) {
        super(view, statusApi, statusManager, attitudeApi);
        mWeibos = new ArrayList<>();
    }

    @Override
    protected void onStatusAttitudeCountUpdate(StatusAttitudeCountUpdateEvent event) {
        for (int index = 0; index < mWeibos.size(); index++) {
            Weibo weibo = mWeibos.get(index);
            if (weibo.getId() == event.statusId) {
                weibo.setAttitudes_count(event.count);
                mView.notifyItemChanged(mWeibos, index);
                break;
            }
        }
    }

    @Override
    protected void onStatusAttitudeUpdate(StatusAttitudeEvent event) {
        for (int index = 0; index < mWeibos.size(); index++) {
            Weibo weibo = mWeibos.get(index);
            if (weibo.getId() == event.statusId) {
                if (event.isAttitude) {
                    weibo.setAttitudes_status(1);
                }else {
                    weibo.setAttitudes_status(0);
                }
                mView.notifyItemChanged(mWeibos, index);
                break;
            }
        }
    }
}
