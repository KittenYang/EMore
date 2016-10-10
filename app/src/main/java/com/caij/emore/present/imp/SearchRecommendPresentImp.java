package com.caij.emore.present.imp;

import com.caij.emore.bean.SinaSearchRecommend;
import com.caij.emore.present.SearchRecommendPresent;
import com.caij.emore.ui.view.SearchRecommendView;
import com.caij.emore.remote.SearchRecommendSource;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/26.
 */
public class SearchRecommendPresentImp extends AbsBasePresent implements SearchRecommendPresent {

    private SearchRecommendSource mServerSearchRecommendSource;
    private SearchRecommendView mSearchRecommendView;

    public SearchRecommendPresentImp(SearchRecommendSource serverSearchRecommendSource, SearchRecommendView view) {
        mServerSearchRecommendSource = serverSearchRecommendSource;
        mSearchRecommendView = view;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void search(String key) {
        Subscription subscription = mServerSearchRecommendSource.getSearchRecommend(key)
                .compose(new SchedulerTransformer<SinaSearchRecommend>())
                .subscribe(new DefaultResponseSubscriber<SinaSearchRecommend>(mSearchRecommendView) {

                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(SinaSearchRecommend sinaSearchRecommend) {
                        mSearchRecommendView.onSearchSuccess(sinaSearchRecommend.getData());
                    }
                });
        addSubscription(subscription);
    }
}
