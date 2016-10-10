package com.caij.emore.dao;

import com.caij.emore.database.bean.Draft;

import java.util.List;

/**
 * Created by Caij on 2016/7/20.
 */
public interface DraftManager{

    public void insertDraft(Draft draft);

    public List<Draft> getDrafts(final long maxTime, final int pageCount, final int pageIndex);

    public long getDraftsCount();

    public void deleteDraftById(long id);

}
