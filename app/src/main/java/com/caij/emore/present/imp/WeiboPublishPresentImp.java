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

    private WeiboPublishView mWeiboPublishView;
    private Account mAccount;

    public WeiboPublishPresentImp(Account account,WeiboPublishView weiboPublishView) {
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

        PublishBean publishBean = new PublishBean();
        publishBean.setText(content);
        publishBean.setPics(imagePaths);
        EventUtil.publishWeibo(publishBean);
        mWeiboPublishView.finish();
    }

}
