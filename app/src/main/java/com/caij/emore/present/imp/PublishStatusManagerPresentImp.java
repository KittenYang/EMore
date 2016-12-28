package com.caij.emore.present.imp;

import android.graphics.BitmapFactory;

import com.caij.emore.EventTag;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.bean.PublishBean;
import com.caij.emore.bean.StatusImageInfo;
import com.caij.emore.manager.DraftManager;
import com.caij.emore.manager.StatusManager;
import com.caij.emore.manager.StatusUploadImageManager;
import com.caij.emore.database.bean.Draft;
import com.caij.emore.database.bean.Status;
import com.caij.emore.database.bean.UploadImageResponse;
import com.caij.emore.present.PublishStatusManagerPresent;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.PublishStatusView;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.RxUtil;
import com.caij.emore.utils.rxjava.SubscriberAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/7/19.
 */
public class PublishStatusManagerPresentImp extends AbsBasePresent implements PublishStatusManagerPresent {

    private Observable<PublishBean> mPublishStatusObservable;
    private DraftManager mDraftManager;
    StatusApi mStatusApi;
    private StatusManager mStatusManager;
    private PublishStatusView mPublishStatusView;
    private StatusUploadImageManager mStatusUploadImageManager;

    public PublishStatusManagerPresentImp(StatusApi statusApi,
                                          StatusManager statusManager,
                                          DraftManager draftManager,
                                          StatusUploadImageManager statusUploadImageManager,
                                          PublishStatusView view) {
        mStatusApi = statusApi;
        mStatusManager = statusManager;
        mDraftManager = draftManager;
        mStatusUploadImageManager = statusUploadImageManager;
        mPublishStatusView = view;
    }

    @Override
    public void publishStatus(final PublishBean publishBean) {
        RxUtil.createDataObservable(new RxUtil.Provider<Object>() {
                @Override
                public Object getData() throws Exception {
                    saveOrUpdate2Draft(publishBean, Draft.STATUS_SENDING);
                    return null;
                }
            }).compose(SchedulerTransformer.create())
            .subscribe(new SubscriberAdapter<Object>() {
                @Override
                public void onNext(Object o) {
                    if (publishBean.getPics() == null || publishBean.getPics().size() == 0) {
                        publishText(publishBean);
                    } else if (publishBean.getPics().size() == 1) {
                        publishStatusOneImage(publishBean);
                    } else {
                        publishStatusMuImage(publishBean);
                    }
                }
            });
    }

