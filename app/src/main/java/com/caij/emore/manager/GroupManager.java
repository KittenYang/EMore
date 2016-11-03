package com.caij.emore.manager;

import com.caij.emore.database.bean.Group;

import java.util.List;

/**
 * Created by Caij on 2016/11/2.
 */

public interface GroupManager  {

    List<Group> getGroups();

    void saveGroups(List<Group> groups);

    void clear();
}
