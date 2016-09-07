package com.caij.emore.ui.view;

import com.caij.emore.bean.MessageUser;
import com.caij.emore.database.bean.UnReadMessage;

import java.util.List;

/**
 * Created by Caij on 2016/7/23.
 */
public interface MessageUserView extends RefreshListView<MessageUser.UserListBean> {
    void onLoadUnReadMessageSuccess(UnReadMessage unReadMessage);

    void notifyItemRemove(List<MessageUser.UserListBean> mUserListBeens, int position);
}
