package com.caij.emore.manager.imp;

import com.caij.emore.manager.NotifyManager;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.dao.UnReadMessageDao;
import com.caij.emore.utils.db.DBManager;

/**
 * Created by Caij on 2016/10/9.
 */

public class NotifyManagerImp implements NotifyManager {

    private UnReadMessageDao mUnReadMessageDao;

    public NotifyManagerImp() {
        mUnReadMessageDao = DBManager.getDaoSession().getUnReadMessageDao();
    }

    @Override
    public UnReadMessage getUnReadMessage(long uid) {
        return  mUnReadMessageDao.load(uid);
    }

    @Override
    public void resetUnReadMessage(long uid, String type, int value) {
        UnReadMessage unReadMessage = mUnReadMessageDao.load(uid);
        if (unReadMessage != null) {
            if (type.equals(UnReadMessage.TYPE_MENTION_STATUS)) {
                unReadMessage.setMention_status(value);
            }else if (type.equals(UnReadMessage.TYPE_MENTION_CMT)) {
                unReadMessage.setMention_cmt(value);
            }else if (type.equals(UnReadMessage.TYPE_CMT)) {
                unReadMessage.setCmt(value);
            }else if (type.equals(UnReadMessage.TYPE_STATUS)) {
                unReadMessage.setStatus(value);
            }else if (type.equals(UnReadMessage.TYPE_DM)) {
                unReadMessage.setDm_single(value);
            }else if (type.equals(UnReadMessage.TYPE_ATTITUDE)) {
                unReadMessage.setAttitude(value);
            }else if (type.equals(UnReadMessage.TYPE_FOLLOWER)) {
                unReadMessage.setFollower(value);
            }
            mUnReadMessageDao.insertOrReplace(unReadMessage);
        }
    }

    @Override
    public void saveUnReadMessage(UnReadMessage unReadMessage) {
        mUnReadMessageDao.insertOrReplace(unReadMessage);
    }
}
