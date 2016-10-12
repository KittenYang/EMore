package com.caij.emore.manager;

import com.caij.emore.database.bean.UnReadMessage;

/**
 * Created by Caij on 2016/10/9.
 */

public interface NotifyManager {

    public UnReadMessage getUnReadMessage(final long uid);

    public void resetUnReadMessage(final long uid, final String type, final int value);
}
