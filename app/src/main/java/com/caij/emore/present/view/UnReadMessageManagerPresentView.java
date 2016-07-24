package com.caij.emore.present.view;

import com.caij.emore.database.bean.UnReadMessage;

/**
 * Created by Caij on 2016/7/23.
 */
public interface UnReadMessageManagerPresentView {
    void onIntervalMillisUpdate();

    void notifyMessage(UnReadMessage serverUnReadMessage, UnReadMessage localUnReadMessage);
}
