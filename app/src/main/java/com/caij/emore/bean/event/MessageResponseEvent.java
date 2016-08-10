package com.caij.emore.bean.event;

import com.caij.emore.database.bean.DirectMessage;

import java.io.Serializable;

/**
 * Created by Caij on 2016/8/9.
 */
public class MessageResponseEvent implements Serializable {

    public long localMessageId;

    public DirectMessage message;

    public MessageResponseEvent(long localMessageId, DirectMessage message) {
        this.localMessageId = localMessageId;
        this.message = message;
    }
}
