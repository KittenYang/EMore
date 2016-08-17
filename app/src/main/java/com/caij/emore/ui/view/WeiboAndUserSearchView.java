package com.caij.emore.ui.view;

import com.caij.emore.database.bean.User;

import java.util.List;

/**
 * Created by Caij on 2016/7/26.
 */
public interface WeiboAndUserSearchView extends TimeLineWeiboView {
    void setUsers(List<User> users);
}
