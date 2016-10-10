package com.caij.emore.present.imp;

import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.dao.NotifyManager;
import com.caij.emore.dao.StatusManager;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboMentionPresent;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.remote.UnReadMessageApi;
import com.caij.emore.ui.view.TimeLineWeiboView;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;
import com.caij.emore.utils.weibo.MessageUtil;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/7/4.
 */
public class WeiboMentionPresentImp extends AbsListTimeLinePresent<TimeLineWeiboView> implements WeiboMentionPresent {

    private static final int COUNT = 20;

    private long mUid;

    private UnReadMessageApi mUnReadMessageApi;
    private NotifyManager mNotifyManager;

    public WeiboMentionPresentImp(long uid, TimeLineWeiboView view, StatusApi statusApi,
                                  StatusManager statusManager, AttitudeApi attitudeApi,
                                  UnReadMessageApi unReadMessageApi, NotifyManager notifyManager) {
        super(view, statusApi, statusManager, attitudeApi);
        mUid = uid;
        mUnReadMessageApi = unReadMessageApi;
        mNotifyManager = notifyManager;
    }

    @Override
    public void userFirstVisible() {
        refresh();
    }

    @Override
    public void refresh() {
        Subscription su = createObservable(0, true)
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.setEntities(mWeibos);

                        mView.onRefreshComplete();
                        mView.onLoadComplete(weibos.size() > COUNT - 2);

                        MessageUtil.resetUnReadMessage(UnReadMessage.TYPE_MENTION_STATUS, mUid,
                                mUnReadMessageApi, mNotifyManager);
                    }
                });
        addSubscription(su);
    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mWeibos != null && mWeibos.size() > 1) {
            maxId = mWeibos.get(mWeibos.size() - 1).getId();
        }
        Subscription su = createObservable(maxId, false)
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.notifyItemRangeInserted(mWeibos, mWeibos.size() - weibos.size(), weibos.size());

                        mView.onLoadComplete(weibos.size() > COUNT - 1);
                    }
                });
        addSubscription(su);
    }

    private Observable<List<Weibo>> createObservable(long maxId, final boolean isRefresh) {
        return mStatusApi.getWeiboMentions(0, maxId, COUNT, 1)
                .compose(new ErrorCheckerTransformer<QueryWeiboResponse>())
                .flatMap(new Func1<QueryWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(QueryWeiboResponse queryWeiboResponse) {
                        return Observable.from(queryWeiboResponse.getStatuses());
                    }
                })
                .filter(new Func1<Weibo, Boolean>() {
                    @Override
                    public Boolean call(Weibo weibo) {
                        return isRefresh || !mWeibos.contains(weibo);
                    }
                })
                .toList()
                .doOnNext(new Action1<List<Weibo>>() {
                    @Override
                    public void call(List<Weibo> weibos) {
                        doSpanNext(weibos);
                    }
                })
                .compose(new SchedulerTransformer<List<Weibo>>());
    }

}
