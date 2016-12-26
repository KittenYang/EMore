package com.caij.emore.ui.view;


import android.support.v4.util.ArrayMap;

import com.caij.emore.database.bean.DirectMessage;

import java.util.List;

/**
 * Created by Caij on 2016/7/11.
 */
public interface DirectMessageView extends ListView<DirectMessage> {

    void toScrollToPosition(int position);

    void attemptSmoothScrollToBottom();

    void setShowTimeDirectMessageMap(ArrayMap<String, DirectMessage> showTimeDirectMessageMap);


    void notifyItemRemoved(List<DirectMessage> directMessage, int index);
}
