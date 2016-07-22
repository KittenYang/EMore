package com.caij.emore.present.imp;

import com.caij.emore.Key;
import com.caij.emore.bean.Account;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboDetailPresent;
import com.caij.emore.present.view.WeiboDetailView;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.weibo.ApiUtil;

import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Caij on 2016/7/14.
 */
public class WeiboDetailPresentImp extends AbsTimeLinePresent<WeiboDetailView> implements WeiboDetailPresent {

    private long mWeiboId;

    public WeiboDetailPresentImp(Account account, long weiboId, WeiboDetailView view,
                                 WeiboSource serverWeiboSource,
                                 WeiboSource localWeiboSource) {
        super(account, view, serverWeiboSource, localWeiboSource);
        mWeiboId = weiboId;
    }

    @Override
    public void loadWeiboDetail() {
        final String token  = mAccount.getWeicoToken().getAccess_token();
        Observable<Weibo> localObservable = mLocalWeiboSource.getWeiboById(token,
                Key.WEICO_APP_ID, 1, mWeiboId);
        mView.showDialogLoading(true);
        Observable<Weibo> serverObservable = mServerWeiboSource.getWeiboById(token,
                Key.WEICO_APP_ID, 1, mWeiboId)
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        weibo.transformPicUrlsByPicIds();
                        weibo.transformText();
                        mLocalWeiboSource.saveWeibo(token, weibo);
                    }
                });
        Subscription subscription = Observable.concat(localObservable, serverObservable)
                .first(new Func1<Weibo, Boolean>() {
                    @Override
                    public Boolean call(Weibo weibo) {
                        return weibo != null
                                && weibo.getUpdate_time() != null
                                && System.currentTimeMillis() - weibo.getUpdate_time() < 10 * 60 * 1000
                                && !weibo.getText().contains("全文： http");
                    }
                })
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        toGetImageSize(weibo);
                        weibo.setAttitudes(mLocalWeiboSource.getAttitudes(weibo.getId()));
                        doSpanNext(weibo);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weibo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onDefaultLoadError();
                        mView.showDialogLoading(false);
                    }

                    @Override
                    public void onNext(Weibo weibo) {
                        mView.setWeibo(weibo);
                        mView.showDialogLoading(false);
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void refreshWeiboDetail() {
        final String token  = mAccount.getWeicoToken().getAccess_token();
        Observable<Weibo> serverObservable = mServerWeiboSource.getWeiboById(token,
                Key.WEICO_APP_ID, 1, mWeiboId);
        Subscription subscription = serverObservable
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        weibo.transformPicUrlsByPicIds();
                        weibo.transformText();
                        mLocalWeiboSource.saveWeibo(token, weibo);

                        toGetImageSize(weibo);
                        weibo.setAttitudes(mLocalWeiboSource.getAttitudes(weibo.getId()));
                        doSpanNext(weibo);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weibo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onDefaultLoadError();
                    }

                    @Override
                    public void onNext(Weibo weibo) {
                        mView.setWeibo(weibo);
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mCompositeSubscription.clear();
    }
}
