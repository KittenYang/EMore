package com.caij.emore.present.imp;

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

    public WeiboDetailPresentImp(String token, long weiboId, WeiboDetailView view,
                                 WeiboSource serverWeiboSource,
                                 WeiboSource localWeiboSource) {
        super(token, view, serverWeiboSource, localWeiboSource);
        mWeiboId = weiboId;
    }

    @Override
    public void loadWeiboDetail() {
//        HashMap<String, Object> params = new HashMap<>();
//        ApiUtil.appendAuthSina(params);
//        params.put("isGetLongText", 1);
//        Observable<Weibo> localObservable = mLocalWeiboSource.getWeiboById(params, mWeiboId);
//        mView.showDialogLoading(true);
//        Observable<Weibo> serverObservable = mServerWeiboSource.getWeiboById(params, mWeiboId)
//                .doOnNext(new Action1<Weibo>() {
//                    @Override
//                    public void call(Weibo weibo) {
//                        weibo.transformPicUrlsByPicIds();
//                        weibo.transformText();
//                        mLocalWeiboSource.saveWeibo(mToken, weibo);
//                    }
//                });
//        Subscription subscription = Observable.concat(localObservable, serverObservable)
//                .first(new Func1<Weibo, Boolean>() {
//                    @Override
//                    public Boolean call(Weibo weibo) {
//                        return weibo != null
//                                && weibo.getUpdate_time() != null
//                                && System.currentTimeMillis() - weibo.getUpdate_time() < 10 * 60 * 1000
//                                && !weibo.getText().contains("全文： http");
//                    }
//                })
//                .doOnNext(new Action1<Weibo>() {
//                    @Override
//                    public void call(Weibo weibo) {
//                        toGetImageSize(weibo);
//                        weibo.setAttitudes(mLocalWeiboSource.getAttitudes(weibo.getId()));
//                        doSpanNext(weibo);
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Weibo>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        mView.onDefaultLoadError();
//                        mView.showDialogLoading(false);
//                    }
//
//                    @Override
//                    public void onNext(Weibo weibo) {
//                        mView.setWeibo(weibo);
//                        mView.showDialogLoading(false);
//                    }
//                });
//        mCompositeSubscription.add(subscription);

        Observable<Weibo> localObservable = mLocalWeiboSource.getWeiboById(mToken, mWeiboId);
        Observable<Weibo> serverObservable = mServerWeiboSource.getWeiboById(mToken, mWeiboId);
        mView.showDialogLoading(true);
        Subscription subscription = Observable.concat(localObservable, serverObservable)
                .first(new Func1<Weibo, Boolean>() {
                    @Override
                    public Boolean call(Weibo weibo) {
                        return weibo != null
                                && weibo.getUpdate_time() != null
                                && System.currentTimeMillis() - weibo.getUpdate_time() < 10 * 60 * 1000;
                    }
                })
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        toGetImageSize(weibo);
                        weibo.setAttitudes(mLocalWeiboSource.getAttitudes(weibo.getId()));
                        doSpanNext(weibo);
                    }
                }).
                subscribeOn(Schedulers.io())
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
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mCompositeSubscription.clear();
    }
}
