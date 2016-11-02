package com.caij.emore.present.imp;

import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.bean.response.FriendshipResponse;
import com.caij.emore.bean.response.QueryStatusResponse;
import com.caij.emore.manager.StatusManager;
import com.caij.emore.database.bean.Status;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.StatusAndUserSearchPresent;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.remote.UserApi;
import com.caij.emore.ui.view.StatusAndUserSearchView;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.SchedulerTransformer;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by Caij on 2016/7/26.
 */
public class StatusAndUserSearchPresentImp extends AbsListTimeLinePresent<StatusAndUserSearchView> implements StatusAndUserSearchPresent {

    public static final int PAGE_COUNT = 20;

    private String mKey;
    private int mPage;

    private UserApi mUserApi;

    public StatusAndUserSearchPresentImp(String key, StatusAndUserSearchView view, StatusApi statusApi,
                                         StatusManager statusManager, AttitudeApi attitudeApi,
                                         UserApi userApi) {
        super(view, statusApi, statusManager, attitudeApi);
        mKey = key;
        mUserApi = userApi;
    }

    @Override
    public void refresh() {
        Observable<List<Status>> getStatusesObservable = createObservable(1, true);

        Observable<List<User>> userObservable = mUserApi.searchUser(mKey, 1, PAGE_COUNT).flatMap(new Func1<FriendshipResponse, Observable<List<User>>>() {
            @Override
            public Observable<List<User>> call(FriendshipResponse friendshipResponse) {
                return Observable.just(friendshipResponse.getUsers());
            }
        });

        Subscription subscription = Observable.zip(getStatusesObservable, userObservable, new Func2<List<Status>, List<User>, Zip>() {
            @Override
            public Zip call(List<Status> weibos, List<User> users) {
                        Zip zip = new Zip();
                        zip.statuses = weibos;
                        zip.users = users;
                        return zip;
                    }
                })
                .compose(new SchedulerTransformer<Zip>())
                .subscribe(new ResponseSubscriber<Zip>(mView) {

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(Zip zip) {
                        List<Status> statuses = zip.statuses;
                        mStatuses.addAll(statuses);
                        mView.setEntities(statuses);
                        mView.onRefreshComplete();

                        mView.setUsers(zip.users);

                        if (statuses.size() > PAGE_COUNT - 3) {
                            mView.onLoadComplete(true);
                        }else {
                            mView.onLoadComplete(false);
                        }

                        mPage = 2;
                    }
                });

        addSubscription(subscription);
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
        Subscription subscription = createObservable(mPage, false)
                .subscribe(new ResponseSubscriber<List<Status>>(mView) {

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Status> statuses) {
                        mStatuses.addAll(statuses);
                        mView.notifyItemRangeInserted(mStatuses, mStatuses.size() - statuses.size(), statuses.size());

                        if (statuses.size() > PAGE_COUNT - 3) {
                            mView.onLoadComplete(true);
                        }else {
                            mView.onLoadComplete(false);
                        }

                        mPage ++;
                    }
                });
        addSubscription(subscription);
    }

    private Observable<List<Status>> createObservable(int page, final boolean isRefresh) {
        return mStatusApi.getSearchStatus(mKey, page, PAGE_COUNT)
                .compose(new ErrorCheckerTransformer<QueryStatusResponse>())
                .flatMap(new Func1<QueryStatusResponse, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(QueryStatusResponse response) {
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
                .toList()
                .compose(SchedulerTransformer.<List<Status>>create());
    }

    private static class Zip{
        public List<Status> statuses;
        public List<User> users;
    }
}
