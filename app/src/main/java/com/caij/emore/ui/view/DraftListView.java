package com.caij.emore.ui.view;

import com.caij.emore.database.bean.Draft;

/**
 * Created by Caij on 2016/7/22.
 */
public interface DraftListView extends ListView<Draft> {
    void onDraftUpdate(Draft draft);
}
