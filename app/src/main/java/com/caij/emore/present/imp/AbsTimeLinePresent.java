package com.caij.emore.present.imp;

import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.QueryUrlResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.database.bean.LocakImage;
import com.caij.emore.database.bean.PicUrl;
import com.caij.emore.database.bean.UrlInfo;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboActionPresent;
import com.caij.emore.present.view.WeiboActionView;
import com.caij.emore.source.ImageSouce;
import com.caij.emore.source.UrlSource;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.source.local.LocalImageSource;
import com.caij.emore.source.local.LocalUrlSource;
import com.caij.emore.source.server.ServerImageSource;
import com.caij.emore.source.server.ServerUrlSource;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.weibo.ApiUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/2.
 */
public abstract class AbsTimeLinePresent<V extends WeiboActionView> implements WeiboActionPresent {

    protected V mView;
    protected ImageSouce mServerImageSouce;
    protected ImageSouce mLocalImageSouce;
    protected WeiboSource mServerWeiboSource;
    protected String mToken;
    protected WeiboSource mLocalWeiboSource;
    protected CompositeSubscription mLoginCompositeSubscription;
    protected UrlSource mLocalUrlSource;
    protected UrlSource mServerUrlSource;

    public AbsTimeLinePresent(String token, V view,  WeiboSource serverWeiboSource, WeiboSource localWeiboSource) {
        mView = view;
        mToken = token;
        mLocalImageSouce = new LocalImageSource();
        mServerImageSouce = new ServerImageSource();
        mLocalUrlSource = new LocalUrlSource();
        mServerUrlSource = new ServerUrlSource();
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
        Observable<FavoritesCreateResponse> serverObservable = mServerWeiboSource.collectWeibo(mToken, weibo.getId());
        Observable<FavoritesCreateResponse> localObservable = mLocalWeiboSource.collectWeibo(mToken, weibo.getId());
        Subscription subscription = Observable.concat(serverObservable, localObservable)
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
        Observable<FavoritesCreateResponse> serverObservable = mServerWeiboSource.uncollectWeibo(mToken, weibo.getId());
        Observable<FavoritesCreateResponse> localObservable = mLocalWeiboSource.uncollectWeibo(mToken, weibo.getId());
        Subscription subscription = Observable.concat(serverObservable, localObservable)
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
        AccessToken accessToken = UserPrefs.get().getEMoreToken();
        long uid = Long.parseLong(accessToken.getUid());
        if (uid == weibo.getUser().getId()) {
            mView.showHint(R.string.self_weibo_unable_attitude);
            return;
        }

        mView.showDialogLoading(true, R.string.requesting);
        Map<String, Object> params = new HashMap<>();
        ApiUtil.appendAuthSina(params);
        Observable<Response> serverObservable = mServerWeiboSource.attitudesWeibo(params, "smile", weibo.getId());
        Observable<Response> localObservable =  mLocalWeiboSource.attitudesWeibo(null, null, weibo.getId());
        Subscription subscription = Observable.concat(serverObservable, localObservable)
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
        Observable<Response> serverObservable = mServerWeiboSource.destoryAttitudesWeibo(params, "smile", weibo.getId());
        Observable<Response> localObservable =  mLocalWeiboSource.destoryAttitudesWeibo(null, null, weibo.getId());

        Subscription subscription = Observable.concat(serverObservable, localObservable)
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

    protected void doSpanNext(List<Weibo> weibos) {
        List<String> shortUrls  = SpannableStringUtil.getWeiboTextHttpUrl(weibos);
        Map<String, UrlInfo> shortLongLinkMap = getShortUrlInfos(shortUrls);
        for (Weibo weibo : weibos) {
            SpannableStringUtil.paraeSpannable(weibo, shortLongLinkMap);
        }
    }

    protected void doSpanNext(Weibo weibo) {
        List<String> shortUrls  = SpannableStringUtil.getWeiboTextHttpUrl(weibo, null);
        Map<String, UrlInfo> shortLongLinkMap = getShortUrlInfos(shortUrls);
        SpannableStringUtil.paraeSpannable(weibo, shortLongLinkMap);
    }

    private Map<String, UrlInfo> getShortUrlInfos(List<String> shortUrls){
        Map<String, UrlInfo> shortLongLinkMap = new HashMap<String, UrlInfo>();
        if (shortUrls.size() > 0) {
            int size = shortUrls.size() / 20 + 1;
            List<String> params = new ArrayList<String>(20);
            for (int i = 0; i < size; i ++) {
                params.clear();
                for (int j = i * 20; j < Math.min(shortUrls.size(), (i + 1) * 20); j ++) {
                    String shortUrl  = shortUrls.get(j);
                    if (UrlInfo.isShortUrl(shortUrl)) {
                        UrlInfo urlInfo = mLocalUrlSource.getShortUrlInfo(mToken, shortUrl);
                        if (urlInfo != null) {
                            shortLongLinkMap.put(urlInfo.getShortUrl(), urlInfo);
                        } else {
                            params.add(shortUrl);
                        }
                    }
                }
                if (params.size() > 0) {
                    try {
                        QueryUrlResponse queryUrlResponse = mServerUrlSource.getShortUrlInfo(mToken, params);
                        if (queryUrlResponse != null) {
                            for (Object obj : queryUrlResponse.getUrls()) {
                                UrlInfo shortLongLink = new UrlInfo(new JSONObject(GsonUtils.toJson(obj)));
                                mLocalUrlSource.saveUrlInfo(shortLongLink);
                                shortLongLinkMap.put(shortLongLink.getShortUrl(), shortLongLink);
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }
        return shortLongLinkMap;
    }

}
