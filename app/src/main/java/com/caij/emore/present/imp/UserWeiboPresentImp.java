package com.caij.emore.present.imp;

import com.caij.emore.R;
import com.caij.emore.bean.Weibo;
import com.caij.emore.bean.response.UserWeiboResponse;
import com.caij.emore.present.UserWeiboPresent;
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

/**
 * Created by Caij on 2016/5/31.
 */
public class UserWeiboPresentImp extends AbsTimeLinePresent implements UserWeiboPresent {

    private final static int PAGE_COUNT = 20;

    private List<Weibo> mWeibos;

    private String mUsername;
    private int mFeature = 0;

    public UserWeiboPresentImp(String token, String name, TimeLineWeiboView view, WeiboSource serverWeiboSource) {
        super(token, view, serverWeiboSource);
        mUsername = name;
        mWeibos = new ArrayList<>();
    }


    @Override
    public void deleteWeibo(final Weibo weibo, final int position) {
        mView.showDialogLoading(true, R.string.deleting);
        mServerWeiboSource.deleteWeibo(mToken, weibo.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weibo>() {
                    @Override
                    public void onCompleted() {
                        mView.onDeleteWeiboSuccess(weibo, position);
                        mView.showDialogLoading(false, R.string.deleting);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onDefaultLoadError();
                        mView.showDialogLoading(false, R.string.deleting);
                    }

                    @Override
                    public void onNext(Weibo weibo) {

                    }
                });
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }

    @Override
    public void filter(int feature) {
        if (mFeature != feature) {
            mFeature = feature;
            mWeibos.clear();
            mView.setEntities(mWeibos);
            refresh();
        }
    }

    @Override
    public void refresh() {
        Subscription subscription = mServerWeiboSource.getUseWeibo(mToken, mUsername, mFeature, 0, 0, PAGE_COUNT, 1)
                .flatMap(new Func1<UserWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(UserWeiboResponse response) {
                        return Observable.from(response.getStatuses());
                    }
                })
                .map(new Func1<Weibo, Weibo>() {

                    @Override
                    public Weibo call(Weibo weibo) {
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
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onDefaultLoadError();
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.clear();
                        mWeibos.addAll(weibos);
                        mView.setEntities(mWeibos);
                        if (weibos.size() == 0) {
                            mView.onEmpty();
                        }else {
                            mView.onLoadComplete(weibos.size() >= PAGE_COUNT);
                        }
                    }
                });
        mLoginCompositeSubscription.add(subscription);
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
        Subscription subscription = mServerWeiboSource.getUseWeibo(mToken, mUsername, mFeature, 0, maxId, PAGE_COUNT, 1)
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
                .map(new Func1<Weibo, Weibo>() {

                    @Override
                    public Weibo call(Weibo weibo) {
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
                        mView.onDefaultLoadError();
                        mView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.setEntities(mWeibos);
                        mView.onLoadComplete(weibos.size() >= PAGE_COUNT - 1); //这里有一条重复的 所以需要-1
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }
}
