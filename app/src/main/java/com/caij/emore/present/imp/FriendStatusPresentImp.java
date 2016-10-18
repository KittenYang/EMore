package com.caij.emore.present.imp;

import android.text.TextUtils;

import com.caij.emore.EMApplication;
import com.caij.emore.EventTag;
import com.caij.emore.Key;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.bean.response.QueryStatusResponse;
import com.caij.emore.manager.NotifyManager;
import com.caij.emore.manager.StatusManager;
import com.caij.emore.database.bean.Status;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.present.FriendStatusPresent;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.FriendStatusView;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SPUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.utils.rxjava.RxUtil;
import com.caij.emore.utils.rxjava.SubscriberAdapter;
import com.caij.emore.utils.weibo.MessageUtil;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/5/31.
 */
public class FriendStatusPresentImp extends AbsListTimeLinePresent<FriendStatusView> implements FriendStatusPresent {

    private final static int PAGE_COUNT = 20;

    private final static long STATUS_REFRESH_INTERVAL = 60 * 60 * 1000;

    private NotifyManager mNotifyManager;

    private Observable<Status> mPublishStatusObservable;

    private long mUid;

    private long mNextCursor;

    public FriendStatusPresentImp(long uid, FriendStatusView view, StatusApi statusApi,
                                  StatusManager statusManager, AttitudeApi attitudeApi,
                                  NotifyManager notifyManager) {
        super(view, statusApi, statusManager, attitudeApi);
        mNotifyManager = notifyManager;
        mUid = uid;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerEvent();
        getLocalFriendStatus();
    }

    private void registerEvent() {
        mPublishStatusObservable = RxBus.getDefault().register(EventTag.EVENT_PUBLISH_WEIBO_SUCCESS);
        mPublishStatusObservable.compose(StatusContentSpannableConvertTransformer.create(false))
                    .compose(SchedulerTransformer.<Status>create())
                    .subscribe(new Action1<Status>() {
                        @Override
                        public void call(Status status) {
                            mView.onStatusPublishSuccess(status);
                        }
                    });
    }

    private void getLocalFriendStatus() {
        Subscription subscription = RxUtil.createDataObservable(new RxUtil.Provider<List<Status>>() {
            @Override
            public List<Status> getData() {
                return getCacheStatus();
            }
        }).flatMap(new Func1<List<Status>, Observable<Status>>() {
            @Override
            public Observable<Status> call(List<Status> statuses) {
                return Observable.from(statuses);
            }
        })
        .compose(StatusContentSpannableConvertTransformer.create(false))
        .toList()
        .compose(SchedulerTransformer.<List<Status>>create())
        .subscribe(new SubscriberAdapter<List<Status>>() {

            @Override
            public void onError(Throwable e) {
                mView.toRefresh();
            }

            @Override
            public void onNext(List<Status> statuses) {
               onGetLocalStatusSuccess(statuses);
            }
        });
        addSubscription(subscription);
    }

    private List<Status> getCacheStatus() {
        String ids = new SPUtil.SPBuilder(EMApplication.getInstance())
                .openDefault()
                .getString(Key.FRIEND_STATUS_LOCAL_CACHE_IDS, "");
        if (!TextUtils.isEmpty(ids)) {
            List<Long> idsl = GsonUtils.fromJson(ids, new TypeToken<List<Long>>(){}.getType());
            List<Status> statuses = new ArrayList<Status>();
            for (Long id : idsl) {
                Status status = mStatusManager.getStatusById(id);
                if (status != null) {
                    statuses.add(status);
                }
            }
            return statuses;
        }
        return null;
    }

    private void onGetLocalStatusSuccess(List<Status> statuses) {
        mStatuses.addAll(statuses);
        mView.setEntities(mStatuses);

        mNextCursor = new SPUtil.SPBuilder(EMApplication.getInstance())
                .openDefault()
                .getLong(Key.FRIEND_STATUS_LOCAL_NEXT_CURSOR, 0);
        LogUtil.d(this, "local next cursor %s", mNextCursor);

        long preRefreshTime  = new SPUtil.SPBuilder(EMApplication.getInstance())
                .openDefault()
                .getLong(Key.FRIEND_STATUS_UPDATE_TIME, -1);
        if (System.currentTimeMillis() - preRefreshTime > STATUS_REFRESH_INTERVAL
                || statuses.size() <= 0) {
            mView.toRefresh();
        }

        if (statuses.size() >= PAGE_COUNT){
            mView.onLoadComplete(true);
        }
    }

