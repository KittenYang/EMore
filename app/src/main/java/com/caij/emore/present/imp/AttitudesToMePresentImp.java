package com.caij.emore.present.imp;

import com.caij.emore.account.Account;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.response.AttitudeResponse;
import com.caij.emore.manager.NotifyManager;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.present.RefreshListPresent;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.remote.NotifyApi;
import com.caij.emore.ui.view.RefreshListView;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.utils.weibo.MessageUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/7/4.
 */
public class AttitudesToMePresentImp extends AbsBasePresent implements RefreshListPresent {

    private static final int COUNT = 20;

    private Account mAccount;
    private RefreshListView<Attitude> mView;
    private List<Attitude> mAttitudes;

    private AttitudeApi mAttitudeApi;
    private NotifyApi mNotifyApi;
    private NotifyManager mNotifyManager;

    public AttitudesToMePresentImp(Account account, AttitudeApi attitudeApi,
                                   NotifyApi notifyApi, NotifyManager notifyManager,
                                   RefreshListView<Attitude> view) {
        super();
        mAccount = account;
        mAttitudeApi = attitudeApi;
        mView = view;
        mNotifyApi = notifyApi;
        mNotifyManager = notifyManager;
        mAttitudes = new ArrayList<>();
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void refresh() {
        Subscription su =  createGetAttitudeObservable(0, true)
                .subscribe(new ResponseSubscriber<List<Attitude>>(mView) {

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<Attitude> attitudes) {
                        mAttitudes.clear();

                        mAttitudes.addAll(attitudes);
                        mView.setEntities(mAttitudes);

                        mView.onRefreshComplete();
                        mView.onLoadComplete(attitudes.size() > COUNT - 1);

                        MessageUtil.resetUnReadMessage(UnReadMessage.TYPE_ATTITUDE,
                                mAccount.getUid(), mNotifyApi, mNotifyManager);
                    }
                });
        addSubscription(su);
    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mAttitudes.size() > 0) {
            maxId = mAttitudes.get(mAttitudes.size() - 1).getId();
        }
        Subscription su = createGetAttitudeObservable(maxId, false)
                .subscribe(new ResponseSubscriber<List<Attitude>>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mView.onLoadComplete(true);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<Attitude> attitudes) {
                        mAttitudes.addAll(attitudes);
                        mView.notifyItemRangeInserted(mAttitudes, mAttitudes.size() - attitudes.size(),
                                attitudes.size());

                        mView.onLoadComplete(attitudes.size() > COUNT - 1);
                    }
                });
        addSubscription(su);
    }

    private Observable<List<Attitude>> createGetAttitudeObservable(long maxId, final boolean isRefresh) {
        return mAttitudeApi.getToMeAttitudes(maxId, 0, 1, COUNT)
                .compose(new ErrorCheckerTransformer<AttitudeResponse>())
                .flatMap(new Func1<AttitudeResponse, Observable<Attitude>>() {
                    @Override
                    public Observable<Attitude> call(AttitudeResponse queryWeiboAttitudeResponse) {
                        return Observable.from(queryWeiboAttitudeResponse.getAttitudes());
                    }
                })
                .filter(new Func1<Attitude, Boolean>() {
                    @Override
                    public Boolean call(Attitude attitude) {
                        return !mAttitudes.contains(attitude) || isRefresh;
                    }
                })
                .toList()
                .compose(new SchedulerTransformer<List<Attitude>>());
    }

    @Override
    public void onCreate() {
        refresh();
    }

}
