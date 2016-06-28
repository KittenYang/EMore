package com.caij.weiyo.present.imp;

import android.text.SpannableString;

import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.bean.response.QueryRepostWeiboResponse;
import com.caij.weiyo.present.WeiboRepostsPresent;
import com.caij.weiyo.present.view.WeiboRepostsView;
import com.caij.weiyo.source.DefaultResponseSubscriber;
import com.caij.weiyo.source.RepostSource;
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
 * Created by Caij on 2016/6/28.
 */
public class WeiboRepostsPresentImp implements WeiboRepostsPresent {

    private static final int PAGE_COUNET = 20;

    private final CompositeSubscription mLoginCompositeSubscription;
    private String mToken;
    private long mWeiboId;
    RepostSource mServerRepostSource;
    WeiboRepostsView mWeiboRepostsView;
    List<Weibo> mWeobos;

    public WeiboRepostsPresentImp(String token, long weiboId, RepostSource repostSource, WeiboRepostsView repostsView) {
        mToken = token;
        mServerRepostSource = repostSource;
        mWeiboRepostsView = repostsView;
        mWeiboId = weiboId;
        mLoginCompositeSubscription = new CompositeSubscription();
        mWeobos = new ArrayList<>();
    }

    @Override
    public void onFirstVisible() {
        Subscription subscription = mServerRepostSource.getRepostWeibos(mToken, mWeiboId, 0, 0, PAGE_COUNET, 1)
                .flatMap(new Func1<QueryRepostWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(QueryRepostWeiboResponse queryRepostWeiboResponse) {
                        return Observable.from(queryRepostWeiboResponse.getReposts());
                    }
                })
                .map(new Func1<Weibo, Weibo>() {
                    @Override
                    public Weibo call(Weibo weibo) {
                        SpannableString content  = SpannableString.valueOf(weibo.getText());
                        SpannableStringUtil.paraeSpannable(content, mWeiboRepostsView.getContent());
                        weibo.setContentSpannableString(content);
                        return weibo;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mWeiboRepostsView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mWeiboRepostsView.onComnLoadError();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeobos.addAll(weibos);
                        mWeiboRepostsView.setWeibos(weibos);
                        if (weibos.size() == 0) {
                            mWeiboRepostsView.onEmpty();
                        }else {
                            mWeiboRepostsView.onLoadComplite(weibos.size() >= PAGE_COUNET);
                        }
                    }
                });

        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void onLoadMore() {
        long maxId = 0;
        if (mWeobos.size() > 0) {
            maxId = mWeobos.get(mWeobos.size() - 1).getId();
        }
        Subscription subscription = mServerRepostSource.getRepostWeibos(mToken, mWeiboId, 0, maxId, PAGE_COUNET, 1)
                .flatMap(new Func1<QueryRepostWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(QueryRepostWeiboResponse queryRepostWeiboResponse) {
                        return Observable.from(queryRepostWeiboResponse.getReposts());
                    }
                })
                .filter(new Func1<Weibo, Boolean>() {
                    @Override
                    public Boolean call(Weibo weibo) {
                        return !mWeobos.contains(weibo);
                    }
                })
                .map(new Func1<Weibo, Weibo>() {
                    @Override
                    public Weibo call(Weibo weibo) {
                        SpannableString content  = SpannableString.valueOf(weibo.getText());
                        SpannableStringUtil.paraeSpannable(content, mWeiboRepostsView.getContent());
                        weibo.setContentSpannableString(content);
                        return weibo;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mWeiboRepostsView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mWeiboRepostsView.onComnLoadError();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeobos.addAll(weibos);
                        mWeiboRepostsView.setWeibos(weibos);
                        mWeiboRepostsView.onLoadComplite(weibos.size() >= 15);
                    }
                });

        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }
}
