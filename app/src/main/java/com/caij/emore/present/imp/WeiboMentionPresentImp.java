package com.caij.emore.present.imp;

import com.caij.emore.bean.Weibo;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.present.WeiboMentionPresent;
import com.caij.emore.present.view.TimeLineWeiboView;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.SpannableStringUtil;

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
    private TimeLineWeiboView mTimeLineWeiboView;
    private List<Weibo> mWeibos;

    public WeiboMentionPresentImp(String token, WeiboSource serverWeiboSource, WeiboSource localWeiboSource, TimeLineWeiboView timeLineWeiboView) {
        super(token, timeLineWeiboView, serverWeiboSource, localWeiboSource);
        mToken = token;
        mTimeLineWeiboView = timeLineWeiboView;
        mWeibos = new ArrayList<>();
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void userFirstVisible() {
        refresh();
    }

    @Override
    public void refresh() {
        Subscription su =  mServerWeiboSource.getWeiboMentions(mToken, 0 ,0, COUNT, 1)
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
                        SpannableStringUtil.paraeSpannable(weibo);
                        weibo.setAttitudes(mLocalWeiboSource.getAttitudes(weibo.getId()));
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
                        mTimeLineWeiboView.onDefaultLoadError();
                        mTimeLineWeiboView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mTimeLineWeiboView.setEntities(mWeibos);

                        mTimeLineWeiboView.onRefreshComplete();
                        mTimeLineWeiboView.onLoadComplete(weibos.size() > COUNT - 1);
                    }
                });
        mLoginCompositeSubscription.add(su);
    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mWeibos != null && mWeibos.size() > 1) {
            maxId = mWeibos.get(mWeibos.size() - 1).getId();
        }
        Subscription su = mServerWeiboSource.getWeiboMentions(mToken, 0, maxId, COUNT, 1)
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
                        weibo.setAttitudes(mLocalWeiboSource.getAttitudes(weibo.getId()));
                        toGetImageSize(weibo);
                        SpannableStringUtil.paraeSpannable(weibo);
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
                        mTimeLineWeiboView.onDefaultLoadError();
                        mTimeLineWeiboView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mTimeLineWeiboView.setEntities(mWeibos);

                        mTimeLineWeiboView.onLoadComplete(weibos.size() > COUNT - 1);
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
