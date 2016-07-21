package com.caij.emore.source;

import com.caij.emore.database.bean.Draft;

import java.util.List;

import rx.Observable;

/**
 * Created by Caij on 2016/7/20.
 */
public interface DraftSource {

    public void saveDraft(Draft draft);

    Observable<List<Draft>> getDrafts(long maxTime, int pageCount, int pageSize);

    void deleteDraft(Draft draft);

    void deleteDraftById(long id);
}
