package com.caij.emore.present.view;

import com.caij.emore.bean.DirectMessage;

/**
 * Created by Caij on 2016/7/11.
 */
public interface DirectMessageView extends BaseListView<DirectMessage> {
    void toScrollBottom();

    void onSendEnd(DirectMessage message);
}
