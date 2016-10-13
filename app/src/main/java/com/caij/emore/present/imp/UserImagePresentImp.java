package com.caij.emore.present.imp;

import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.bean.StatusImageInfo;
import com.caij.emore.bean.response.UserWeiboResponse;
import com.caij.emore.database.bean.Status;
import com.caij.emore.present.UserStatusPresent;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.TimeLineStatusImageView;
import com.caij.emore.api.ex.ResponseSubscriber;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Caij on 2016/5/31.
 */
public class UserImagePresentImp extends AbsBasePresent implements UserStatusPresent {

    private final static int PAGE_COUNT = 20;

    private TimeLineStatusImageView mView;
    private StatusApi mStatusApi;
    private List<Status> mStatuses;
    private long mUid;
    private List<StatusImageInfo> mPicUrl;

    public UserImagePresentImp(long uid, TimeLineStatusImageView view, StatusApi statusApi) {
        mView = view;
        mUid = uid;
        mStatusApi = statusApi;
        mStatuses = new ArrayList<>();
        mPicUrl = new ArrayList<>();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void deleteStatus(Status status, int position) {

    }

    @Override
    public void collectStatus(Status status) {

    }

    @Override
    public void unCollectStatus(Status status) {

    }

    @Override
    public void attitudeStatus(Status status) {

    }

    @Override
    public void destroyAttitudeStatus(Status status) {

    }

    @Override
    public void filter(int feature) {

    }

    @Override
    public void refresh() {
        Subscription subscription = mStatusApi.getUseWeibo(mUid, 2, 0, 0, PAGE_COUNT, 1)
                .flatMap(new Func1<UserWeiboResponse, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(UserWeiboResponse response) {
                        return Observable.from(response.getStatuses());
                    }
                })
                .doOnNext(new Action1<Status>() {
                    @Override
                    public void call(Status weibo) {
                        // TODO: 2016/8/25 图片加载
                        mPicUrl.clear();
                        for (String picId : weibo.getPic_ids()) {
                            mPicUrl.add(weibo.getPic_infos().get(picId));
                        }
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResponseSubscriber<List<Status>>(mView) {

                    @Override
                    protected void onFail(Throwable e) {
                    }

                    @Override
                    public void onNext(List<Status> statuses) {
                        mStatuses.clear();
                        mStatuses.addAll(statuses);
                        mView.setEntities(mPicUrl);

                        mView.onLoadComplete(statuses.size() >= PAGE_COUNT);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void userFirstVisible() {
        refresh();
    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mStatuses.size() > 0) {
            maxId = mStatuses.get(mStatuses.size() - 1).getId();
        }
        Subscription subscription = mStatusApi.getUseWeibo(mUid, 2, 0, maxId, PAGE_COUNT, 1)
                .flatMap(new Func1<UserWeiboResponse, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(UserWeiboResponse response) {
                        return Observable.from(response.getStatuses());
                    }
                })
                .filter(new Func1<Status, Boolean>() {
                    @Override
                    public Boolean call(Status weibo) {
                        return !mStatuses.contains(weibo);
                    }
                })
                .doOnNext(new Action1<Status>() {
                    @Override
                    public void call(Status weibo) {
                        for (String picId : weibo.getPic_ids()) {
                            mPicUrl.add(weibo.getPic_infos().get(picId));
                        }
                    }
                })
                .toList()
                .compose(SchedulerTransformer.<List<Status>>create())
                .subscribe(new ResponseSubscriber<List<Status>>(mView) {

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Status> statuses) {
                        mStatuses.addAll(statuses);
                        mView.setEntities(mPicUrl);
                        mView.onLoadComplete(statuses.size() >= PAGE_COUNT - 1); //这里有一条重复的 所以需要-1
                    }
                });
        addSubscription(subscription);
    }
}
