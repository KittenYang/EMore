package com.caij.emore.source;

import com.caij.emore.bean.MessageUser;
import com.caij.emore.bean.UnreadMessageCount;
import com.caij.emore.bean.response.UserMessageResponse;

import rx.Observable;

/**
 * Created by Caij on 2016/7/7.
 */
public interface MessageSource {

    Observable<UnreadMessageCount> getUnReadMessage(String accessToken, long uid) ;

    Observable<MessageUser> getMessageUserList(String accessToken, int count, long cursor) ;

    Observable<UserMessageResponse> getUserMessage(String accessToken, long uid, long since_id, long max_id,
                                                   int count, int page);
}
