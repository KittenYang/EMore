package com.caij.emore.ui.view;


import com.caij.emore.database.bean.DirectMessage;

/**
 * Created by Caij on 2016/7/11.
 */
public interface DirectMessageView extends ListView<DirectMessage> {

    void toScrollToPosition(int position);

    void attemptSmoothScrollToBottom();

}