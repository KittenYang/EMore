package com.caij.emore.manager.imp;

import com.caij.emore.manager.DraftManager;
import com.caij.emore.database.bean.Draft;
import com.caij.emore.database.dao.DraftDao;
import com.caij.emore.utils.db.DBManager;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

/**
 * Created by Caij on 2016/10/9.
 */

public class DraftManagerImp implements DraftManager {

    public static final long FAIL_TIME = 5 * 60 * 1000L;

    private DraftDao mDraftDao;

    public DraftManagerImp() {
        mDraftDao = DBManager.getDaoSession().getDraftDao();
    }

    @Override
    public void insertDraft(Draft draft) {
        mDraftDao.insertOrReplace(draft);
    }

    @Override
    public List<Draft> getDrafts(long maxTime, int pageCount, int pageIndex) {
        DraftDao draftDao = DBManager.getDaoSession().getDraftDao();
        QueryBuilder<Draft> queryBuilder = draftDao.queryBuilder();

        WhereCondition conditionStatus = queryBuilder.or(DraftDao.Properties.Status.eq(Draft.STATUS_SAVE),
                DraftDao.Properties.Status.eq(Draft.STATUS_FAIL));
        long time = System.currentTimeMillis() - FAIL_TIME; //这种表示发送时间太长 可能是杀进程导致 把他归为发送失败。5分钟
        WhereCondition conditionOther = queryBuilder.and(DraftDao.Properties.Status.eq(Draft.STATUS_SENDING),
                DraftDao.Properties.Create_at.lt(time));
        return queryBuilder.where(queryBuilder.or(conditionStatus, conditionOther))
                .limit(pageCount).offset(pageIndex - 1)
                .orderDesc(DraftDao.Properties.Create_at).list();
    }

    @Override
    public long getDraftsCount() {
        DraftDao draftDao = DBManager.getDaoSession().getDraftDao();
        QueryBuilder<Draft> queryBuilder = draftDao.queryBuilder();
        WhereCondition conditionStauts = queryBuilder.or(DraftDao.Properties.Status.eq(Draft.STATUS_SAVE),
                DraftDao.Properties.Status.eq(Draft.STATUS_FAIL));
        long time = System.currentTimeMillis() - FAIL_TIME; //这种表示发送时间太长 可能是杀进程导致 把他归为发送失败。5分钟
        WhereCondition conditionOther = queryBuilder.and(DraftDao.Properties.Status.eq(Draft.STATUS_SENDING),
                DraftDao.Properties.Create_at.lt(time));
        return queryBuilder.where(queryBuilder.or(conditionStauts, conditionOther)).count();
    }

    @Override
    public void deleteDraftById(long id) {
        mDraftDao.deleteByKey(id);
    }
}
