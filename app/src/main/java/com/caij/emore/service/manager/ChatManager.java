package com.caij.emore.service.manager;

import com.caij.emore.account.UserPrefs;
import com.caij.emore.present.ChatManagerPresent;
import com.caij.emore.present.imp.ChatManagerPresentImp;
import com.caij.emore.source.local.LocalMessageSource;
import com.caij.emore.source.server.ServerMessageSource;

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
        mChatManagerPresent = new ChatManagerPresentImp(UserPrefs.get(ctx).getToken().getAccess_token(),
                new ServerMessageSource(), new LocalMessageSource());
        mChatManagerPresent.onCreate();
    }

    @Override
    public void reset() {
        mChatManagerPresent.onDestroy();
    }
}
