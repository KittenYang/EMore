package com.caij.emore.present.imp;

import android.os.AsyncTask;

import com.caij.emore.EventTag;
import com.caij.emore.bean.PublishBean;
import com.caij.emore.manager.DraftManager;
import com.caij.emore.manager.StatusManager;
import com.caij.emore.manager.StatusUploadImageManager;
import com.caij.emore.database.bean.Draft;
import com.caij.emore.database.bean.Status;
import com.caij.emore.database.bean.UploadImageResponse;
import com.caij.emore.present.PublishWeiboManagerPresent;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.PublishServiceView;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.api.ex.ErrorCheckerTransformer;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Caij on 2016/7/19.
 */
public class PublishStatusManagerPresentImp extends AbsBasePresent implements PublishWeiboManagerPresent {

    private Observable<PublishBean> mPublishStatusObservable;
    private DraftManager mDraftManager;
    StatusApi mStatusApi;
    private StatusManager mStatusManager;
    private PublishServiceView mPublishServiceView;
    private StatusUploadImageManager mStatusUploadImageManager;

    public PublishStatusManagerPresentImp(StatusApi statusApi,
                                          StatusManager statusManager,
                                          DraftManager draftManager,
                                          StatusUploadImageManager statusUploadImageManager,
                                          PublishServiceView view) {
        mStatusApi = statusApi;
        mStatusManager = statusManager;
        mDraftManager = draftManager;
        mStatusUploadImageManager = statusUploadImageManager;
        mPublishServiceView = view;
    }

