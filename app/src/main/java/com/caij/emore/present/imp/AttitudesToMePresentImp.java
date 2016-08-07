package com.caij.emore.present.imp;

import com.caij.emore.Key;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.response.QueryWeiboAttitudeResponse;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.bean.UrlInfo;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.RefreshListPresent;
import com.caij.emore.present.view.RefreshListView;
import com.caij.emore.source.MessageSource;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.source.local.LocalUrlSource;
import com.caij.emore.source.server.ServerUrlSource;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.UrlUtil;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.DefaultTransformer;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;
import com.caij.emore.utils.weibo.MessageUtil;

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
    MessageSource mServerMessageSource;
    MessageSource mLocalMessageSource;

    public AttitudesToMePresentImp(String token, WeiboSource weiboSource,
                                   MessageSource serverMessageSource,
                                   MessageSource localMessageSource,
                                   RefreshListView<Attitude> view) {
        mToken = token;
        mWeiboSource = weiboSource;
        mView = view;
        mServerMessageSource = serverMessageSource;
        mLocalMessageSource = localMessageSource;
        mAttitudes = new ArrayList<>();
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void refresh() {
        Subscription su =  createGetAttitudeObservable(0, true)
                .subscribe(new DefaultResponseSubscriber<List<Attitude>>(mView) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<Attitude> attitudes) {
                        mAttitudes.addAll(attitudes);
                        mView.setEntities(mAttitudes);

                        mView.onRefreshComplete();
                        mView.onLoadComplete(attitudes.size() > COUNT - 1);

                        MessageUtil.resetUnReadMessage(mToken, UnReadMessage.TYPE_ATTITUDE,
                                mServerMessageSource, mLocalMessageSource);
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
        Subscription su = createGetAttitudeObservable(maxId, false)
                .subscribe(new DefaultResponseSubscriber<List<Attitude>>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mView.onLoadComplete(true);
                    }

                    @Override
                    public void onCompleted() {

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

    private Observable<List<Attitude>> createGetAttitudeObservable(long maxId, final boolean isRefresh) {
        return mWeiboSource.getToMeAttiyudes(mToken, maxId, 0, Key.WEICO_APP_ID, Key.WEICO_APP_FROM, 1, COUNT)
                .compose(new ErrorCheckerTransformer<QueryWeiboAttitudeResponse>())
                .flatMap(new Func1<QueryWeiboAttitudeResponse, Observable<Attitude>>() {
                    @Override
                    public Observable<Attitude> call(QueryWeiboAttitudeResponse queryWeiboAttitudeResponse) {
                        return Observable.from(queryWeiboAttitudeResponse.getAttitudes());
                    }
                })
                .filter(new Func1<Attitude, Boolean>() {
                    @Override
                    public Boolean call(Attitude attitude) {
                        return !mAttitudes.contains(attitude) || isRefresh;
                    }
                })
                .toList()
                .compose(new SchedulerTransformer<List<Attitude>>());
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
