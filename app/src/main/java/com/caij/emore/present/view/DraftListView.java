package com.caij.emore.present.view;

import com.caij.emore.database.bean.Draft;

/**
 * Created by Caij on 2016/7/22.
 */
public interface DraftListView extends BaseListView<Draft> {
    void onDraftUpdate(Draft draft);
}
