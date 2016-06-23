package com.caij.weiyo.source.local;

import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.source.PublishWeiboSource;

import java.util.List;

import rx.Observable;

/**
 * Created by Caij on 2016/6/22.
 */
public class LocalPublishWeiboSourceImp implements PublishWeiboSource {

    @Override
    public Observable<Weibo> publishWeiboOfText(String token, String content) {
        return null;
    }

    @Override
    public Observable<Weibo> publishWeiboOfOneImage(String token, String content, String imagePath) {
        return null;
    }

    @Override
    public Observable<Weibo> publishWeiboOfMultiImage(String token, String source, String content, List<String> imagePaths) {
        return null;
    }
}
