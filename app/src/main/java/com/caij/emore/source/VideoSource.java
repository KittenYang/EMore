package com.caij.emore.source;

import com.caij.emore.bean.VideoInfo;

import rx.Observable;

/**
 * Created by Caij on 2016/7/28.
 */
public interface VideoSource {

    public Observable<VideoInfo> getVideoInfo(long weiboId);
}