    private void publishStatusMuImage(final PublishBean publishBean) {
        mPublishStatusView.onPublishStart(publishBean);
        Subscription subscription = RxUtil.createDataObservable(new RxUtil.Provider<List<String>>() {
            @Override
            public List<String> getData() throws Exception {
                    List<String> outPaths = new ArrayList<String>();
                    for (String source : publishBean.getPics()) {
                        outPaths.add(ImageUtil.compressImage(source,
                                mPublishStatusView.getContent().getApplicationContext()));
                    }
                    return outPaths;
                }
            }).flatMap(new Func1<List<String>, Observable<String>>() {
                @Override
                public Observable<String> call(List<String> strings) {
                    return Observable.from(strings);
                }
            }).flatMap(new Func1<String, Observable<UploadImageResponse>>() {
                @Override
                public Observable<UploadImageResponse> call(final String imagePath) {
                    return uploadImage(imagePath);
                }
            })
            .toList()
            .flatMap(new Func1<List<UploadImageResponse>, Observable<Status>>() {
                @Override
                public Observable<Status> call(List<UploadImageResponse> uploadImageResponses) {
                    return publishStatusOfMultiImage(publishBean.getText(), uploadImageResponses);
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
                public void call(Status status) {
                    mDraftManager.deleteDraftById(publishBean.getId());
                    mStatusManager.saveStatus(status);
                }
            })
            .compose(SchedulerTransformer.<Status>create())
            .subscribe(createStatusSubscriber());

        addSubscription(subscription);
    }

    private Observable<UploadImageResponse> uploadImage(final String imagePath) {
        Observable<UploadImageResponse> serverObservable = mStatusApi.uploadStatusOfOneImage(imagePath)
                .doOnNext(new Action1<UploadImageResponse>() {
                    @Override
                    public void call(UploadImageResponse uploadImageResponse) {
                        uploadImageResponse.setImagePath(imagePath);
                        mStatusUploadImageManager.insert(uploadImageResponse);
                    }
                });
        Observable<UploadImageResponse> localObservable = RxUtil.createDataObservable(new RxUtil.Provider<UploadImageResponse>() {
                @Override
                public UploadImageResponse getData() throws Exception {
                    return mStatusUploadImageManager.getByPath(imagePath);
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

    private Observable<Status> publishStatusOfMultiImage(String text, final List<UploadImageResponse> uploadImageResponses) {
        StringBuilder sb = new StringBuilder();
        for (UploadImageResponse uploadImageResponse : uploadImageResponses) {
            sb.append(uploadImageResponse.getPic_id()).append(",");
        }
        return mStatusApi.publishStatusOfMultiImage(text, sb.toString())
                .compose(ErrorCheckerTransformer.<Status>create())
                .doOnNext(new Action1<Status>() {
                    @Override
                    public void call(Status status) {
                        LinkedHashMap<String, StatusImageInfo> map = new LinkedHashMap<>(uploadImageResponses.size());
                        for (UploadImageResponse uploadImageResponse : uploadImageResponses) {
                            StatusImageInfo statusImageInfo = new StatusImageInfo();
                            StatusImageInfo.Image image = new StatusImageInfo.Image();
                            File file = new File(uploadImageResponse.getImagePath());
                            BitmapFactory.Options options = ImageUtil.getImageOptions(file);
                            image.setHeight(options.outHeight);
                            image.setWidth(options.outWidth);
                            image.setUrl("file://" + file.getAbsolutePath());
                            String type = "png";
                            try {type = ImageUtil.getImageType(file);} catch (IOException e) {}
                            image.setType(type);
                            statusImageInfo.setBmiddle(image);
                            map.put(uploadImageResponse.getPic_id(),statusImageInfo);
                        }
                        status.setPic_infos(map);
                    }
                });
    }

    private void publishStatusOneImage(final PublishBean publishBean) {
        mPublishStatusView.onPublishStart(publishBean);
        Subscription subscription = RxUtil.createDataObservable(new RxUtil.Provider<String>() {
                @Override
                public String getData() throws Exception {
                    return ImageUtil.compressImage(publishBean.getPics().get(0),
                            mPublishStatusView.getContent().getApplicationContext());
                }
            }).flatMap(new Func1<String, Observable<Status>>() {
                @Override
                public Observable<Status> call(final String path) {
                    return createPublishStatusOfOneImage(publishBean.getText(), path);
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
                public void call(Status status) {
                    mDraftManager.deleteDraftById(publishBean.getId());

                    mStatusManager.saveStatus(status);
                }
            })
            .compose(SchedulerTransformer.<Status>create())
            .subscribe(createStatusSubscriber());

        addSubscription(subscription);
    }

    private Observable<Status> createPublishStatusOfOneImage(String text, final String path) {
        return mStatusApi.publishStatusOfOneImage(text, path)
                .compose(ErrorCheckerTransformer.<Status>create())
                .doOnNext(new Action1<Status>() {
                    @Override
                    public void call(Status status) {
                        LinkedHashMap<String, StatusImageInfo> map = new LinkedHashMap<String, StatusImageInfo>(1);
                        StatusImageInfo statusImageInfo = new StatusImageInfo();
                        StatusImageInfo.Image image = new StatusImageInfo.Image();
                        File file = new File(path);
                        BitmapFactory.Options options = ImageUtil.getImageOptions(file);
                        image.setHeight(options.outHeight);
                        image.setWidth(options.outWidth);
                        image.setUrl("file://" + file.getAbsolutePath());
                        String type = "png";
                        try {type = ImageUtil.getImageType(file);} catch (IOException e) {}
                        image.setType(type);
                        statusImageInfo.setBmiddle(image);
                        map.put(status.getPic_ids().get(0),statusImageInfo);
                        status.setPic_infos(map);
                    }
                });

    }

    private void publishText(final PublishBean publishBean) {
        mPublishStatusView.onPublishStart(publishBean);
        Subscription subscription = mStatusApi.publishStatusOfText(publishBean.getText())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        saveOrUpdate2Draft(publishBean, Draft.STATUS_FAIL);
                    }
                })
                .doOnNext(new Action1<Status>() {
                    @Override
                    public void call(Status status) {
                        mDraftManager.deleteDraftById(publishBean.getId());
                        mStatusManager.saveStatus(status);
                    }
                })
                .compose(SchedulerTransformer.<Status>create())
                .subscribe(createStatusSubscriber());
        addSubscription(subscription);
    }

    private Subscriber<Status> createStatusSubscriber() {
        return new ResponseSubscriber<Status>(mPublishStatusView) {

            @Override
            protected void onFail(Throwable e) {
                LogUtil.d(PublishStatusManagerPresentImp.this, "publish weibo error " + e.getMessage());
                mPublishStatusView.onPublishFail();
            }

            @Override
            public void onNext(Status status) {
                mPublishStatusView.onPublishSuccess(status);
                postPublishStatusSuccessEvent(status);
            }
        };
    }

    private void postPublishStatusSuccessEvent(Status status) {
        RxBus.getDefault().post(EventTag.EVENT_PUBLISH_WEIBO_SUCCESS, status);
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
