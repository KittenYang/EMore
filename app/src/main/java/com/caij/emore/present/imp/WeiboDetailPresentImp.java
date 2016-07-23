package com.caij.emore.present.imp;

import com.caij.emore.Key;
import com.caij.emore.bean.Account;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.response.QueryRepostWeiboResponse;
import com.caij.emore.database.bean.UrlInfo;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboDetailPresent;
import com.caij.emore.present.view.WeiboDetailView;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.UrlUtil;
import com.caij.emore.utils.rxbus.RxBus;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func4;
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
        Observable<Weibo> weiboObservable = mServerWeiboSource.getWeiboById(token,
                Key.WEICO_APP_ID, 1, mWeiboId)
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
                });

        Observable<List<Weibo>> observableRepostWeibo = mServerWeiboSource.
                getRepostWeibos(token, mWeiboId, 0, 0, 20, 1)
                .flatMap(new Func1<QueryRepostWeiboResponse, Observable<List<Weibo>>>() {
                    @Override
                    public Observable<List<Weibo>> call(QueryRepostWeiboResponse queryRepostWeiboResponse) {
                        return Observable.just(queryRepostWeiboResponse.getReposts());
                    }
                })
                .doOnNext(new Action1<List<Weibo>>() {
                    @Override
                    public void call(List<Weibo> weibos) {
                        doSpanNext(weibos);
                    }
                });

        Observable<List<Comment>> observableWeiboCommnet = mServerWeiboSource.getCommentsByWeibo(token, mWeiboId, 0, 0, 20, 1)
                .doOnNext(new Action1<List<Comment>>() {
                    @Override
                    public void call(List<Comment> comments) {
                        List<String> shortUrls  = SpannableStringUtil.getCommentTextHttpUrl(comments);
                        Map<String, UrlInfo> shortLongLinkMap = UrlUtil.getShortUrlInfos(shortUrls,
                                mServerUrlSource, mLocalUrlSource, token);
                        for (Comment comment : comments) {
                            SpannableStringUtil.paraeSpannable(comment, shortLongLinkMap);
                        }
                    }
                });

        Observable<List<Attitude>> observableWeiboAttitude = mServerWeiboSource.getWeiboAttiyudes(token, mWeiboId, 1, 20);

        Subscription subscription = Observable.zip(weiboObservable, observableRepostWeibo,
                observableWeiboCommnet, observableWeiboAttitude,
                new Func4<Weibo, List<Weibo>, List<Comment>, List<Attitude>, Zip>() {
                    @Override
                    public Zip call(Weibo weibo, List<Weibo> weibos, List<Comment> comments, List<Attitude> attitudes) {
                        Zip zip = new Zip();
                        zip.weibo = weibo;
                        zip.weibos = weibos;
                        zip.comments = comments;
                        zip.attitudes = attitudes;
                        return zip;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Zip>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onDefaultLoadError();
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(Zip zip) {
                        mView.setWeibo(zip.weibo);
                        RxBus.get().post(Key.EVENT_REPOST_WEIBO_REFRESH_COMPLETE, zip.weibos);
                        RxBus.get().post(Key.EVENT_WEIBO_COMMENTS_REFRESH_COMPLETE, zip.comments);
                        RxBus.get().post(Key.EVENT_WEIBO_ATTITUDE_REFRESH_COMPLETE, zip.attitudes);
                        mView.onRefreshComplete();
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

    private static class Zip {
        Weibo weibo;
        List<Weibo> weibos;
        List<Comment> comments;
        List<Attitude> attitudes;
    }
}
