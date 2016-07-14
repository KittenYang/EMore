package com.caij.emore.present.view;


import com.caij.emore.database.bean.DirectMessage;

/**
 * Created by Caij on 2016/7/11.
 */
public interface DirectMessageView extends BaseListView<DirectMessage> {

    void onSendEnd(DirectMessage message);

    void toScrollToPosition(int position);

    void attemptSmoothScrollToBottom();
}
