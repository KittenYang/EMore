package com.caij.emore.service.manager;

import android.text.TextUtils;

import com.caij.emore.Key;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.present.ChatManagerPresent;
import com.caij.emore.present.ChatPresent;
import com.caij.emore.present.imp.ChatManagerPresentImp;
import com.caij.emore.source.MessageSource;
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
        mChatManagerPresent = new ChatManagerPresentImp(UserPrefs.get().getWeiCoToken().getAccess_token(),
                new ServerMessageSource(), new LocalMessageSource());
        mChatManagerPresent.onCreate();
    }

    @Override
    public void reset() {
        mChatManagerPresent.onDestroy();
    }
}
