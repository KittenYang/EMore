package com.caij.emore.present.imp;

import com.caij.emore.bean.Account;
import com.caij.emore.bean.WeiboIds;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.FriendWeiboPresent;
import com.caij.emore.present.HotWeiboPresent;
import com.caij.emore.present.view.FriendWeiboView;
import com.caij.emore.present.view.TimeLineWeiboView;
import com.caij.emore.source.DefaultResponseSubscriber;
import com.caij.emore.source.WeiboSource;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Caij on 2016/5/31.
 */
public class HotWeiboPresentImp extends AbsTimeLinePresent<TimeLineWeiboView> implements FriendWeiboPresent, HotWeiboPresent {

    private final static int PAGE_COUNT = 20;

    private List<Weibo> mWeibos;
    private int page;

    public HotWeiboPresentImp(Account account, TimeLineWeiboView view, WeiboSource serverWeiboSource,
                              WeiboSource localWeiboSource) {
        super(account, view, serverWeiboSource, localWeiboSource);
        mWeibos = new ArrayList<>();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void refresh() {
        Subscription subscription = createObservable(1, true)
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {
                    @Override
                    public void onCompleted() {
                        mView.onRefreshComplete();
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onDefaultLoadError();
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.clear();
                        mWeibos.addAll(weibos);
                        mView.setEntities(mWeibos);
                        if (weibos.size() == 0) {
                            mView.onEmpty();
                        }else {
                            mView.onLoadComplete(weibos.size() >= PAGE_COUNT - 1);
                        }

                        page = 2;
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
        Subscription subscription = createObservable(page, false)
            .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        protected void onFail(Throwable e) {
                            mView.onDefaultLoadError();
                            mView.onLoadComplete(true);
                        }

                        @Override
                        public void onNext(List<Weibo> weibos) {
                            page ++;
                            mWeibos.addAll(weibos);
                            mView.setEntities(mWeibos);
                            mView.onLoadComplete(weibos.size() >= PAGE_COUNT - 2); //这里有一条重复的 所以需要-1
                        }
                    });
        mCompositeSubscription.add(subscription);
    }

    private Observable<List<Weibo>> createObservable(int page, final boolean isRefresh) {
        final String token = mAccount.getWeicoToken().getAccess_token();
        return mServerWeiboSource.getHotWeibosIds(token, page)
                .flatMap(new Func1<WeiboIds, Observable<List<Weibo>>>() {
                    @Override
                    public Observable<List<Weibo>> call(WeiboIds weiboIds) {
                        StringBuilder sb = new StringBuilder();
                        for (long id : weiboIds.getIds()) {
                            sb.append(id).append(",");
                        }
                        return mServerWeiboSource.getWeibosByIds(token, sb.toString());
                    }
                })
                .flatMap(new Func1<List<Weibo>, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(List<Weibo> weibos) {
                        return Observable.from(weibos);
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
                        toGetImageSize(weibo);
                        weibo.setAttitudes(mLocalWeiboSource.getAttitudes(weibo.getId()));
                        return weibo;
                    }
                })
                .toList()
                .doOnNext(new Action1<List<Weibo>>() {
                    @Override
                    public void call(List<Weibo> weibos) {
                        mLocalWeiboSource.saveWeibos(mAccount.getWeiyoToken().getAccess_token(), weibos);
                        doSpanNext(weibos);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void onDestroy() {
        mCompositeSubscription.clear();
    }

}
