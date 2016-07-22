package com.caij.emore.present.imp;

import android.os.AsyncTask;

import com.caij.emore.Key;
import com.caij.emore.bean.PublishBean;
import com.caij.emore.database.bean.Draft;
import com.caij.emore.present.DraftPresent;
import com.caij.emore.present.view.BaseListView;
import com.caij.emore.present.view.DraftListView;
import com.caij.emore.source.DraftSource;
import com.caij.emore.utils.EventUtil;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.rxbus.RxBus;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Caij on 2016/7/20.
 */
public class DraftPresentImp implements DraftPresent {

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
        mDraftSource.getDrafts(maxTime, 20, 1)
                .flatMap(new Func1<List<Draft>, Observable<Draft>>() {
                    @Override
                    public Observable<Draft> call(List<Draft> drafts) {
                        return Observable.from(drafts);
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
                        mView.setEntities(mDrafts);
                    }
                });
    }

    @Override
    public void onCreate() {
        mDraftSource.getDrafts(Long.MAX_VALUE, 20, 1)
                .flatMap(new Func1<List<Draft>, Observable<Draft>>() {
                    @Override
                    public Observable<Draft> call(List<Draft> drafts) {
                        return Observable.from(drafts);
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
                    }
                });

        mDraftObservable = RxBus.get().register(Key.EVENT_DRAFT_UPDATE);
        mDraftObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Draft>() {
                    @Override
                    public void call(Draft draft) {
                        if (mDrafts.contains(draft)) {
                            if (draft.getStatus() == Draft.STATUS_SENDING ||
                                    draft.getStatus() == Draft.STATUS_SUCCESS) {
                                mDrafts.remove(draft);
                            }else {
                                for (Draft item : mDrafts) {
                                    item.setCreate_at(draft.getCreate_at());
                                    item.setImage_paths(draft.getImage_paths());
                                    item.setContent(draft.getContent());
                                    item.setStatus(draft.getStatus());
                                    item.setImages(draft.getImages());
                                    item.setType(draft.getType());
                                    break;
                                }
                            }
                        }else {
                            mDrafts.add(0, draft);
                        }

                        mView.onDraftUpdate(draft);
                    }
                });
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void publishDraft(Draft draft) {
        switch (draft.getType()) {
            case Draft.TYPE_WEIBO:
                PublishBean publishBean = new PublishBean();
                publishBean.setText(draft.getContent());
                publishBean.setPics(draft.getImages());
                EventUtil.publishWeibo(publishBean);
                break;
        }
        deleteDraft(draft);
    }

    @Override
    public void deleteDraft(final Draft draft) {
        ExecutorServiceUtil.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                mDraftSource.deleteDraft(draft);
                return null;
            }
        });
    }

}
