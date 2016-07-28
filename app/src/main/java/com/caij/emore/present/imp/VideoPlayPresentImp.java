package com.caij.emore.present.imp;

import com.caij.emore.bean.VideoInfo;
import com.caij.emore.present.VideoPlayPresent;
import com.caij.emore.present.view.VideoPlayView;
import com.caij.emore.source.VideoSource;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/28.
 */
public class VideoPlayPresentImp implements VideoPlayPresent {

    private VideoPlayView mVideoPlayView;
    private long mWeiboId;
    private VideoSource mServerVideoSource;
    private VideoSource mLocalVideoSource;
    private final CompositeSubscription mCompositeSubscription;

    public VideoPlayPresentImp(long weiboId, VideoSource serverVideoSource,
                               VideoSource localVideoSource, VideoPlayView videoPlayView) {
        mWeiboId = weiboId;
        mVideoPlayView = videoPlayView;
        mServerVideoSource = serverVideoSource;
        mLocalVideoSource = localVideoSource;
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onCreate() {
        Subscription subscription = mServerVideoSource.geVideoInfo(mWeiboId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<VideoInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(VideoInfo videoInfo) {
                        mVideoPlayView.onGetVideoInfoSuccess(videoInfo);
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onDestroy() {
        mCompositeSubscription.clear();
    }
}
