package com.caij.emore.bean.event;

import com.caij.emore.database.bean.DirectMessage;

import java.io.Serializable;

/**
 * Created by Caij on 2016/8/9.
 */
public class MessageResponseEvent extends Event {

    public long localMessageId;

    public DirectMessage message;

    public MessageResponseEvent(String type, long localMessageId, DirectMessage message) {
        this.type = type;
        this.localMessageId = localMessageId;
        this.message = message;
    }
}
