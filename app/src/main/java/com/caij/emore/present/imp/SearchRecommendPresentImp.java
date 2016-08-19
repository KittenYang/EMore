package com.caij.emore.present.imp;

import com.caij.emore.bean.SinaSearchRecommend;
import com.caij.emore.present.SearchRecommendPresent;
import com.caij.emore.ui.view.SearchRecommendView;
import com.caij.emore.source.SearchSource;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/26.
 */
public class SearchRecommendPresentImp extends AbsBasePresent implements SearchRecommendPresent {

    SearchSource mServerSearchSource;
    private SearchRecommendView mSearchRecommendView;
    private CompositeSubscription mCompositeSubscription;

    public SearchRecommendPresentImp(SearchSource serverSearchSource, SearchRecommendView view) {
        mServerSearchSource = serverSearchSource;
        mSearchRecommendView = view;
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.clear();
    }

    @Override
    public void search(String key) {
        mCompositeSubscription.clear();
        Subscription subscription = mServerSearchSource.getSearchRecommend(key)
                .compose(new SchedulerTransformer<SinaSearchRecommend>())
                .subscribe(new Subscriber<SinaSearchRecommend>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(SinaSearchRecommend sinaSearchRecommend) {
                        mSearchRecommendView.onSearchSuccess(sinaSearchRecommend.getData());
                    }
                });
        mCompositeSubscription.add(subscription);
    }
}
