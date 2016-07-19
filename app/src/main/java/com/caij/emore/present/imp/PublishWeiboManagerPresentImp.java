package com.caij.emore.present.imp;

import com.caij.emore.Key;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.Account;
import com.caij.emore.bean.PublishBean;
import com.caij.emore.database.bean.UploadImageResponse;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.PublishWeiboManagerPresent;
import com.caij.emore.present.view.PublishServiceView;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.EventUtil;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.rxbus.RxBus;

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

/**
 * Created by Caij on 2016/7/19.
 */
public class PublishWeiboManagerPresentImp extends AbsTimeLinePresent<PublishServiceView> implements PublishWeiboManagerPresent {

    Observable<PublishBean> mPublishWeiboObservable;

    public PublishWeiboManagerPresentImp(WeiboSource serverWeiboSource, WeiboSource localWeiboSource, PublishServiceView view) {
        super( UserPrefs.get().getAccount().getWeicoToken().getAccess_token(),
                view, serverWeiboSource, localWeiboSource);
    }

    @Override
    public void publishWeibo(PublishBean publishBean) {
        if (publishBean.getPics().size() == 1) {
            publishWeiboOneImage(publishBean);
        } else {
            publishWeiboMuImage(publishBean);
        }
    }

    private void publishWeiboMuImage(final PublishBean publishBean) {
        mView.onPublishStart(publishBean);
        final Account account = UserPrefs.get().getAccount();
        Subscription subscription = Observable.create(new Observable.OnSubscribe<List<String>>() {
                    @Override
                    public void call(Subscriber<? super List<String>> subscriber) {
                        try {
                            List<String> outPaths = new ArrayList<String>();
                            for (String source : publishBean.getPics()) {
                                outPaths.add(ImageUtil.compressImage(source,
                                        mView.getContent().getApplicationContext()));
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
                                    uploadWeiboOfOneImage(account.getWeicoToken().getAccess_token(), imagePath)
                                    .doOnNext(new Action1<UploadImageResponse>() {
                                        @Override
                                        public void call(UploadImageResponse uploadImageResponse) {
                                            uploadImageResponse.setImagePath(imagePath);
                                            mLocalWeiboSource.saveUploadImageResponse(uploadImageResponse);
                                        }
                                    });
                            Observable<UploadImageResponse> localObservable = mLocalWeiboSource.
                                    uploadWeiboOfOneImage(account.getWeicoToken().getAccess_token(), imagePath);
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
                        return mServerWeiboSource.publishWeiboOfMultiImage(account.getWeiyoToken().getAccess_token(), publishBean.getText(), sb.toString());
                    }
                })
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        toGetImageSize(weibo);
                        doSpanNext(weibo);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weibo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onPublishFail();
                    }

                    @Override
                    public void onNext(Weibo weibo) {
                        mView.onPublishSuccess(weibo);
                        postPublishWeiboSuccessEvent(weibo);
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    private void publishWeiboOneImage(final PublishBean publishBean) {
        mView.onPublishStart(publishBean);
        final Account account = UserPrefs.get().getAccount();
        Subscription subscription = Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        try {
                            subscriber.onNext(ImageUtil.compressImage(publishBean.getPics().get(0),
                                    mView.getContent().getApplicationContext()));
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weibo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onPublishFail();
                    }

                    @Override
                    public void onNext(Weibo weibo) {
                        mView.onPublishSuccess(weibo);
                        postPublishWeiboSuccessEvent(weibo);
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    private void postPublishWeiboSuccessEvent(Weibo weibo) {
        RxBus.get().post(Key.EVENT_PUBLISH_WEIBO_SUCCESS, weibo);
    }

    @Override
    public void onCreate() {
        mPublishWeiboObservable = EventUtil.registPublishEvent();
        mPublishWeiboObservable.subscribe(new Action1<PublishBean>() {
            @Override
            public void call(PublishBean publishBean) {
                publishWeibo(publishBean);
            }
        });
    }

    @Override
    public void onDestroy() {
        EventUtil.unregistPublishEvent(mPublishWeiboObservable);
        mCompositeSubscription.clear();
    }
}
