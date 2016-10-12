package com.caij.emore.manager;

import com.caij.emore.database.bean.User;

/**
 * Created by Caij on 2016/10/9.
 */

public interface UserManager {

    public User getUserByName(String name);

    public User getUserByUid(long uid);

    public void saveUser(final User user);
}
