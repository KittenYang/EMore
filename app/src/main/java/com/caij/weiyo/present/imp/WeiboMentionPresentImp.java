package com.caij.weiyo.present.imp;

import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.bean.response.QueryWeiboCommentResponse;
import com.caij.weiyo.bean.response.QueryWeiboResponse;
import com.caij.weiyo.present.MentionPresent;
import com.caij.weiyo.present.WeiboMentionPresent;
import com.caij.weiyo.present.view.MentionView;
import com.caij.weiyo.present.view.TimeLineWeiboView;
import com.caij.weiyo.source.WeiboSource;
import com.caij.weiyo.utils.SpannableStringUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/4.
 */
public class WeiboMentionPresentImp extends AbsTimeLinePresent implements WeiboMentionPresent {

    private static final int COUNT = 20;

    private final CompositeSubscription mLoginCompositeSubscription;
    private String mToken;
    private WeiboSource mWeiboSource;
    private TimeLineWeiboView mTimeLineWeiboView;
    private List<Weibo> mWeibos;

    public WeiboMentionPresentImp(String token, WeiboSource weiboSource, TimeLineWeiboView timeLineWeiboView) {
        super(token, timeLineWeiboView, weiboSource);
        mToken = token;
        mWeiboSource = weiboSource;
        mTimeLineWeiboView = timeLineWeiboView;
        mWeibos = new ArrayList<>();
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onUserFirstVisible() {
        mTimeLineWeiboView.toRefresh();
    }

    @Override
    public void onRefresh() {
        Subscription su =  mWeiboSource.getWeiboMentions(mToken, 0 ,0, COUNT, 1)
                .flatMap(new Func1<QueryWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(QueryWeiboResponse queryWeiboResponse) {
                        return Observable.from(queryWeiboResponse.getStatuses());
                    }
                })
                .map(new Func1<Weibo, Weibo>() {

                    @Override
                    public Weibo call(Weibo weibo) {
                        toGetImageSize(weibo);
                        SpannableStringUtil.paraeSpannable(weibo, mView.getContent().getApplicationContext());
                        return weibo;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Weibo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mTimeLineWeiboView.onComnLoadError();
                        mTimeLineWeiboView.onRefreshComplite();
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mTimeLineWeiboView.setWeibos(mWeibos);

                        mTimeLineWeiboView.onRefreshComplite();
                        mTimeLineWeiboView.onLoadComplite(weibos.size() > COUNT - 1);
                    }
                });
        mLoginCompositeSubscription.add(su);
    }

    @Override
    public void onLoadMore() {
        long maxId = 0;
        if (mWeibos != null && mWeibos.size() > 1) {
            maxId = mWeibos.get(mWeibos.size() - 1).getId();
        }
        Subscription su = mWeiboSource.getWeiboMentions(mToken, 0, maxId, COUNT, 1)
                .flatMap(new Func1<QueryWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(QueryWeiboResponse queryWeiboResponse) {
                        return Observable.from(queryWeiboResponse.getStatuses());
                    }
                })
                .filter(new Func1<Weibo, Boolean>() {
                    @Override
                    public Boolean call(Weibo weibo) {
                        return !mWeibos.contains(weibo);
                    }
                })
                .map(new Func1<Weibo, Weibo>() {

                    @Override
                    public Weibo call(Weibo weibo) {
                        toGetImageSize(weibo);
                        SpannableStringUtil.paraeSpannable(weibo, mView.getContent().getApplicationContext());
                        return weibo;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Weibo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mTimeLineWeiboView.onComnLoadError();
                        mTimeLineWeiboView.onLoadComplite(true);
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mTimeLineWeiboView.setWeibos(mWeibos);

                        mTimeLineWeiboView.onLoadComplite(weibos.size() > COUNT - 1);
                    }
                });
        mLoginCompositeSubscription.add(su);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }
}
