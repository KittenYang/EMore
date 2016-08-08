package com.caij.emore.present;

import com.caij.emore.database.bean.Draft;

/**
 * Created by Caij on 2016/7/20.
 */
public interface DraftPresent extends ListPresent {
    void publishDraft(Draft draft);

    void deleteDraft(Draft draft, int position);
}
