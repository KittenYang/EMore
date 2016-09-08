package com.caij.emore.present;

import com.caij.emore.bean.MessageUser;

/**
 * Created by Caij on 2016/7/10.
 */
public interface MessageUserPresent extends RefreshListPresent {
    void deleteMessageConversation(MessageUser.UserListBean id);
}
