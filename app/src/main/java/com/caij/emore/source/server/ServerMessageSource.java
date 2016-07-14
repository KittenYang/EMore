package com.caij.emore.source.server;

import com.caij.emore.Key;
import com.caij.emore.api.WeiBoService;
import com.caij.emore.bean.MessageUser;
import com.caij.emore.bean.UnreadMessageCount;
import com.caij.emore.bean.response.UserMessageResponse;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.database.bean.MessageImage;
import com.caij.emore.source.MessageSource;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/7/7.
 */
public class ServerMessageSource implements MessageSource {

    private WeiBoService mWeiBoService;

    public ServerMessageSource() {
        mWeiBoService = WeiBoService.Factory.create();
    }

    @Override
    public Observable<UnreadMessageCount> getUnReadMessage(String accessToken, long uid) {
        return mWeiBoService.getUnReadMessage("https://rm.api.weibo.com/2/remind/unread_count.json", accessToken, uid);
    }

    @Override
    public Observable<MessageUser> getMessageUserList(String accessToken, int count, long cursor) {
        return mWeiBoService.getMessageUserList(accessToken, count, cursor);
    }

    @Override
    public Observable<UserMessageResponse> getUserMessage(String accessToken, long uid, long since_id, long max_id, int count, int page) {
        return mWeiBoService.getUserMessage(accessToken, uid, since_id, max_id, count, page);
    }

    @Override
    public Observable<DirectMessage> createTextMessage(String accessToken, String text, long uid) {
        return mWeiBoService.createMessage(accessToken, text, uid, "", "");
    }

    @Override
    public Observable<DirectMessage> createImageMessage(final String accessToken, final Map<String, Object> paramMap, final String text,
                                                        final String imagePath, final long uid, final String screenName) {
        final File file = new File(imagePath);
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String type  = ImageUtil.getImageType(file);
                    subscriber.onNext(type);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        })
        .flatMap(new Func1<String, Observable<MessageImage>>() {
            @Override
            public Observable<MessageImage> call(String type) {
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("image/" + type), file);
                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                return  mWeiBoService.uploadMessageImage(Key.UPLOAD_MESSAGE_IMAGE_URL, paramMap, accessToken, uid, body);
            }
        })
        .flatMap(new Func1<MessageImage, Observable<DirectMessage>>() {
            @Override
            public Observable<DirectMessage> call(MessageImage uploadMessageImageResponse) {
                long vifid = uploadMessageImageResponse.getVfid();
                long tofid = uploadMessageImageResponse.getTovfid();
                StringBuilder fids = new StringBuilder().append(vifid).append(",").append(tofid);
                return mWeiBoService.createMessage(accessToken, text, uid, screenName, fids.toString());
            }
        });
    }

    @Override
    public void saveMessage(DirectMessage message) {

    }

    @Override
    public Observable<MessageImage> getMessageImageInfo(String accessToken, long fid) {
        return mWeiBoService.getMessageImageInfo(Key.QUERY_MESSAGE_IMAGE_URL, accessToken, fid);
    }

    @Override
    public void saveMessageImage(MessageImage messageImage) {

    }

}
