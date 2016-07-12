package com.caij.emore.present;

import com.caij.emore.bean.DirectMessage;

/**
 * Created by Caij on 2016/7/10.
 */
public interface ChatPresent extends ListPresent {

    void sendMessage(DirectMessage message);
}
