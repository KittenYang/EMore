package com.caij.emore.present.imp;


import com.caij.emore.R;
import com.caij.emore.bean.Account;
import com.caij.emore.bean.PublishBean;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboPublishPresent;
import com.caij.emore.present.view.WeiboPublishView;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.EventUtil;

import java.util.ArrayList;

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
    private WeiboSource mServerPublishWeiboSource;

    private WeiboPublishView mWeiboPublishView;
    private Account mAccount;

    public WeiboPublishPresentImp(Account account,
                                  WeiboSource serverPublishWeiboSource, WeiboPublishView weiboPublishView) {
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
                return;
            }
        }

        if (imagePaths != null && imagePaths.size() > 0) {
            PublishBean publishBean = new PublishBean();
            publishBean.setText(content);
            publishBean.setPics(imagePaths);
            EventUtil.publishWeibo(publishBean);
            mWeiboPublishView.finish();
            return;
        }

        mWeiboPublishView.showDialogLoading(true, R.string.publish_loading);
        Observable<Weibo> publishWeiboObservable = mServerPublishWeiboSource.
                    publishWeiboOfText(mAccount.getWeiyoToken().getAccess_token(), content);
        Subscription subscription = publishWeiboObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weibo>() {
                    @Override
                    public void onCompleted() {
                        mWeiboPublishView.showDialogLoading(false, R.string.publish_loading);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mWeiboPublishView.onDefaultLoadError();
                        mWeiboPublishView.showDialogLoading(false, R.string.publish_loading);
                    }

                    @Override
                    public void onNext(Weibo weibo) {
                        mWeiboPublishView.onPublishSuccess(weibo);
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

}
