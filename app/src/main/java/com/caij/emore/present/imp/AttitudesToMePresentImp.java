package com.caij.emore.present.imp;

import com.caij.emore.Key;
import com.caij.emore.account.Account;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.response.QueryWeiboAttitudeResponse;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.present.RefreshListPresent;
import com.caij.emore.ui.view.RefreshListView;
import com.caij.emore.source.MessageSource;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;
import com.caij.emore.utils.weibo.MessageUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/4.
 */
public class AttitudesToMePresentImp extends AbsBasePresent implements RefreshListPresent {

    private static final int COUNT = 20;

    private Account mAccount;
    private WeiboSource mWeiboSource;
    private RefreshListView<Attitude> mView;
    private List<Attitude> mAttitudes;
    MessageSource mServerMessageSource;
    MessageSource mLocalMessageSource;

    public AttitudesToMePresentImp(Account account, WeiboSource weiboSource,
                                   MessageSource serverMessageSource,
                                   MessageSource localMessageSource,
                                   RefreshListView<Attitude> view) {
        super();
        mAccount = account;
        mWeiboSource = weiboSource;
        mView = view;
        mServerMessageSource = serverMessageSource;
        mLocalMessageSource = localMessageSource;
        mAttitudes = new ArrayList<>();
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void refresh() {
        Subscription su =  createGetAttitudeObservable(0, true)
                .subscribe(new DefaultResponseSubscriber<List<Attitude>>(mView) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<Attitude> attitudes) {
                        mAttitudes.addAll(attitudes);
                        mView.setEntities(mAttitudes);

                        mView.onRefreshComplete();
                        mView.onLoadComplete(attitudes.size() > COUNT - 1);

                        MessageUtil.resetUnReadMessage(mAccount.getToken().getAccess_token(), UnReadMessage.TYPE_ATTITUDE,
                                mAccount.getUid(), mServerMessageSource, mLocalMessageSource);
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
                .subscribe(new DefaultResponseSubscriber<List<Attitude>>(mView) {
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
        return mWeiboSource.getToMeAttiyudes(mAccount.getToken().getAccess_token(), maxId, 0, Key.WEICO_APP_ID, Key.WEICO_APP_FROM, 1, COUNT)
                .compose(new ErrorCheckerTransformer<QueryWeiboAttitudeResponse>())
                .flatMap(new Func1<QueryWeiboAttitudeResponse, Observable<Attitude>>() {
                    @Override
                    public Observable<Attitude> call(QueryWeiboAttitudeResponse queryWeiboAttitudeResponse) {
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
