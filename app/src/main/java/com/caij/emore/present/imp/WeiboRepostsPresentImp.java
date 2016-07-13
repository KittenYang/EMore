package com.caij.emore.present.imp;

import android.text.SpannableString;

import com.caij.emore.bean.response.QueryRepostWeiboResponse;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboRepostsPresent;
import com.caij.emore.present.view.WeiboRepostsView;
import com.caij.emore.source.DefaultResponseSubscriber;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.SpannableStringUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
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
    WeiboSource mServerRepostSource;
    WeiboRepostsView mWeiboRepostsView;
    List<Weibo> mWeobos;

    public WeiboRepostsPresentImp(String token, long weiboId, WeiboSource repostSource, WeiboRepostsView repostsView) {
        mToken = token;
        mServerRepostSource = repostSource;
        mWeiboRepostsView = repostsView;
        mWeiboId = weiboId;
        mLoginCompositeSubscription = new CompositeSubscription();
        mWeobos = new ArrayList<>();
    }

    @Override
    public void userFirstVisible() {
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
                        mWeiboRepostsView.onDefaultLoadError();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeobos.addAll(weibos);
                        mWeiboRepostsView.setEntities(weibos);
                        if (weibos.size() == 0) {
                            mWeiboRepostsView.onEmpty();
                        }else {
                            mWeiboRepostsView.onLoadComplete(weibos.size() >= PAGE_COUNET);
                        }
                    }
                });

        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void loadMore() {
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
                        mWeiboRepostsView.onDefaultLoadError();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeobos.addAll(weibos);
                        mWeiboRepostsView.setEntities(weibos);
                        mWeiboRepostsView.onLoadComplete(weibos.size() >= 15);
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
