package com.caij.emore.present.imp;

import com.caij.emore.UserPrefs;
import com.caij.emore.bean.Account;
import com.caij.emore.bean.PublishBean;
import com.caij.emore.bean.response.UploadImageResponse;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.PublishWeiboManagerPresent;
import com.caij.emore.present.view.PublishServiceView;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.EventUtil;
import com.caij.emore.utils.ImageUtil;

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

    private WeiboSource mPublishWeiboSource;
    private CompositeSubscription mCompositeSubscription;
    Observable<PublishBean> mPublishWeiboObservable;
    private PublishServiceView mPublishServiceView;

    public PublishWeiboManagerPresentImp(WeiboSource publishWeiboSource, PublishServiceView view) {
        mPublishWeiboSource = publishWeiboSource;
        mCompositeSubscription = new CompositeSubscription();
        mPublishServiceView = view;
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
        mPublishServiceView.onPublishStart(publishBean);
        final Account account = UserPrefs.get().getAccount();
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
                    public Observable<UploadImageResponse> call(String imagePath) {
                        try {
                            return mPublishWeiboSource.uploadWeiboOfOneImage(account.getWeicoToken().getAccess_token(), imagePath);
                        } catch (IOException e) {
                            throw new RuntimeException("文件为找到");
                        }
                    }
                })
                .doOnNext(new Action1<UploadImageResponse>() {
                    @Override
                    public void call(UploadImageResponse uploadImageResponse) {
                        //已经上传成功的图片保存到数据库 避免下次重新上传
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
                        return mPublishWeiboSource.publishWeiboOfMultiImage(account.getWeiyoToken().getAccess_token(), publishBean.getText(), sb.toString());
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
                        mPublishServiceView.onPublishFail();
                    }

                    @Override
                    public void onNext(Weibo weibo) {
                        mPublishServiceView.onPublishSuccess(weibo);
                    }
                });
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
                        return mPublishWeiboSource.publishWeiboOfOneImage(account.getWeiyoToken().getAccess_token(),
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
                        mPublishServiceView.onPublishFail();
                    }

                    @Override
                    public void onNext(Weibo weibo) {
                        mPublishServiceView.onPublishSuccess(weibo);
                    }
                });
        mCompositeSubscription.add(subscription);
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
