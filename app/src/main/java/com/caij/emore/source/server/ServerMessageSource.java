package com.caij.emore.source.server;

import com.caij.emore.api.WeiBoService;
import com.caij.emore.bean.MessageUser;
import com.caij.emore.bean.UnreadMessageCount;
import com.caij.emore.bean.response.UserMessageResponse;
import com.caij.emore.source.MessageSource;

import rx.Observable;

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
}
