package com.caij.emore.present;

import com.caij.emore.database.bean.DirectMessage;

/**
 * Created by Caij on 2016/7/15.
 */
public interface ChatManagerPresent extends BasePresent {

    void senMessage(DirectMessage message);
}
