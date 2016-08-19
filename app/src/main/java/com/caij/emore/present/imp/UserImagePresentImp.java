package com.caij.emore.present.imp;

import com.caij.emore.bean.response.UserWeiboResponse;
import com.caij.emore.database.bean.PicUrl;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.UserWeiboPresent;
import com.caij.emore.ui.view.TimeLineWeiboImageView;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/5/31.
 */
public class UserImagePresentImp extends AbsBasePresent implements UserWeiboPresent {

    private final static int PAGE_COUNT = 20;

    private String mToken;
    private TimeLineWeiboImageView mView;
    private WeiboSource mServerWeiboSource;
    private List<Weibo> mWeibos;
    private String mUsername;
    private List<PicUrl> mPicUrl;

    public UserImagePresentImp(String token, String name, TimeLineWeiboImageView view, WeiboSource serverWeiboSource) {
        mToken = token;
        mView = view;
        mUsername = name;
        mServerWeiboSource = serverWeiboSource;
        mWeibos = new ArrayList<>();
        mPicUrl = new ArrayList<>();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void deleteWeibo(Weibo weibo, int position) {

    }

    @Override
    public void collectWeibo(Weibo weibo) {

    }

    @Override
    public void uncollectWeibo(Weibo weibo) {

    }

    @Override
    public void attitudesWeibo(Weibo weibo) {

    }

    @Override
    public void destoryAttitudesWeibo(Weibo weibo) {

    }

    @Override
    public void filter(int feature) {

    }

    @Override
    public void refresh() {
        Subscription subscription = mServerWeiboSource.getUseWeibo(mToken, mUsername, 2, 0, 0, PAGE_COUNT, 1)
                .flatMap(new Func1<UserWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(UserWeiboResponse response) {
                        return Observable.from(response.getStatuses());
                    }
                })
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        mPicUrl.addAll(weibo.getPic_urls());
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        if (mPicUrl.size() == 0) {
                            mView.showErrorView();
                        }
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.clear();
                        mWeibos.addAll(weibos);
                        mView.setEntities(mPicUrl);

                        mView.onLoadComplete(weibos.size() >= PAGE_COUNT);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void userFirstVisible() {
        refresh();
    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mWeibos.size() > 0) {
            maxId = mWeibos.get(mWeibos.size() - 1).getId();
        }
        Subscription subscription = mServerWeiboSource.getUseWeibo(mToken, mUsername, 2, 0, maxId, PAGE_COUNT, 1)
                .flatMap(new Func1<UserWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(UserWeiboResponse response) {
                        return Observable.from(response.getStatuses());
                    }
                })
                .filter(new Func1<Weibo, Boolean>() {
                    @Override
                    public Boolean call(Weibo weibo) {
                        return !mWeibos.contains(weibo);
                    }
                })
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        mPicUrl.addAll(weibo.getPic_urls());
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
                        mView.onDefaultLoadError();
                        mView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.setEntities(mPicUrl);
                        mView.onLoadComplete(weibos.size() >= PAGE_COUNT - 1); //这里有一条重复的 所以需要-1
                    }
                });
        addSubscription(subscription);
    }
}
