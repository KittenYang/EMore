package com.caij.weiyo.present.imp;

import android.text.TextUtils;

import com.caij.weiyo.Key;
import com.caij.weiyo.bean.Account;
import com.caij.weiyo.bean.PublishBean;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.present.WeiboPublishPresent;
import com.caij.weiyo.present.view.WeiboPublishView;
import com.caij.weiyo.source.PublishWeiboSource;
import com.caij.weiyo.utils.ExecutorServiceUtil;
import com.caij.weiyo.utils.ImageUtil;
import com.caij.weiyo.utils.LogUtil;
import com.caij.weiyo.utils.PublishWeiboUtil;
import com.caij.weiyo.utils.okhttp.OkHttpClientProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/24.
 */
public class WeiboPublishPresentImp implements WeiboPublishPresent {

    private final CompositeSubscription mLoginCompositeSubscription;
    private PublishWeiboSource mServerPublishWeiboSource;

    private WeiboPublishView mWeiboPublishView;
    private Account mAccount;

    public WeiboPublishPresentImp(Account account,
                                  PublishWeiboSource serverPublishWeiboSource, WeiboPublishView weiboPublishView) {
        mServerPublishWeiboSource = serverPublishWeiboSource;
        mAccount = account;
        mWeiboPublishView = weiboPublishView;
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }

    @Override
    public void publishWeibo(String content, ArrayList<String> imagePaths) {
        if (imagePaths != null && imagePaths.size() > 1) {
            if (mAccount.getWeicoToken() == null || mAccount.getWeicoToken().isExpired()) {
                mWeiboPublishView.toAuthWeico();
            }else {
                PublishBean publishBean = new PublishBean();
                publishBean.setText(content);
                publishBean.setPics(imagePaths);
                PublishWeiboUtil.publishWeibo(publishBean, mWeiboPublishView.getContent());
                mWeiboPublishView.finish();
            }
            return;
        }

        mWeiboPublishView.showPublishLoading(true);
        Observable<Weibo> publishWeiboObservable;
        if (imagePaths == null || imagePaths.size() == 0) {
            publishWeiboObservable = mServerPublishWeiboSource.
                    publishWeiboOfText(mAccount.getWeiyoToken().getAccess_token(), content);
        }else {
            publishWeiboObservable = mServerPublishWeiboSource.
                    publishWeiboOfOneImage(mAccount.getWeiyoToken().getAccess_token(), content,
                    imagePaths.get(0));
        }
        Subscription subscription = publishWeiboObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weibo>() {
                    @Override
                    public void onCompleted() {
                        mWeiboPublishView.showPublishLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mWeiboPublishView.onComnLoadError();
                        mWeiboPublishView.showPublishLoading(false);
                    }

                    @Override
                    public void onNext(Weibo weibo) {
                        mWeiboPublishView.showPublishLoading(false);
                        mWeiboPublishView.onPublishSuccess(weibo);
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

}
