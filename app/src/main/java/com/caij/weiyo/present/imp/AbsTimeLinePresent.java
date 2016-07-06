package com.caij.weiyo.present.imp;

import com.caij.weiyo.R;
import com.caij.weiyo.bean.PicUrl;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.bean.response.FavoritesCreateResponse;
import com.caij.weiyo.database.bean.LocakImage;
import com.caij.weiyo.present.TimeLinePresent;
import com.caij.weiyo.present.view.TimeLineWeiboView;
import com.caij.weiyo.source.ImageSouce;
import com.caij.weiyo.source.WeiboSource;
import com.caij.weiyo.source.local.LocalImageSource;
import com.caij.weiyo.source.server.ServerImageSource;
import com.caij.weiyo.utils.LogUtil;

import java.io.IOException;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/2.
 */
public abstract class AbsTimeLinePresent<V extends TimeLineWeiboView> implements TimeLinePresent {

    protected V mView;
    protected ImageSouce mServerImageSouce;
    protected ImageSouce mLocalImageSouce;
    protected WeiboSource mServerWeiboSource;
    protected String mToken;
    protected CompositeSubscription mLoginCompositeSubscription;

    public AbsTimeLinePresent(String token, V view,  WeiboSource serverWeiboSource) {
        mView = view;
        mToken = token;
        mLocalImageSouce = new LocalImageSource();
        mServerImageSouce = new ServerImageSource();
        mServerWeiboSource = serverWeiboSource;
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void deleteWeibo(final Weibo weibo, final int position) {
        mView.showDialogLoading(true, R.string.deleting);
        Subscription subscription = mServerWeiboSource.deleteWeibo(mToken, weibo.getId())
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
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void collectWeibo(final Weibo weibo) {
        mView.showDialogLoading(true, R.string.collecting);
        Subscription subscription = mServerWeiboSource.collectWeibo(mToken, weibo.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FavoritesCreateResponse>() {
                    @Override
                    public void onCompleted() {
                        mView.onCollectSuccess(weibo);
                        mView.showDialogLoading(false, R.string.collecting);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onDefaultLoadError();
                        mView.showDialogLoading(false, R.string.collecting);
                    }

                    @Override
                    public void onNext(FavoritesCreateResponse weibo) {

                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void uncollectWeibo(final Weibo weibo) {
        mView.showDialogLoading(true, R.string.uncollecting);
        Subscription subscription = mServerWeiboSource.uncollectWeibo(mToken, weibo.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FavoritesCreateResponse>() {
                    @Override
                    public void onCompleted() {
                        mView.onUncollectSuccess(weibo);
                        mView.showDialogLoading(false, R.string.uncollecting);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onDefaultLoadError();
                        mView.showDialogLoading(false, R.string.uncollecting);
                    }

                    @Override
                    public void onNext(FavoritesCreateResponse weibo) {

                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    protected void toGetImageSize(Weibo weibo) {
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
}
