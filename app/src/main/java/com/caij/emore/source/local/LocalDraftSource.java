package com.caij.emore.source.local;

import com.caij.emore.database.bean.Draft;
import com.caij.emore.database.dao.DraftDao;
import com.caij.emore.source.DraftSource;
import com.caij.emore.utils.db.DBManager;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Caij on 2016/7/20.
 */
public class LocalDraftSource implements DraftSource {
    @Override
    public void saveDraft(Draft draft) {
        DBManager.getDaoSession().getDraftDao().insertOrReplace(draft);
    }

    @Override
    public Observable<List<Draft>> getDrafts(final long maxTime, final int pageCount, final int pageIndex) {
        return Observable.create(new Observable.OnSubscribe<List<Draft>>() {
            @Override
            public void call(Subscriber<? super List<Draft>> subscriber) {
                try {
                    DraftDao draftDao = DBManager.getDaoSession().getDraftDao();
                    DraftDao.Properties.Create_at.lt(maxTime);
                    QueryBuilder<Draft> queryBuilder = draftDao.queryBuilder();

                    WhereCondition conditionStauts = queryBuilder.or(DraftDao.Properties.Status.eq(Draft.STATUS_SAVE),
                            DraftDao.Properties.Status.eq(Draft.STATUS_FAIL));
                    long time = System.currentTimeMillis() - 5 * 60 * 1000L; //这种表示发送时间太长 可能是杀进程导致 把他归为发送失败。5分钟
                    WhereCondition conditionOther = queryBuilder.and(DraftDao.Properties.Status.eq(Draft.STATUS_SENDING),
                            DraftDao.Properties.Create_at.lt(time));
                    List<Draft> drafts = queryBuilder.where(queryBuilder.or(conditionStauts, conditionOther))
                            .limit(pageCount).offset(pageIndex - 1)
                            .orderDesc(DraftDao.Properties.Create_at).list();
                    subscriber.onNext(drafts);
                    subscriber.onCompleted();
                }catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public void deleteDraft(Draft draft) {
        DBManager.getDaoSession().getDraftDao().delete(draft);
    }

    @Override
    public void deleteDraftById(long id) {
        DBManager.getDaoSession().getDraftDao().deleteByKey(id);
    }
}
