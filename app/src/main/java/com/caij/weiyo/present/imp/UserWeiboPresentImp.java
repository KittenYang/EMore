package com.caij.weiyo.present.imp;

import com.caij.weiyo.bean.PicUrl;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.bean.response.UserWeiboResponse;
import com.caij.weiyo.database.bean.LocakImage;
import com.caij.weiyo.present.FriendWeiboPresent;
import com.caij.weiyo.present.UserWeiboPresent;
import com.caij.weiyo.present.view.TimeLineWeiboView;
import com.caij.weiyo.source.ImageSouce;
import com.caij.weiyo.source.WeiboSource;
import com.caij.weiyo.source.local.LocalImageSource;
import com.caij.weiyo.source.server.ServerImageSource;
import com.caij.weiyo.utils.LogUtil;
import com.caij.weiyo.utils.SpannableStringUtil;

import java.io.IOException;
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
public class UserWeiboPresentImp implements UserWeiboPresent {

    private final static int PAGE_COUNT = 20;

    private String mToken;
    private TimeLineWeiboView mView;
    private WeiboSource mServerWeiboSource;
    private CompositeSubscription mLoginCompositeSubscription;
    private List<Weibo> mWeibos;
    private ImageSouce mServerImageSouce;
    private ImageSouce mLocalImageSouce;
    private String mUsername;
    private int mFeature = 0;

    public UserWeiboPresentImp(String token, String name, TimeLineWeiboView view, WeiboSource serverWeiboSource) {
        mToken = token;
        mView = view;
        mUsername = name;
        mServerWeiboSource = serverWeiboSource;
        mLoginCompositeSubscription = new CompositeSubscription();
        mLocalImageSouce = new LocalImageSource();
        mServerImageSouce = new ServerImageSource();
        mWeibos = new ArrayList<>();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onRefresh() {
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
                        mView.onRefreshComplite();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onComnLoadError();
                        mView.onRefreshComplite();
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.clear();
                        mWeibos.addAll(weibos);
                        mView.setWeibos(mWeibos);
                        if (weibos.size() == 0) {
                            mView.onEmpty();
                        }else {
                            mView.onLoadComplite(weibos.size() >= PAGE_COUNT);
                        }
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    private void toGetImageSize(Weibo weibo) {
        Weibo realWeibo = weibo.getRetweeted_status() != null ? weibo.getRetweeted_status() : weibo;
        if (realWeibo.getPic_urls() != null && realWeibo.getPic_urls().size() == 1) {
            PicUrl picUrl = realWeibo.getPic_urls().get(0);
            try {
                LocakImage image = mLocalImageSouce.get(picUrl.getThumbnail_pic());
                if (image == null) {
                    image = mServerImageSouce.get(picUrl.getThumbnail_pic());
                    mLocalImageSouce.save(image);
                    LogUtil.d(this, picUrl.getThumbnail_pic() + "pic width and height from server");
                }
                picUrl.setWidth(image.getWidth());
                picUrl.setHeight(image.getHeight());
                LogUtil.d(this, picUrl.getThumbnail_pic() + "  width:" + image.getWidth()
                        + "  height:" + image.getHeight());
            } catch (IOException e) {
                LogUtil.d(this, "%s 图片尺寸获取失败", picUrl.getThumbnail_pic());
            }
        }
    }


    @Override
    public void onLoadMore() {
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
                        mView.onComnLoadError();
                        mView.onLoadComplite(true);
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.setWeibos(mWeibos);
                        mView.onLoadComplite(weibos.size() >= PAGE_COUNT - 1); //这里有一条重复的 所以需要-1
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }

    @Override
    public void onFirstVisible() {
        onRefresh();
    }

    @Override
    public void filter(int feature) {
        if (mFeature != feature) {
            mFeature = feature;
            mWeibos.clear();
            mView.setWeibos(mWeibos);
            onRefresh();
        }
    }
}
