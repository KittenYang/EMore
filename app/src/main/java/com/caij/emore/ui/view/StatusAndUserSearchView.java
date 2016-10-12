package com.caij.emore.ui.view;

import com.caij.emore.database.bean.User;

import java.util.List;

/**
 * Created by Caij on 2016/7/26.
 */
public interface StatusAndUserSearchView extends TimeLineStatusView {
    void setUsers(List<User> users);
}
