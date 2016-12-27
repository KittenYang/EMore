package com.caij.emore.remote;

import com.caij.emore.bean.MessageImage;
import com.caij.emore.bean.MessageUser;
import com.caij.emore.bean.response.Response;
import com.caij.emore.bean.response.UserMessageResponse;
import com.caij.emore.database.bean.DirectMessage;

import java.util.Map;

import rx.Observable;

/**
 * Created by Caij on 2016/10/13.
 */

public interface MessageApi {

    Observable<MessageUser> getConversations(int count, long cursor) ;

    Observable<UserMessageResponse> getChatMessages(long toUid, long selfUid, long since_id, long max_id,
                                                    int count, int page);

    Observable<DirectMessage> createTextMessage(String text, long uid);

    Observable<DirectMessage> createImageMessage(final String text, long uid, final String screenName,
                                                 String fids);

    Observable<MessageImage> uploadMessageImage(long uid, String imagePath);

    Observable<Response> deleteConversation(long uid);

    Observable<Response> blockUser(long recipientId);

    Observable<UserMessageResponse> getStrangerMessages(long since_id, long max_id,
                                                    int count, int page);
}
