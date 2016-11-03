package com.caij.emore.ui.view;

import com.caij.emore.database.bean.Group;

import java.util.List;

/**
 * Created by Caij on 2016/11/2.
 */

public interface StatusContainerView extends BaseView {
    void onGetGroups(List<Group> groups);
}