    @Override
    public void publishStatus(final PublishBean publishBean) {
        ExecutorServiceUtil.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
//                mDraftSource.deleteDraftById(publishBean.getId());
                saveOrUpdate2Draft(publishBean, Draft.STATUS_SENDING);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (publishBean.getPics() == null || publishBean.getPics().size() == 0) {
                    publishText(publishBean);
                }else if (publishBean.getPics().size() == 1) {
                    publishStatusOneImage(publishBean);
                } else {
                    publishStatusMuImage(publishBean);
                }
            }
        });
    }

    private void publishStatusMuImage(final PublishBean publishBean) {
        mPublishServiceView.onPublishStart(publishBean);
//        final Account account = UserPrefs.getDefault().getAccount();
        Subscription subscription = Observable.create(new Observable.OnSubscribe<List<String>>() {
                    @Override
                    public void call(Subscriber<? super List<String>> subscriber) {
                        try {
                            List<String> outPaths = new ArrayList<String>();
                            for (String source : publishBean.getPics()) {
                                outPaths.add(ImageUtil.compressImage(source,
                                        mPublishServiceView.getContent().getApplicationContext()));
                            }
                            subscriber.onNext(outPaths);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .flatMap(new Func1<List<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<String> strings) {
                        return Observable.from(strings);
                    }
                })
                .flatMap(new Func1<String, Observable<UploadImageResponse>>() {
                    @Override
                    public Observable<UploadImageResponse> call(final String imagePath) {
                            Observable<UploadImageResponse> serverObservable = mStatusApi.
                                    uploadWeiboOfOneImage(imagePath)
                                    .doOnNext(new Action1<UploadImageResponse>() {
                                        @Override
                                        public void call(UploadImageResponse uploadImageResponse) {
                                            uploadImageResponse.setImagePath(imagePath);
                                            mStatusUploadImageManager.insert(uploadImageResponse);
                                        }
                                    });
                            Observable<UploadImageResponse> localObservable = Observable.create(new Observable.OnSubscribe<UploadImageResponse>() {
                                @Override
                                public void call(Subscriber<? super UploadImageResponse> subscriber) {
                                        subscriber.onNext(mStatusUploadImageManager.getByPath(imagePath));
                                        subscriber.onCompleted();
                                    }
                                });
                            return Observable.concat(localObservable, serverObservable)
                                    .first(new Func1<UploadImageResponse, Boolean>() {
                                        @Override
                                        public Boolean call(UploadImageResponse uploadImageResponse) {
                                            return uploadImageResponse != null;
                                        }
                                    });

                    }
                })
                .toList()
                .flatMap(new Func1<List<UploadImageResponse>, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(List<UploadImageResponse> uploadImageResponses) {
                        StringBuilder sb = new StringBuilder();
                        for (UploadImageResponse uploadImageResponse : uploadImageResponses) {
                            sb.append(uploadImageResponse.getPic_id()).append(",");
                        }
                        return mStatusApi.publishWeiboOfMultiImage(publishBean.getText(), sb.toString());
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        saveOrUpdate2Draft(publishBean, Draft.STATUS_FAIL);
                    }
                })
                .doOnNext(new Action1<Status>() {
                    @Override
                    public void call(Status weibo) {
                        mDraftManager.deleteDraftById(publishBean.getId());
                        mStatusManager.saveStatus(weibo);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createStatusSubscriber());
        addSubscription(subscription);
    }

    private void publishStatusOneImage(final PublishBean publishBean) {
        mPublishServiceView.onPublishStart(publishBean);
        Subscription subscription = Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        try {
                            subscriber.onNext(ImageUtil.compressImage(publishBean.getPics().get(0),
                                    mPublishServiceView.getContent().getApplicationContext()));
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .flatMap(new Func1<String, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(String path) {
                        return mStatusApi.publishWeiboOfOneImage(publishBean.getText(), path);
                    }
                })
                .compose(ErrorCheckerTransformer.<Status>create())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        saveOrUpdate2Draft(publishBean, Draft.STATUS_FAIL);
                    }
                })
                .doOnNext(new Action1<Status>() {
                    @Override
                    public void call(Status weibo) {
                        mDraftManager.deleteDraftById(publishBean.getId());
                        mStatusManager.saveStatus(weibo);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createStatusSubscriber());
        addSubscription(subscription);
    }

    private void publishText(final PublishBean publishBean) {
        Observable<Status> publishStatusObservable = mStatusApi.publishWeiboOfText(publishBean.getText());
        mPublishServiceView.onPublishStart(publishBean);
        Subscription subscription = publishStatusObservable.subscribeOn(Schedulers.io())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        saveOrUpdate2Draft(publishBean, Draft.STATUS_FAIL);
                    }
                })
                .doOnNext(new Action1<Status>() {
                    @Override
                    public void call(Status weibo) {
                        mDraftManager.deleteDraftById(publishBean.getId());
                        mStatusManager.saveStatus(weibo);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createStatusSubscriber());
        addSubscription(subscription);
    }

    private Subscriber<Status> createStatusSubscriber() {
        return new Subscriber<Status>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.d(PublishStatusManagerPresentImp.this, "publish weibo error " + e.getMessage());
                mPublishServiceView.onPublishFail();
            }

            @Override
            public void onNext(Status status) {
                mPublishServiceView.onPublishSuccess(status);
                postPublishStatusSuccessEvent(status);
            }
        };
    }

    private void postPublishStatusSuccessEvent(Status status) {
//        RxBus.getDefault().post(EventTag.EVENT_PUBLISH_WEIBO_SUCCESS, weibo);
    }

    @Override
    public void onCreate() {
        mPublishStatusObservable = RxBus.getDefault().register(EventTag.PUBLISH_WEIBO);
        mPublishStatusObservable.subscribe(new Action1<PublishBean>() {
            @Override
            public void call(PublishBean publishBean) {
                publishStatus(publishBean);
            }
        });
    }

    private void saveOrUpdate2Draft(PublishBean publishBean, int status) {
        Draft draft = new Draft();
        draft.setCreate_at(System.currentTimeMillis());
        draft.setStatus(status);
        draft.setType(Draft.TYPE_WEIBO);
        draft.setId(publishBean.getId());
        draft.setContent(publishBean.getText());
        if (publishBean.getPics() != null && publishBean.getPics().size() > 0) {
            draft.setImage_paths(GsonUtils.toJson(publishBean.getPics()));
        }
        draft.setImages(publishBean.getPics());
        mDraftManager.insertDraft(draft);
        RxBus.getDefault().post(EventTag.EVENT_DRAFT_UPDATE, draft);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(EventTag.PUBLISH_WEIBO, mPublishStatusObservable);
    }
}
