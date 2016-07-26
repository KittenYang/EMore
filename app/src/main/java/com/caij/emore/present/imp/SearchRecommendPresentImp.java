package com.caij.emore.present.imp;

import com.caij.emore.bean.SinaSearchRecommend;
import com.caij.emore.present.ListPresent;
import com.caij.emore.present.SearchRecommendPresent;
import com.caij.emore.present.view.SearchRecommendView;
import com.caij.emore.source.SearchSource;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/26.
 */
public class SearchRecommendPresentImp implements SearchRecommendPresent {

    SearchSource mServerSearchSource;
    protected CompositeSubscription mCompositeSubscription;
    private SearchRecommendView mSearchRecommendView;

    public SearchRecommendPresentImp(SearchSource serverSearchSource, SearchRecommendView view) {
        mServerSearchSource = serverSearchSource;
        mCompositeSubscription = new CompositeSubscription();
        mSearchRecommendView = view;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mCompositeSubscription.clear();
    }

    @Override
    public void search(String key) {
        mCompositeSubscription.clear();
        Subscription subscription = mServerSearchSource.getSearchRecommend(key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
