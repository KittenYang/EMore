package com.caij.emore.present.imp;

import com.caij.emore.account.Account;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboAndUserSearchPresent;
import com.caij.emore.ui.view.WeiboAndUserSearchView;
import com.caij.emore.source.UserSource;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by Caij on 2016/7/26.
 */
public class WeiboAndUserSearchPresentImp extends AbsListTimeLinePresent<WeiboAndUserSearchView> implements WeiboAndUserSearchPresent {

    public static final int PAGE_COUNT = 20;

    private String mKey;
    private int mPage;
    private UserSource mServerUserSource;

    public WeiboAndUserSearchPresentImp(Account account, String key, WeiboAndUserSearchView view,
                                        WeiboSource serverWeiboSource,
                                        WeiboSource localWeiboSource,
                                        UserSource serverUserSource) {
        super(account, view, serverWeiboSource, localWeiboSource);
        mKey = key;
        mServerUserSource = serverUserSource;
    }

    @Override
    public void refresh() {
        Observable<List<Weibo>> weiboObservable = createObservable(1, true)
                .doOnNext(new Action1<List<Weibo>>() {
                    @Override
                    public void call(List<Weibo> weibos) {
                        doSpanNext(weibos);
                    }
                });
        Observable<List<User>> userObservable = mServerUserSource.getSearchUser(mAccount.getToken().getAccess_token(),
                mKey, 1, PAGE_COUNT);

        Subscription subscription = Observable.zip(weiboObservable, userObservable, new Func2<List<Weibo>, List<User>, Zip>() {
            @Override
            public Zip call(List<Weibo> weibos, List<User> users) {
                        Zip zip = new Zip();
                        zip.weibos = weibos;
                        zip.users = users;
                        return zip;
                    }
                })
                .compose(new SchedulerTransformer<Zip>())
                .subscribe(new DefaultResponseSubscriber<Zip>(mView) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(Zip zip) {
                        List<Weibo> weibos = zip.weibos;
                        mWeibos.addAll(weibos);
                        mView.setEntities(weibos);
                        mView.onRefreshComplete();

                        mView.setUsers(zip.users);

                        if (weibos.size() > PAGE_COUNT - 3) {
                            mView.onLoadComplete(true);
                        }else {
                            mView.onLoadComplete(false);
                        }

                        mPage = 2;
                    }
                });

        addSubscription(subscription);
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
        Subscription subscription = createObservable(mPage, false)
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

                        if (weibos.size() > PAGE_COUNT - 3) {
                            mView.onLoadComplete(true);
                        }else {
                            mView.onLoadComplete(false);
                        }

                        mPage ++;
                    }
                });
        addSubscription(subscription);
    }

    private Observable<List<Weibo>> createObservable(int page, final boolean isRefresh) {
        return mServerWeiboSource.getSearchWeibo(mAccount.getToken().getAccess_token(), mKey, page, PAGE_COUNT)
                .compose(new ErrorCheckerTransformer<QueryWeiboResponse>())
                .flatMap(new Func1<QueryWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(QueryWeiboResponse response) {
                        return Observable.from(response.getStatuses());
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static class Zip{
        public List<Weibo> weibos;
        public List<User> users;
    }
}
