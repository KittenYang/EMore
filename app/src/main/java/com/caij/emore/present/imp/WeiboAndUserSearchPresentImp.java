package com.caij.emore.present.imp;

import com.caij.emore.bean.Account;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.TimeLinePresent;
import com.caij.emore.present.WeiboAndUserSearchPresent;
import com.caij.emore.present.view.WeiboAndUserSearchView;
import com.caij.emore.source.UserSource;
import com.caij.emore.source.WeiboSource;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by Caij on 2016/7/26.
 */
public class WeiboAndUserSearchPresentImp extends AbsTimeLinePresent<WeiboAndUserSearchView> implements WeiboAndUserSearchPresent {

    public static final int PAGE_COUNT = 20;

    private String mKey;
    private int mPage;
    private List<Weibo> mWeibos;
    private UserSource mServerUserSource;

    public WeiboAndUserSearchPresentImp(Account account, String key, WeiboAndUserSearchView view,
                                        WeiboSource serverWeiboSource,
                                        WeiboSource localWeiboSource,
                                        UserSource serverUserSource) {
        super(account, view, serverWeiboSource, localWeiboSource);
        mKey = key;
        mServerUserSource = serverUserSource;
        mWeibos = new ArrayList<>();
    }

    @Override
    public void refresh() {
        Observable<List<Weibo>> weiboObservable = createObservable(1, true);
        Observable<List<User>> userObservable = mServerUserSource.getSearchUser(mAccount.getWeicoToken().getAccess_token(),
                mKey, 1, PAGE_COUNT);

        Subscription subscription = Observable.zip(weiboObservable, userObservable, new Func2<List<Weibo>, List<User>, Zip>() {
            @Override
            public Zip call(List<Weibo> weibos, List<User> users) {
                Zip zip = new Zip();
                zip.weibos = weibos;
                zip.users = users;
                return zip;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Zip>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
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

        mCompositeSubscription.add(subscription);
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
        Subscription subscription = createObservable(mPage, false)
                .subscribe(new Subscriber<List<Weibo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.setEntities(weibos);

                        if (weibos.size() > PAGE_COUNT - 3) {
                            mView.onLoadComplete(true);
                        }else {
                            mView.onLoadComplete(false);
                        }

                        mPage ++;
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onCreate() {

    }

    private Observable<List<Weibo>> createObservable(int page, final boolean isRefresh) {
        return mServerWeiboSource.getSearchWeibo(mAccount.getWeicoToken().getAccess_token(), mKey, page, PAGE_COUNT)
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

    private static class Zip{
        public List<Weibo> weibos;
        public List<User> users;
    }
}