    @Override
    public void refresh() {
        Subscription subscription = getFriendStatusesObservable(0, true)
                .doOnNext(new Action1<List<Status>>() {
                    @Override
                    public void call(List<Status> statuses) {
                        saveStatusRefreshTimeAndNextCursor();

                        saveCacheStatusId(statuses);
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mView.onRefreshComplete();
                    }
                })
                .subscribe(new ResponseSubscriber<List<Status>>(mView) {

                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Status> statuses) {
                        mStatuses.clear();
                        mStatuses.addAll(statuses);
                        mView.setEntities(mStatuses);

                        mView.onLoadComplete(mNextCursor > 0);

                        MessageUtil.resetLocalUnReadMessage(UnReadMessage.TYPE_STATUS, 0, mUid, mNotifyManager);
                    }
                });
        addSubscription(subscription);
    }

    /**
     * 出现广告微博的时候导致排序就不太对， 所以采用这种方法缓存微博
     * @param statuses
     */
    private void saveCacheStatusId(List<Status> statuses) {
        List<Long> ids = new ArrayList<>();
        for (Status status : statuses) {
            ids.add(status.getId());
        }
        new SPUtil.SPBuilder(EMApplication.getInstance())
                .openDefault().edit()
                .putString(Key.FRIEND_STATUS_LOCAL_CACHE_IDS, GsonUtils.toJson(ids))
                .apply();
    }

    private void saveStatusRefreshTimeAndNextCursor() {
        new SPUtil.SPBuilder(EMApplication.getInstance())
                .openDefault().edit()
                .putLong(Key.FRIEND_STATUS_UPDATE_TIME, System.currentTimeMillis())
                .putLong(Key.FRIEND_STATUS_LOCAL_NEXT_CURSOR, mNextCursor)
                .apply();
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
        Subscription subscription = getFriendStatusesObservable(mNextCursor, false)
            .subscribe(new ResponseSubscriber<List<Status>>(mView) {

                @Override
                protected void onFail(Throwable e) {
                    mView.onLoadComplete(true);
                }

                @Override
                public void onNext(List<Status> statuses) {
                    mStatuses.addAll(statuses);
                    mView.notifyItemRangeInserted(mStatuses, mStatuses.size() - statuses.size(), statuses.size());
                    mView.onLoadComplete(mNextCursor > 0);
                }
            });
        addSubscription(subscription);
    }

    private Observable<List<Status>> getFriendStatusesObservable(long maxId, final boolean isRefresh) {
        return mStatusApi.getFriendWeibo(mUid, 0, maxId, PAGE_COUNT, 1)
                .compose(ErrorCheckerTransformer.<QueryStatusResponse>create())
                .flatMap(new Func1<QueryStatusResponse, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(QueryStatusResponse response) {
                        mNextCursor = response.getNext_cursor();
                        processAds(response);
                        return Observable.from(response.getStatuses());
                    }
                })
                .filter(new Func1<Status, Boolean>() {
                    @Override
                    public Boolean call(Status status) {
                        return isRefresh || !mStatuses.contains(status);
                    }
                })
                .compose(StatusContentSpannableConvertTransformer.create(false))
                .doOnNext(new Action1<Status>() {
                    @Override
                    public void call(Status status) {
                        mStatusManager.saveStatus(status);
                    }
                })
                .toList()
                .compose(SchedulerTransformer.<List<Status>>create());
    }

    /**
     * 在返回的微博中 广告用户的关注是true  。。。所以把广告微博用户关注设置为false
     * @param response
     */
    private void processAds(QueryStatusResponse response) {
        if (response != null && response.getStatuses() != null && response.getAdvertises() != null
                && response.getStatuses().size() > 0 && response.getAdvertises().size() > 0) {
            for (Status status : response.getStatuses()) {
                if (response.getAdvertises().contains(String.valueOf(status.getId()))) {
                    status.getUser().setFollowing(false);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(EventTag.EVENT_PUBLISH_WEIBO_SUCCESS, mPublishStatusObservable);
    }

}
