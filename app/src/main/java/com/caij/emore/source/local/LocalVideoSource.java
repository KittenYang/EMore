package com.caij.emore.source.local;

import com.caij.emore.bean.VideoInfo;
import com.caij.emore.source.VideoSource;

import rx.Observable;

/**
 * Created by Caij on 2016/7/28.
 */
public class LocalVideoSource implements VideoSource {
    @Override
    public Observable<VideoInfo> geVideoInfo(long weibo_id) {
        return null;
    }

    @Override
    public void saveVideoInfo(VideoInfo videoInfo) {

    }
}
