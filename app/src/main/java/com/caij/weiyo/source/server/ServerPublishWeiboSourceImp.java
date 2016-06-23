package com.caij.weiyo.source.server;

import com.caij.weiyo.api.WeiBoService;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.bean.response.UploadImageResponse;
import com.caij.weiyo.source.PublishWeiboSource;
import com.caij.weiyo.utils.ImageUtil;
import com.caij.weiyo.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/6/22.
 */
public class ServerPublishWeiboSourceImp implements PublishWeiboSource {

    private final WeiBoService mWeiBoService;

    public ServerPublishWeiboSourceImp() {
        mWeiBoService = WeiBoService.Factory.create();
    }

    @Override
    public Observable<Weibo> publishWeiboOfText(String token, String content) {
        return mWeiBoService.publishWeiboOfOnlyText(token, content);
    }

    @Override
    public Observable<Weibo> publishWeiboOfOneImage(final String token, final String content, final String imagePath) {
        final File file = new File(imagePath);
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String type  = ImageUtil.getImageType(file);
                    subscriber.onNext(type);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    LogUtil.d(ServerPublishWeiboSourceImp.this, e.getMessage());
                    subscriber.onError(e);
                }
            }
        }).flatMap(new Func1<String, Observable<Weibo>>() {
            @Override
            public Observable<Weibo> call(String type) {
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("image/" + type), file);
                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("pic", file.getName(), requestFile);
                return mWeiBoService.publishWeiboOfOneImage(token, content, body);
            }
        });
    }

    @Override
    public Observable<Weibo> publishWeiboOfMultiImage(final String token, final String source, final String content, List<String> imagePaths) {
        return Observable.from(imagePaths)
                .flatMap(new Func1<String, Observable<UploadImageResponse>>() {
                    @Override
                    public Observable<UploadImageResponse> call(String s) {
                        File file = new File(s);
                        try {
                            String type = ImageUtil.getImageType(file);
                            RequestBody requestFile =
                                    RequestBody.create(MediaType.parse("image/" + type), file);
                            MultipartBody.Part body =
                                    MultipartBody.Part.createFormData("pic", file.getName(), requestFile);
                            return mWeiBoService.uploadWeiboOfOneImage(token, source, body);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
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
                        return mWeiBoService.publishWeiboOfMultiImage(token, content, sb.toString());
                    }
                });
    }
}
