package com.caij.emore.present.imp;

import android.os.AsyncTask;

import com.caij.emore.EventTag;
import com.caij.emore.bean.PublishBean;
import com.caij.emore.manager.DraftManager;
import com.caij.emore.database.bean.Draft;
import com.caij.emore.present.DraftPresent;
import com.caij.emore.ui.view.DraftListView;
import com.caij.emore.utils.ExecutorServicePool;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.utils.rxjava.RxUtil;
import com.caij.emore.utils.rxjava.SubscriberAdapter;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/7/20.
 */
public class DraftPresentImp extends AbsBasePresent implements DraftPresent {

    private static final int PAGE_COUNT = 20;

    private List<Draft> mDrafts;
    private DraftListView mView;
    private Observable<Draft> mDraftObservable;

    private DraftManager mDraftManager;

    public DraftPresentImp(DraftManager draftManager, DraftListView view) {
        mDraftManager = draftManager;
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
                .subscribe(new SubscriberAdapter<List<Draft>>() {

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
                .subscribe(new SubscriberAdapter<List<Draft>>() {

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d(DraftPresentImp.this, "Draft load error " + e.getMessage());
                    }

                    @Override
                    public void onNext(List<Draft> drafts) {
                        mDrafts.addAll(drafts);
                        mView.setEntities(mDrafts);

                        mView.onLoadComplete(drafts.size() >= PAGE_COUNT - 1);
                    }
                });

        addSubscription(subscription);

        mDraftObservable = RxBus.getDefault().register(EventTag.EVENT_DRAFT_UPDATE);
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

    private Observable<List<Draft>> createDraftObservable(final long maxTime) {
        return RxUtil.createDataObservable(new RxUtil.Provider<List<Draft>>() {
                @Override
                public List<Draft> getData() {
                    return mDraftManager.getDrafts(maxTime, PAGE_COUNT, 1);
                }
            })
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
        RxBus.getDefault().unregister(EventTag.EVENT_DRAFT_UPDATE, mDraftObservable);
    }

    @Override
    public void publishDraft(Draft draft) {
        switch (draft.getType()) {
            case Draft.TYPE_WEIBO:
                PublishBean publishBean = new PublishBean();
                publishBean.setText(draft.getContent());
                publishBean.setPics(draft.getImages());
                publishBean.setId(draft.getId());
                RxBus.getDefault().post(EventTag.PUBLISH_WEIBO, publishBean);
                break;
        }
    }

    @Override
    public void deleteDraft(final Draft draft, final int position) {
        RxUtil.createDataObservable(new RxUtil.Provider<Object>() {
                @Override
                public Object getData() throws Exception {
                    mDraftManager.deleteDraftById(draft.getId());
                    return null;
                }
            })
            .compose(SchedulerTransformer.create())
            .subscribe(new SubscriberAdapter<Object>() {
                @Override
                public void onNext(Object o) {

                }
            });
    }

}
