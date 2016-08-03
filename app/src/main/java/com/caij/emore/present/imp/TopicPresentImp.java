package com.caij.emore.present.imp;

import com.caij.emore.bean.Account;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboMentionPresent;
import com.caij.emore.present.view.TimeLineWeiboView;
import com.caij.emore.source.MessageSource;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.weibo.MessageUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Caij on 2016/7/4.
 */
public class TopicPresentImp extends AbsTimeLinePresent<TimeLineWeiboView> implements WeiboMentionPresent {

    private static final int COUNT = 20;

    private List<Weibo> mWeibos;
    private String mTopic;
    private int page;

    public TopicPresentImp(Account account, String topic, WeiboSource serverWeiboSource,
                           WeiboSource localWeiboSource,
                           TimeLineWeiboView timeLineWeiboView) {
        super(account, timeLineWeiboView, serverWeiboSource, localWeiboSource);
        mWeibos = new ArrayList<>();
        mTopic = topic;
    }

    @Override
    public void userFirstVisible() {
    }

    @Override
    public void refresh() {
        Subscription su = createObservable(1, true)
                .subscribe(new Subscriber<List<Weibo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onDefaultLoadError();
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.setEntities(mWeibos);

                        mView.onRefreshComplete();
                        mView.onLoadComplete(weibos.size() > COUNT - 1);

                        page = 2;
                    }
                });
        mCompositeSubscription.add(su);
    }

    @Override
    public void loadMore() {
        Subscription su = createObservable(page, false)
                .subscribe(new Subscriber<List<Weibo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onDefaultLoadError();
                        mView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.setEntities(mWeibos);

                        mView.onLoadComplete(weibos.size() > COUNT - 1);

                        page++;
                    }
                });
        mCompositeSubscription.add(su);
    }

    private Observable<List<Weibo>> createObservable(int page, final boolean isRefresh) {
        return mServerWeiboSource.getTopicsByKey(mAccount.getWeicoToken().getAccess_token(), mTopic, page, COUNT)
                .flatMap(new Func1<List<Weibo>, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(List<Weibo> queryWeiboResponse) {
                        return Observable.from(queryWeiboResponse);
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mCompositeSubscription.clear();
    }


}