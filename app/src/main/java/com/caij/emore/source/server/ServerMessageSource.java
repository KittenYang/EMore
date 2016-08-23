package com.caij.emore.source.server;

import com.caij.emore.Key;
import com.caij.emore.api.WeiBoService;
import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.MessageUser;
import com.caij.emore.bean.response.Response;
import com.caij.emore.bean.response.UserMessageResponse;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.database.bean.MessageImage;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.source.MessageSource;
import com.caij.emore.utils.ImageUtil;

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
    private WeiCoService mWeiCoService;

    public ServerMessageSource() {
        mWeiBoService = WeiBoService.Factory.create();
        mWeiCoService =  WeiCoService.WeiCoFactory.create();
    }

    @Override
    public Observable<UnReadMessage> getUnReadMessage(String accessToken, long uid) {
        return mWeiCoService.getUnreadMessageCount(accessToken, Key.WEICO_APP_ID, Key.WEICO_APP_FROM, uid);
    }

    @Override
    public Observable<MessageUser> getMessageUserList(String accessToken, int count, long cursor) {
        return mWeiBoService.getMessageUserList(accessToken, count, cursor);
    }

    @Override
    public Observable<UserMessageResponse> getUserMessage(String accessToken, long toUid, long selfUid,
                                                          long since_id, long max_id, int count, int page) {
        return mWeiBoService.getUserMessage(accessToken, toUid, since_id, max_id, count, page);
    }

    @Override
    public Observable<DirectMessage> createTextMessage(String accessToken, String text, long uid) {
        return mWeiBoService.createMessage(accessToken, text, uid, "", "");
    }

    @Override
    public Observable<DirectMessage> createImageMessage(final String accessToken, final String text,
                                                        final long uid, final String screenName,  String fids) {
        return mWeiBoService.createMessage(accessToken, text, uid, screenName, fids);
    }

    @Override
    public Observable<MessageImage> uploadMessageImage(final Map<String, Object> paramMap, final String accessToken, final long uid, String imagePath) {
        final File file = new File(imagePath);
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String type  = ImageUtil.getImageType(file).getValue();
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

    @Override
    public void removeMessage(DirectMessage bean) {

    }

    @Override
    public void removeMessageById(long id) {

    }

    @Override
    public DirectMessage getMessageById(long id) {
        return null;
    }

    @Override
    public void saveUnReadMessage(UnReadMessage serverUnReadMessage) {

    }

    @Override
    public Observable<Response> resetUnReadMessage(String token, long uid, String source, String from, String type, int value) {
        return mWeiCoService.resetUnReadMsg(token, source, from, type, value);
    }

}
