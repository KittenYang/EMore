package com.caij.emore.present.imp;

import android.os.AsyncTask;

import com.caij.emore.Event;
import com.caij.emore.bean.PublishBean;
import com.caij.emore.database.bean.Draft;
import com.caij.emore.present.DraftPresent;
import com.caij.emore.ui.view.DraftListView;
import com.caij.emore.source.DraftSource;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.SchedulerTransformer;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/20.
 */
public class DraftPresentImp extends AbsBasePresent implements DraftPresent {

    private static final int PAGE_COUNT = 20;

    private DraftSource mDraftSource;
    private List<Draft> mDrafts;
    private DraftListView mView;
    private Observable<Draft> mDraftObservable;

    public DraftPresentImp(DraftSource draftSource, DraftListView view) {
        mDraftSource = draftSource;
        mDrafts = new ArrayList<>();
        mView = view;
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
        long maxTime = Long.MAX_VALUE;
        if (mDrafts.size() > 0) {
            maxTime = mDrafts.get(mDrafts.size() - 1).getCreate_at();
        }
        Subscription subscription = createDraftObservable(maxTime)
                .subscribe(new Subscriber<List<Draft>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onDefaultLoadError();
                    }

                    @Override
                    public void onNext(List<Draft> drafts) {
                        mDrafts.addAll(drafts);
                        mView.notifyItemRangeInserted(mDrafts, mDrafts.size() - drafts.size(), drafts.size());
                        mView.onLoadComplete(drafts.size() >= PAGE_COUNT - 1);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void onCreate() {
        Subscription subscription = createDraftObservable(Long.MAX_VALUE)
                .subscribe(new Subscriber<List<Draft>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Draft> drafts) {
                        mDrafts.addAll(drafts);
                        mView.setEntities(mDrafts);

                        mView.onLoadComplete(drafts.size() >= PAGE_COUNT - 1);
                    }
                });

        addSubscription(subscription);

        mDraftObservable = RxBus.getDefault().register(Event.EVENT_DRAFT_UPDATE);
        mDraftObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Draft>() {
                    @Override
                    public void call(Draft draft) {
                        if (mDrafts.contains(draft)) {
                            if (draft.getStatus() == Draft.STATUS_SENDING ||
                                    draft.getStatus() == Draft.STATUS_SUCCESS) {
                                mDrafts.remove(draft);
                            }else {
                                for (int i = 0; i <= mDrafts.size(); i ++) {
                                    Draft item = mDrafts.get(i);
                                    if (item.equals(draft)) {
                                        mDrafts.remove(item);
                                        mDrafts.add(i, draft);
                                        break;
                                    }
                                }
                            }
                        }else {
                            if (draft.getStatus() == Draft.STATUS_SAVE ||
                                    draft.getStatus() == Draft.STATUS_FAIL) {
                                mDrafts.add(0, draft);
                            }
                        }

                        mView.onDraftUpdate(draft);
                    }
                });
    }

    private Observable<List<Draft>> createDraftObservable(long maxTime) {
        return mDraftSource.getDrafts(maxTime, PAGE_COUNT, 1)
                .flatMap(new Func1<List<Draft>, Observable<Draft>>() {
                    @Override
                    public Observable<Draft> call(List<Draft> drafts) {
                        return Observable.from(drafts);
                    }
                })
                .filter(new Func1<Draft, Boolean>() {
                    @Override
                    public Boolean call(Draft draft) {
                        return !mDrafts.contains(draft);
                    }
                })
                .doOnNext(new Action1<Draft>() {
                    @Override
                    public void call(Draft draft) {
                        List<String> images  = GsonUtils.fromJson(draft.getImage_paths(), new TypeToken<List<String>>(){}.getType());
                        draft.setImages(images);
                    }
                })
                .toList()
                .compose(new SchedulerTransformer<List<Draft>>());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(Event.EVENT_DRAFT_UPDATE, mDraftObservable);
    }

    @Override
    public void publishDraft(Draft draft) {
        switch (draft.getType()) {
            case Draft.TYPE_WEIBO:
                PublishBean publishBean = new PublishBean();
                publishBean.setText(draft.getContent());
                publishBean.setPics(draft.getImages());
                publishBean.setId(draft.getId());
                RxBus.getDefault().post(Event.PUBLISH_WEIBO, publishBean);
                break;
        }
    }

    @Override
    public void deleteDraft(final Draft draft, final int position) {
        ExecutorServiceUtil.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                mDraftSource.deleteDraft(draft);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
            }
        });
    }

}
