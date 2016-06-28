package com.caij.weiyo.source;

import com.caij.weiyo.bean.Weibo;

import java.util.List;

import rx.Observable;

/**
 * Created by Caij on 2016/6/22.
 */
public interface PublishWeiboSource {

    public Observable<Weibo> publishWeiboOfText(String token, String content);

    public Observable<Weibo> publishWeiboOfOneImage(String token, String content, String imagePath);

    public Observable<Weibo> publishWeiboOfMultiImage(String weiyoToken, String weicoToken, String content, List<String> imagePaths);
}
