package com.caij.emore.present.imp;

import com.caij.emore.bean.VideoInfo;
import com.caij.emore.present.VideoPlayPresent;
import com.caij.emore.ui.view.VideoPlayView;
import com.caij.emore.source.VideoSource;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/28.
 */
public class VideoPlayPresentImp extends AbsBasePresent implements VideoPlayPresent {

    private VideoPlayView mVideoPlayView;
    private long mWeiboId;
    private VideoSource mServerVideoSource;

    public VideoPlayPresentImp(long weiboId, VideoSource serverVideoSource,VideoPlayView videoPlayView) {
        mWeiboId = weiboId;
        mVideoPlayView = videoPlayView;
        mServerVideoSource = serverVideoSource;
    }

    @Override
    public void onCreate() {
        Subscription subscription = mServerVideoSource.getVideoInfo(mWeiboId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<VideoInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mVideoPlayView.onDefaultLoadError();
                    }

                    @Override
                    public void onNext(VideoInfo videoInfo) {
                        mVideoPlayView.onGetVideoInfoSuccess(videoInfo);
                    }
                });
        addSubscription(subscription);
    }

}
