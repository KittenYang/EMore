package com.caij.weiyo.present.imp;

import android.text.SpannableString;
import android.text.TextUtils;

import com.caij.weiyo.R;
import com.caij.weiyo.bean.PicUrl;
import com.caij.weiyo.bean.User;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.database.bean.LocakImage;
import com.caij.weiyo.present.FriendWeiboPresent;
import com.caij.weiyo.present.view.FriendWeiboView;
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
public class FriendWeiboPresentImp implements FriendWeiboPresent {

    private final static int PAGE_COUNT = 20;

    private String mToken;
    private FriendWeiboView mView;
    private WeiboSource mServerWeiboSource;
    private WeiboSource mLocalWeiboSource;
    private CompositeSubscription mLoginCompositeSubscription;
    private List<Weibo> mWeibos;
    private ImageSouce mLocalImageSouce;
    private ImageSouce mServerImageSouce;

    public FriendWeiboPresentImp(String token, FriendWeiboView view, WeiboSource serverWeiboSource,
                                 WeiboSource localWeiboSource) {
        mToken = token;
        mView = view;
        mServerWeiboSource = serverWeiboSource;
        mLocalWeiboSource = localWeiboSource;
        mLoginCompositeSubscription = new CompositeSubscription();
        mWeibos = new ArrayList<>();
        mLocalImageSouce = new LocalImageSource();
        mServerImageSouce = new ServerImageSource();
    }

    @Override
    public void onCreate() {
        Subscription subscription = mLocalWeiboSource.getFriendWeibo(mToken, 0, 0, PAGE_COUNT, 1)
                .flatMap(new Func1<List<Weibo>, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(List<Weibo> weibos) {
                        return Observable.from(weibos);
                    }
                })
                .map(new Func1<Weibo, Weibo>() {
                    @Override
                    public Weibo call(Weibo weibo) {
                        toGetImageSize(weibo);
                        paraeSpannable(weibo);
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

                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.setFriendWeibo(mWeibos);
                        mView.toRefresh();
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void onRefresh() {
        Subscription subscription = mServerWeiboSource.getFriendWeibo(mToken, 0, 0, PAGE_COUNT, 1)
                .flatMap(new Func1<List<Weibo>, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(List<Weibo> weibos) {
                        return Observable.from(weibos);
                    }
                })
                .map(new Func1<Weibo, Weibo>() {

                    @Override
                    public Weibo call(Weibo weibo) {
                        toGetImageSize(weibo);
                        paraeSpannable(weibo);
                        return weibo;
                    }
                })
                .toList()
                .doOnNext(new Action1<List<Weibo>>() {
                    @Override
                    public void call(List<Weibo> weibos) {
                        LogUtil.d("onRefresh", "TO SAVE LOACL");
                        mLocalWeiboSource.saveFriendWeibo(mToken, weibos);
                    }
                })
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
                        mView.setFriendWeibo(mWeibos);
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    private void toGetImageSize(Weibo weibo) {
        Weibo realWeibo = weibo.getRetweeted_status() != null ? weibo.getRetweeted_status() : weibo;
        if (realWeibo.getPic_urls() != null && realWeibo.getPic_urls().size() == 1) {
            PicUrl picUrl = realWeibo.getPic_urls().get(0);
            try {
                LocakImage image = mServerImageSouce.get(picUrl.getThumbnail_pic());
                picUrl.setWidth(image.getWidth());
                picUrl.setHeight(image.getHeight());
                LogUtil.d(this, picUrl.getThumbnail_pic() + "  width:" + image.getWidth()
                        + "  height:" + image.getHeight());
                mLocalImageSouce.save(image);
            } catch (IOException e) {
                LogUtil.d(this, "%s 图片尺寸获取失败", picUrl.getThumbnail_pic());
            }
        }
    }

    private void paraeSpannable(Weibo weibo) {
        int color = mView.getContent().getResources().getColor(R.color.colorPrimary);
        SpannableString contentSpannableString = SpannableString.valueOf(weibo.getText());
        SpannableStringUtil.praseName(contentSpannableString);
        SpannableStringUtil.praseHttpUrl(contentSpannableString);
        SpannableStringUtil.praseTopic(contentSpannableString);
        SpannableStringUtil.urlSpan2ClickSpan(contentSpannableString, color);
        SpannableStringUtil.praseEmotions(mView.getContent().getApplicationContext(), contentSpannableString);
        weibo.setContentSpannableString(contentSpannableString);

        if (weibo.getRetweeted_status() != null) {
            Weibo reWeibo = weibo.getRetweeted_status();
            String reUserName = "";
            User reUser = reWeibo.getUser();
            if (reUser != null && !TextUtils.isEmpty(reUser.getScreen_name()))
                reUserName = String.format("@%s :", reUser.getScreen_name());
            SpannableString reContentSpannableString = SpannableString.valueOf(reUserName + reWeibo.getText());
            SpannableStringUtil.praseName(reContentSpannableString);
            SpannableStringUtil.praseHttpUrl(reContentSpannableString);
            SpannableStringUtil.praseTopic(reContentSpannableString);
            SpannableStringUtil.praseEmotions(mView.getContent().getApplicationContext(), reContentSpannableString);
            SpannableStringUtil.urlSpan2ClickSpan(reContentSpannableString, color);
            reWeibo.setContentSpannableString(reContentSpannableString);
        }
    }

    @Override
    public void onLoadMore() {
        long maxId = 0;
        if (mWeibos.size() > 0) {
            maxId = mWeibos.get(mWeibos.size() - 1).getId();
        }
        Subscription subscription = mServerWeiboSource.getFriendWeibo(mToken, 0, maxId, PAGE_COUNT, 1)
                .flatMap(new Func1<List<Weibo>, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(List<Weibo> weibos) {
                        return Observable.from(weibos);
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
                        paraeSpannable(weibo);
                        return weibo;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Weibo>>() {
                    @Override
                    public void onCompleted() {
                        mView.onLoadComplite();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onComnLoadError();
                        mView.onLoadComplite();
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.setFriendWeibo(mWeibos);
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }
}
