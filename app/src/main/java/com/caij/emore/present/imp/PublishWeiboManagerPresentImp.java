package com.caij.emore.present.imp;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.caij.emore.Event;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.Account;
import com.caij.emore.bean.PublishBean;
import com.caij.emore.database.bean.Draft;
import com.caij.emore.database.bean.ImageInfo;
import com.caij.emore.database.bean.UploadImageResponse;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.PublishWeiboManagerPresent;
import com.caij.emore.ui.view.PublishServiceView;
import com.caij.emore.source.DraftSource;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.source.local.LocalImageSource;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/19.
 */
public class PublishWeiboManagerPresentImp implements PublishWeiboManagerPresent {

    LocalImageSource mLocalImageSouce;
    Observable<PublishBean> mPublishWeiboObservable;
    DraftSource mDraftSource;
    WeiboSource mServerWeiboSource;
    Account mAccount;
    WeiboSource mLocalWeiboSource;
    CompositeSubscription mCompositeSubscription;
    PublishServiceView mPublishServiceView;

    public PublishWeiboManagerPresentImp(Account account, WeiboSource serverWeiboSource,
                                         WeiboSource localWeiboSource,
                                         DraftSource localDraftSource,
                                         PublishServiceView view) {
        mAccount = account;
        mServerWeiboSource = serverWeiboSource;
        mLocalWeiboSource = localWeiboSource;
        mDraftSource = localDraftSource;
        mPublishServiceView = view;
        mLocalImageSouce = new LocalImageSource();
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void publishWeibo(final PublishBean publishBean) {
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
                    publishWeiboOneImage(publishBean);
                } else {
                    publishWeiboMuImage(publishBean);
                }
            }
        });
    }

    private void publishWeiboMuImage(final PublishBean publishBean) {
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
                        try {
                            Observable<UploadImageResponse> serverObservable = mServerWeiboSource.
                                    uploadWeiboOfOneImage(mAccount.getWeicoToken().getAccess_token(), imagePath)
                                    .doOnNext(new Action1<UploadImageResponse>() {
                                        @Override
                                        public void call(UploadImageResponse uploadImageResponse) {
                                            uploadImageResponse.setImagePath(imagePath);
                                            mLocalWeiboSource.saveUploadImageResponse(uploadImageResponse);
                                        }
                                    });
                            Observable<UploadImageResponse> localObservable = mLocalWeiboSource.
                                    uploadWeiboOfOneImage(mAccount.getWeicoToken().getAccess_token(), imagePath);
                            return Observable.concat(localObservable, serverObservable)
                                    .first(new Func1<UploadImageResponse, Boolean>() {
                                        @Override
                                        public Boolean call(UploadImageResponse uploadImageResponse) {
                                            return uploadImageResponse != null;
                                        }
                                    });
                        } catch (IOException e) {
                            throw new RuntimeException("文件为找到");
                        }
                    }
                })
                .toList()
                .flatMap(new Func1<List<UploadImageResponse>, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(List<UploadImageResponse> uploadImageResponses) {
                        StringBuilder sb = new StringBuilder();
                        for (UploadImageResponse uploadImageResponse : uploadImageResponses) {
                            sb.append(uploadImageResponse.getPic_id()).append(",");
                        }
                        return mServerWeiboSource.publishWeiboOfMultiImage(mAccount.getWeiyoToken().getAccess_token(), publishBean.getText(), sb.toString());
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        saveOrUpdate2Draft(publishBean, Draft.STATUS_FAIL);
                    }
                })
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        mDraftSource.deleteDraftById(publishBean.getId());
                        mLocalWeiboSource.saveWeibo(mAccount.getWeiyoToken().getAccess_token(), weibo);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createWeiboSubscriber());
        mCompositeSubscription.add(subscription);
    }

    private void publishWeiboOneImage(final PublishBean publishBean) {
        mPublishServiceView.onPublishStart(publishBean);
        final Account account = UserPrefs.get().getAccount();
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
                .flatMap(new Func1<String, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(String path) {
                        return mServerWeiboSource.publishWeiboOfOneImage(account.getWeiyoToken().getAccess_token(),
                                publishBean.getText(), path);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        saveOrUpdate2Draft(publishBean, Draft.STATUS_FAIL);
                    }
                })
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        mDraftSource.deleteDraftById(publishBean.getId());
                        mLocalWeiboSource.saveWeibo(mAccount.getWeiyoToken().getAccess_token(), weibo);

                        try {
                            ImageInfo locakImage = new ImageInfo();
                            locakImage.setUrl(weibo.getPic_urls().get(0).getThumbnail_pic());
                            BitmapFactory.Options options = ImageUtil.getImageOptions(new File(publishBean.getPics().get(0)));
                            locakImage.setHeight(options.outHeight);
                            locakImage.setWidth(options.outWidth);
                            mLocalImageSouce.save(locakImage);
                        }catch (Exception e) {
                            LogUtil.d(PublishWeiboManagerPresentImp.this, "publish one image save image size error");
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createWeiboSubscriber());
        mCompositeSubscription.add(subscription);
    }

    private void publishText(final PublishBean publishBean) {
        final Account account = UserPrefs.get().getAccount();
        Observable<Weibo> publishWeiboObservable = mServerWeiboSource.
                publishWeiboOfText(account.getWeiyoToken().getAccess_token(), publishBean.getText());
        mPublishServiceView.onPublishStart(publishBean);
        Subscription subscription = publishWeiboObservable.subscribeOn(Schedulers.io())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        saveOrUpdate2Draft(publishBean, Draft.STATUS_FAIL);
                    }
                })
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        mDraftSource.deleteDraftById(publishBean.getId());
                        mLocalWeiboSource.saveWeibo(mAccount.getWeiyoToken().getAccess_token(), weibo);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createWeiboSubscriber());
        mCompositeSubscription.add(subscription);
    }

    private Subscriber<Weibo> createWeiboSubscriber() {
        return new Subscriber<Weibo>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.d(PublishWeiboManagerPresentImp.this, "publish weibo error " + e.getMessage());
                mPublishServiceView.onPublishFail();
            }

            @Override
            public void onNext(Weibo weibo) {
                mPublishServiceView.onPublishSuccess(weibo);
                postPublishWeiboSuccessEvent(weibo);
            }
        };
    }

    private void postPublishWeiboSuccessEvent(Weibo weibo) {
        RxBus.getDefault().post(Event.EVENT_PUBLISH_WEIBO_SUCCESS, weibo);
    }

    @Override
    public void onCreate() {
        mPublishWeiboObservable = RxBus.getDefault().register(Event.PUBLISH_WEIBO);
        mPublishWeiboObservable.subscribe(new Action1<PublishBean>() {
            @Override
            public void call(PublishBean publishBean) {
                publishWeibo(publishBean);
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
        mDraftSource.saveDraft(draft);
        RxBus.getDefault().post(Event.EVENT_DRAFT_UPDATE, draft);
    }

    @Override
    public void onDestroy() {
        RxBus.getDefault().unregister(Event.PUBLISH_WEIBO, mPublishWeiboObservable);
        mCompositeSubscription.clear();
    }
}
