package com.caij.emore.present.imp;

import com.caij.emore.Event;
import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.account.Account;
import com.caij.emore.account.Token;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.ShortUrlInfo;
import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboActionPresent;
import com.caij.emore.source.UrlSource;
import com.caij.emore.source.local.LocalUrlSource;
import com.caij.emore.source.server.ServerUrlSource;
import com.caij.emore.ui.view.WeiboActionView;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.UrlUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.DefaultTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;


import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/7/2.
 */
public abstract class AbsTimeLinePresent<V extends WeiboActionView> extends AbsBasePresent
        implements WeiboActionPresent {

    protected V mView;
    protected WeiboSource mServerWeiboSource;
    protected Account mAccount;
    protected WeiboSource mLocalWeiboSource;
    protected UrlSource mLocalUrlSource;
    protected UrlSource mServerUrlSource;

    private Observable<Weibo> mWeiboUpdateObservable;

    public AbsTimeLinePresent(Account account, V view, WeiboSource serverWeiboSource, WeiboSource localWeiboSource) {
        super();
        mView = view;
        mAccount = account;
        mLocalUrlSource = new LocalUrlSource();
        mServerUrlSource = new ServerUrlSource();
        mServerWeiboSource = serverWeiboSource;
        mLocalWeiboSource = localWeiboSource;
    }

    @Override
    public void onCreate() {
        mWeiboUpdateObservable = RxBus.getDefault().register(Event.EVENT_WEIBO_UPDATE);
        mWeiboUpdateObservable
            .compose(new SchedulerTransformer<Weibo>())
            .subscribe(new Subscriber<Weibo>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onNext(Weibo weibo) {
                    onWeiboUpdate(weibo);
                }
            });
    }

    protected abstract void onWeiboUpdate(Weibo weibo);

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(Event.EVENT_WEIBO_UPDATE, mWeiboUpdateObservable);
    }

    @Override
    public void deleteWeibo(final Weibo weibo, final int position) {
        mView.showDialogLoading(true, R.string.deleting);
        Observable<Weibo> serverObservable = mServerWeiboSource.deleteWeibo(mAccount.getToken().getAccess_token(),
                weibo.getId())
                .compose(new DefaultTransformer<Weibo>());

        Observable<Weibo> localObservable = mLocalWeiboSource.deleteWeibo(mAccount.getToken().getAccess_token(),
                weibo.getId());
        Subscription subscription = Observable.concat(serverObservable, localObservable)
                .subscribe(new DefaultResponseSubscriber<Weibo>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mView.showDialogLoading(false, R.string.deleting);
                    }

                    @Override
                    public void onCompleted() {
                        mView.onDeleteWeiboSuccess(weibo, position);
                        mView.showDialogLoading(false, R.string.deleting);
                    }

                    @Override
                    public void onNext(Weibo weibo) {

                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void collectWeibo(final Weibo weibo) {
        Token accessToken = mAccount.getToken();
        long uid = Long.parseLong(accessToken.getUid());
        if (uid == weibo.getUser().getId()) {
            mView.showHint(R.string.self_weibo_unable_collect);
            return;
        }
        mView.showDialogLoading(true, R.string.collecting);
        Observable<FavoritesCreateResponse> serverObservable = mServerWeiboSource.collectWeibo(mAccount.getToken().getAccess_token(),
                weibo.getId())
                .compose(new DefaultTransformer<FavoritesCreateResponse>());

        Observable<FavoritesCreateResponse> localObservable = mLocalWeiboSource.collectWeibo(mAccount.getToken().getAccess_token(),
                weibo.getId());
        Subscription subscription = Observable.concat(serverObservable, localObservable)
                .subscribe(new DefaultResponseSubscriber<FavoritesCreateResponse>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mView.showDialogLoading(false, R.string.collecting);
                    }

                    @Override
                    public void onCompleted() {
                        mView.onCollectSuccess(weibo);
                        mView.showDialogLoading(false, R.string.collecting);
                    }

                    @Override
                    public void onNext(FavoritesCreateResponse favoritesCreateResponse) {

                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void uncollectWeibo(final Weibo weibo) {
        mView.showDialogLoading(true, R.string.uncollecting);
        Observable<FavoritesCreateResponse> serverObservable = mServerWeiboSource.uncollectWeibo(mAccount.getToken().getAccess_token(),
                weibo.getId())
                .compose(new DefaultTransformer<FavoritesCreateResponse>());

        Observable<FavoritesCreateResponse> localObservable = mLocalWeiboSource.uncollectWeibo(mAccount.getToken().getAccess_token(),
                weibo.getId());
        Subscription subscription = Observable.concat(serverObservable, localObservable)
                .subscribe(new DefaultResponseSubscriber<FavoritesCreateResponse>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mView.showDialogLoading(false, R.string.uncollecting);
                    }

                    @Override
                    public void onCompleted() {
                        mView.onUncollectSuccess(weibo);
                        mView.showDialogLoading(false, R.string.uncollecting);
                    }

                    @Override
                    public void onNext(FavoritesCreateResponse favoritesCreateResponse) {

                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void attitudesWeibo(final Weibo weibo) {
        final Token accessToken = mAccount.getToken();
        long uid = Long.parseLong(accessToken.getUid());
        if (uid == weibo.getUser().getId()) {
            mView.showHint(R.string.self_weibo_unable_attitude);
            return;
        }

        mView.showDialogLoading(true, R.string.requesting);
        final String token  = mAccount.getToken().getAccess_token();
        Observable<Attitude> serverObservable = mServerWeiboSource.attitudesWeibo(token, "smile", weibo.getId())
                .compose(new DefaultTransformer<Attitude>())
                .doOnNext(new Action1<Attitude>() {
                    @Override
                    public void call(Attitude attitude) {
                        mLocalWeiboSource.attitudesWeibo(token, "smile", weibo.getId());
                        postAttitudeWeiboUpdate(attitude);
                    }
                });

        Subscription subscription = serverObservable
                .subscribe(new DefaultResponseSubscriber<Attitude>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mView.showDialogLoading(false, R.string.requesting);
                    }

                    @Override
                    public void onCompleted() {
                        mView.showDialogLoading(false, R.string.requesting);
                    }

                    @Override
                    public void onNext(Attitude attitude) {
                        if (attitude != null) {
                            RxBus.getDefault().post(Event.EVENT_ATTITUDE_WEIBO_SUCCESS, attitude);
                        }
                    }
                });
        addSubscription(subscription);
    }

    private void postAttitudeWeiboUpdate(Attitude attitude) {
        final Weibo data = attitude.getStatus();
        if (data != null) {
            mLocalWeiboSource.getWeiboById(mAccount.getToken().getAccess_token(), data.getId())
            .filter(new Func1<Weibo, Boolean>() {
                @Override
                public Boolean call(Weibo weibo) {
                    return weibo != null;
                }
            })
            .doOnNext(new Action1<Weibo>() {
                @Override
                public void call(Weibo weibo) {
                    weibo.setAttitudes_count(data.getAttitudes_count());
                    weibo.setReposts_count(data.getReposts_count());
                    weibo.setComments_count(data.getComments_count());
                    weibo.setUpdate_time(System.currentTimeMillis());
                    weibo.setAttitudes_status(1);
                    mLocalWeiboSource.saveWeibo(mAccount.getToken().getAccess_token(), weibo);
                }
            }).subscribe(new Subscriber<Weibo>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Weibo weibo) {
                    RxBus.getDefault().post(Event.EVENT_WEIBO_UPDATE, weibo);
                }
            });

        }
    }

    @Override
    public void destoryAttitudesWeibo(final Weibo weibo) {
        mView.showDialogLoading(true, R.string.requesting);
        final String token  = mAccount.getToken().getAccess_token();
        Observable<Response> serverObservable = mServerWeiboSource.destoryAttitudesWeibo(token,
                "smile", weibo.getId())
                .compose(new DefaultTransformer<Response>());

        Subscription subscription = serverObservable
                .doOnNext(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        mLocalWeiboSource.destoryAttitudesWeibo(token, "smile", weibo.getId());
                        weibo.setAttitudes_status(0);
                    }
                })
                .subscribe(new DefaultResponseSubscriber<Response>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mView.showDialogLoading(false, R.string.requesting);
                    }

                    @Override
                    public void onCompleted() {
                        mView.showDialogLoading(false, R.string.requesting);
                        RxBus.getDefault().post(Event.EVENT_WEIBO_UPDATE, weibo);
                    }

                    @Override
                    public void onNext(Response response) {

                    }
                });
        addSubscription(subscription);
    }

    protected void doSpanNext(List<Weibo> weibos) {
        for (Weibo weibo : weibos) {
            SpannableStringUtil.paraeSpannable(weibo);
        }
    }

    protected void doSpanNext(Weibo weibo) {
        doSpanNext(weibo, false);
    }


    protected void doSpanNext(Weibo weibo, boolean isLongText) {
        SpannableStringUtil.paraeSpannable(weibo, isLongText);
    }

}
