package com.caij.emore.present.imp;

import com.caij.emore.account.Account;
import com.caij.emore.bean.response.UserWeiboResponse;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.UserWeiboPresent;
import com.caij.emore.ui.view.TimeLineWeiboView;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/5/31.
 */
public class UserWeiboPresentImp extends AbsListTimeLinePresent<TimeLineWeiboView> implements UserWeiboPresent {

    private final static int PAGE_COUNT = 20;

    private String mUsername;
    private int mFeature = 0;

    public UserWeiboPresentImp(Account account, String name, TimeLineWeiboView view, WeiboSource serverWeiboSource, WeiboSource localWeiboSource) {
        super(account, view, serverWeiboSource, localWeiboSource);
        mUsername = name;
    }

    @Override
    public void filter(int feature) {
        if (mFeature != feature) {
            mFeature = feature;
            mWeibos.clear();
            mView.setEntities(mWeibos);
            refresh();
        }
    }

    @Override
    public void refresh() {
        Subscription subscription = createObservable(0, true)
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    protected void onFail(Throwable e) {
                        if (mWeibos.size() == 0) {
                            mView.showErrorView();
                        }
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.clear();
                        mWeibos.addAll(weibos);
                        mView.setEntities(mWeibos);

                        mView.onLoadComplete(weibos.size() >= PAGE_COUNT);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void userFirstVisible() {
        refresh();
    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mWeibos.size() > 0) {
            maxId = mWeibos.get(mWeibos.size() - 1).getId();
        }
        Subscription subscription = createObservable(maxId, false)
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
                        mView.onLoadComplete(weibos.size() >= PAGE_COUNT - 1); //这里有一条重复的 所以需要-1
                    }
                });
        addSubscription(subscription);
    }

    private Observable<List<Weibo>> createObservable(long maxId, final boolean isRefresh) {
        return mServerWeiboSource.getUseWeibo(mAccount.getWeiCoToken().getAccess_token(), mUsername, mFeature, 0, maxId, PAGE_COUNT, 1)
                .compose(new ErrorCheckerTransformer<UserWeiboResponse>())
                .flatMap(new Func1<UserWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(UserWeiboResponse response) {
                        return Observable.from(response.getStatuses());
                    }
                })
                .filter(new Func1<Weibo, Boolean>() {
                    @Override
                    public Boolean call(Weibo weibo) {
                        return isRefresh || !mWeibos.contains(weibo);
                    }
                })
                .map(new Func1<Weibo, Weibo>() {

                    @Override
                    public Weibo call(Weibo weibo) {
                        weibo.setAttitudes(mLocalWeiboSource.getAttitudes(weibo.getId()));
                        toGetImageSize(weibo);
                        return weibo;
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
