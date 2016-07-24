package com.caij.emore.present.imp;

import com.caij.emore.Key;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.database.bean.UrlInfo;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.RefreshListPresent;
import com.caij.emore.present.view.RefreshListView;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.source.local.LocalUrlSource;
import com.caij.emore.source.server.ServerUrlSource;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.UrlUtil;

import java.util.ArrayList;
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
 * Created by Caij on 2016/7/4.
 */
public class AttitudesToMePresentImp implements RefreshListPresent {

    private static final int COUNT = 20;

    private final CompositeSubscription mLoginCompositeSubscription;
    private String mToken;
    private WeiboSource mWeiboSource;
    private RefreshListView<Attitude> mView;
    private List<Attitude> mAttitudes;

    public AttitudesToMePresentImp(String token, WeiboSource weiboSource, RefreshListView<Attitude> view) {
        mToken = token;
        mWeiboSource = weiboSource;
        mView = view;
        mAttitudes = new ArrayList<>();
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void refresh() {
        Subscription su =  mWeiboSource.getToMeAttiyudes(mToken, 0, 0, Key.WEICO_APP_ID, Key.WEICO_APP_FROM, 1, COUNT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Attitude>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onDefaultLoadError();
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<Attitude> attitudes) {
                        AttitudesToMePresentImp.this.mAttitudes.addAll(attitudes);
                        mView.setEntities(AttitudesToMePresentImp.this.mAttitudes);

                        mView.onRefreshComplete();
                        mView.onLoadComplete(attitudes.size() > COUNT - 1);
                    }
                });
        mLoginCompositeSubscription.add(su);
    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mAttitudes.size() > 0) {
            maxId = mAttitudes.get(mAttitudes.size() - 1).getId();
        }
        Subscription su = mWeiboSource.getToMeAttiyudes(mToken, maxId, 0, Key.WEICO_APP_ID, Key.WEICO_APP_FROM, 1, COUNT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Attitude>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onDefaultLoadError();
                        mView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Attitude> attitudes) {
                        mAttitudes.addAll(attitudes);
                        mView.setEntities(mAttitudes);

                        mView.onLoadComplete(attitudes.size() > COUNT - 1);
                    }
                });
        mLoginCompositeSubscription.add(su);
    }

    @Override
    public void onCreate() {
        refresh();
    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }


}
