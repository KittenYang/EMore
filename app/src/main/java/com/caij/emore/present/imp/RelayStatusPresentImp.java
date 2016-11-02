package com.caij.emore.present.imp;

import com.caij.emore.EventTag;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.bean.event.RelayStatusEvent;
import com.caij.emore.database.bean.Status;
import com.caij.emore.present.RepostWeiboPresent;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.RelayStatusView;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.api.ex.DefaultTransformer;

import rx.Subscription;

/**
 * Created by Caij on 2016/6/27.
 */
public class RelayStatusPresentImp extends AbsBasePresent implements RepostWeiboPresent {

    private long mStatusId;
    private StatusApi mStatusApi;
    private RelayStatusView mRelayStatusView;

    public RelayStatusPresentImp(long statusId, StatusApi statusApi, RelayStatusView relayStatusView) {
        this.mStatusId = statusId;
        this.mStatusApi = statusApi;
        this.mRelayStatusView = relayStatusView;
    }

    @Override
    public void relayStatus(String status) {
        Subscription subscription = mStatusApi.relayStatus(status, mStatusId)
                .compose(new DefaultTransformer<Status>())
                .subscribe(new ResponseSubscriber<Status>(mRelayStatusView) {

                    @Override
                    protected void onFail(Throwable e) {
                        mRelayStatusView.showDialogLoading(false);
                    }

                    @Override
                    public void onNext(Status relayStatus) {
                        mRelayStatusView.showDialogLoading(false);
                        mRelayStatusView.onRepostSuccess(relayStatus);

                        RelayStatusEvent relayStatusEvent = new RelayStatusEvent(EventTag.EVENT_REPOST_WEIBO_SUCCESS, relayStatus, mStatusId);
                        RxBus.getDefault().post(relayStatusEvent.type, relayStatusEvent);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void onCreate() {

    }

}
