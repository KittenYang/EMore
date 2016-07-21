package com.caij.emore.present.view;


import com.caij.emore.database.bean.DirectMessage;

import java.util.List;

/**
 * Created by Caij on 2016/7/11.
 */
public interface DirectMessageView extends BaseListView<DirectMessage> {

    void toScrollToPosition(int position);

    void attemptSmoothScrollToBottom();

    void notifyDataChange();

    void addMore(List<DirectMessage> directMessages, int size);
}
