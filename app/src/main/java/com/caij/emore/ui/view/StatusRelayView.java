package com.caij.emore.ui.view;


import com.caij.emore.database.bean.Status;

import java.util.List;


/**
 * Created by Caij on 2016/5/31.
 */
public interface StatusRelayView extends ListView<Status> {

    void onRelayStatusSuccess(List<Status> weobos);
}
