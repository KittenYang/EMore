package com.caij.emore.service.manager;

import com.caij.emore.account.UserPrefs;
import com.caij.emore.manager.imp.MessageManagerImp;
import com.caij.emore.present.ChatManagerPresent;
import com.caij.emore.present.imp.ChatManagerPresentImp;
import com.caij.emore.remote.imp.MessageApiImp;

/**
 * Created by Caij on 2016/7/11.
 */
public class ChatManager extends IManager {

    private static ChatManager inst = new ChatManager();

    public static ChatManager getInstance() {
        return inst;
    }

    private ChatManagerPresent mChatManagerPresent;

    @Override
    protected void doOnCreate() {
        mChatManagerPresent = new ChatManagerPresentImp(new MessageApiImp(), new MessageManagerImp());
        mChatManagerPresent.onCreate();
    }

    @Override
    public void reset() {
        mChatManagerPresent.onDestroy();
    }
}
