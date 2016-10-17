package com.caij.emore.remote.imp;

import com.caij.emore.EMApplication;
import com.caij.emore.Key;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.api.WeiBoService;
import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.MessageImage;
import com.caij.emore.bean.MessageUser;
import com.caij.emore.bean.response.Response;
import com.caij.emore.bean.response.UserMessageResponse;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.remote.MessageApi;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.rxjava.RxUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/10/13.
 */

public class MessageApiImp implements MessageApi {

    private WeiBoService mWeiBoService;
    private WeiCoService mWeiCoService;

    public MessageApiImp() {
        mWeiBoService = WeiBoService.Factory.create();
        mWeiCoService =  WeiCoService.WeiCoFactory.create();
    }

    @Override
    public Observable<MessageUser> getConversations(int count, long cursor) {
        return mWeiBoService.getConversations(count, cursor);
    }

    @Override
    public Observable<UserMessageResponse> getChatMessages(long toUid, long selfUid, long since_id, long max_id, int count, int page) {
        return mWeiCoService.getChatMessages(toUid, since_id, max_id, count, page);
    }

    @Override
    public Observable<DirectMessage> createTextMessage(String text, long uid) {
        return mWeiBoService.createMessage(text, uid, "", "");
    }

    @Override
    public Observable<DirectMessage> createImageMessage(String text, long uid, String screenName, String fids) {
        return mWeiBoService.createMessage(text, uid, screenName, fids);
    }

    @Override
    public Observable<MessageImage> uploadMessageImage(final long uid, String imagePath) {
        final File file = new File(imagePath);
        return RxUtil.createDataObservable(new RxUtil.Provider<String>() {
            @Override
            public String getData() throws IOException {
                return ImageUtil.getImageType(file).getValue();
            }
        }).flatMap(new Func1<String, Observable<MessageImage>>() {
                        @Override
                        public Observable<MessageImage> call(String type) {
                    RequestBody requestFile =
                            RequestBody.create(MediaType.parse("image/" + type), file);
                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                    return  mWeiBoService.uploadMessageImage(Key.UPLOAD_MESSAGE_IMAGE_URL,
                            UserPrefs.get(EMApplication.getInstance()).getToken().getAccess_token(),
                            Key.WEICO_APP_ID, Key.WEICO_APP_FROM, uid, body);
                }
            });
    }

    @Override
    public Observable<Response> deleteConversation(long uid) {
        return mWeiBoService.deleteMessageConversation(uid);
    }

}
