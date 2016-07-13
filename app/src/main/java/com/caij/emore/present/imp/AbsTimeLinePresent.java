package com.caij.emore.present.imp;

import com.caij.emore.R;
import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.database.bean.LocakImage;
import com.caij.emore.database.bean.PicUrl;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.TimeLinePresent;
import com.caij.emore.present.view.TimeLineWeiboView;
import com.caij.emore.source.ImageSouce;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.source.local.LocalImageSource;
import com.caij.emore.source.server.ServerImageSource;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.weibo.ApiUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
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
    protected WeiboSource mLocalWeiboSource;
    protected CompositeSubscription mLoginCompositeSubscription;

    public AbsTimeLinePresent(String token, V view,  WeiboSource serverWeiboSource, WeiboSource localWeiboSource) {
        mView = view;
        mToken = token;
        mLocalImageSouce = new LocalImageSource();
        mServerImageSouce = new ServerImageSource();
        mServerWeiboSource = serverWeiboSource;
        mLocalWeiboSource = localWeiboSource;
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void deleteWeibo(final Weibo weibo, final int position) {
        mView.showDialogLoading(true, R.string.deleting);
        Observable<Weibo> serverObservable = mServerWeiboSource.deleteWeibo(mToken, weibo.getId());
        Observable<Weibo> localObservable = mLocalWeiboSource.deleteWeibo(mToken, weibo.getId());
        Subscription subscription = Observable.concat(serverObservable, localObservable)
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
            } catch (IOException e) {
                LogUtil.d(this, "%s 图片尺寸获取失败", picUrl.getThumbnail_pic());
            }
        }
    }

    @Override
    public void attitudesWeibo(final Weibo weibo) {
        mView.showDialogLoading(true, R.string.requesting);
        Map<String, Object> params = new HashMap<>();
        ApiUtil.appendAuthSina(params);
        Subscription subscription = mServerWeiboSource.attitudesWeibo(params, "smile", weibo.getId())
                .doOnNext(new Action1<Response>() {
                    @Override
                    public void call(Response s) {
                        mLocalWeiboSource.attitudesWeibo(null, null, weibo.getId());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {
                        mView.showDialogLoading(false, R.string.requesting);
                        mView.onAttitudesSuccess(weibo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showDialogLoading(false, R.string.requesting);
                    }

                    @Override
                    public void onNext(Response s) {
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void destoryAttitudesWeibo(final Weibo weibo) {
        mView.showDialogLoading(true, R.string.requesting);
        Map<String, Object> params = new HashMap<>();
        ApiUtil.appendAuthSina(params);
        Subscription subscription = mServerWeiboSource.destoryAttitudesWeibo(params, "smile", weibo.getId())
                .doOnNext(new Action1<Response>() {
                    @Override
                    public void call(Response s) {
                        mLocalWeiboSource.destoryAttitudesWeibo(null, null, weibo.getId());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {
                        mView.showDialogLoading(false, R.string.requesting);
                        mView.onAttitudesSuccess(weibo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showDialogLoading(false, R.string.requesting);
                    }

                    @Override
                    public void onNext(Response s) {
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    public class WeiboTransformer implements Observable.Transformer<Weibo, Weibo> {

        @Override
        public Observable<Weibo> call(Observable<Weibo> weiboObservable) {
            return weiboObservable
                    .map(new Func1<Weibo, Weibo>() {

                        @Override
                        public Weibo call(Weibo weibo) {
                            toGetImageSize(weibo);
                            weibo.setAttitudes(mLocalWeiboSource.getAttitudes(weibo.getId()));
                            SpannableStringUtil.paraeSpannable(weibo);
                            return weibo;
                        }
                    });
        }
    }

}
