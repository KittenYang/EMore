package com.caij.emore.source;

import com.caij.emore.bean.MessageUser;
import com.caij.emore.bean.response.Response;
import com.caij.emore.bean.response.UserMessageResponse;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.database.bean.MessageImage;
import com.caij.emore.database.bean.UnReadMessage;

import java.util.Map;

import rx.Observable;

/**
 * Created by Caij on 2016/7/7.
 */
public interface MessageSource {

    Observable<UnReadMessage> getUnReadMessage(String accessToken, long uid) ;

    Observable<MessageUser> getMessageUserList(String accessToken, int count, long cursor) ;

    Observable<UserMessageResponse> getUserMessage(String accessToken, long uid, long since_id, long max_id,
                                                   int count, int page);

    Observable<DirectMessage> createTextMessage(String accessToken, String text, long uid);

    Observable<DirectMessage> createImageMessage(String accessToken,Map<String, Object> paramMap,
                                                 final String text,String imagePath, long uid, final String screenName);

    void saveMessage(DirectMessage message);

    public Observable<MessageImage> getMessageImageInfo(String accessToken, long fid);

    void saveMessageImage(MessageImage messageImage);

    void removeMessage(DirectMessage bean);

    void saveUnReadMessage(UnReadMessage serverUnReadMessage);

    Observable<Response> resetUnReadMessage(String token, String source, String from, String type, int value);
}
