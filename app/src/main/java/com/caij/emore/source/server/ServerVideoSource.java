package com.caij.emore.source.server;

import com.caij.emore.Key;
import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.VideoInfo;
import com.caij.emore.source.VideoSource;

import rx.Observable;

/**
 * Created by Caij on 2016/7/28.
 */
public class ServerVideoSource implements VideoSource {



    @Override
    public Observable<VideoInfo> geVideoInfo(long weibo_id) {
        return WeiCoService.WeiCoFactory.create().getVideoInfo(Key.WEICO_API_URL, "get_video", "default", weibo_id);
    }

    @Override
    public void saveVideoInfo(VideoInfo videoInfo) {

    }
}
