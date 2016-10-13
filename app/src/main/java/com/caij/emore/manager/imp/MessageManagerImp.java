package com.caij.emore.manager.imp;

import android.text.TextUtils;

import com.caij.emore.bean.MessageAttachInfo;
import com.caij.emore.manager.MessageManager;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.dao.DirectMessageDao;
import com.caij.emore.database.dao.UserDao;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.db.DBManager;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Caij on 2016/10/9.
 */

public class MessageManagerImp implements MessageManager {

    private DirectMessageDao mDirectMessageDao;
    private UserDao mUserDao;

    public MessageManagerImp (){
        mDirectMessageDao = DBManager.getDaoSession().getDirectMessageDao();
        mUserDao = DBManager.getDaoSession().getUserDao();
    }

    @Override
    public List<DirectMessage> getUserMessage(long toUid, long selfUid, long since_id, long max_id, int count, int page) {
        DirectMessage sinceMessage = mDirectMessageDao.load(since_id);
        Date sinceCreateTime = null;
        if (sinceMessage != null) {
            sinceCreateTime = sinceMessage.getCreated_at();
        }

        DirectMessage maxMessage = mDirectMessageDao.load(max_id);
        Date maxCreateTime = null;
        if (maxMessage != null) {
            maxCreateTime = maxMessage.getCreated_at();
        }
        QueryBuilder<DirectMessage> queryBuilder =  mDirectMessageDao.queryBuilder();
        if(sinceCreateTime != null && maxCreateTime != null) {
            queryBuilder.where(queryBuilder.and(DirectMessageDao.Properties.Created_at.lt(maxCreateTime),
                    DirectMessageDao.Properties.Created_at.ge(sinceCreateTime)));
        }else if (maxCreateTime != null) {
            queryBuilder.where(DirectMessageDao.Properties.Created_at.lt(maxCreateTime));
        }else if (sinceCreateTime != null) {
            queryBuilder.where(DirectMessageDao.Properties.Created_at.ge(sinceCreateTime));
        }
        queryBuilder.where(queryBuilder.or(queryBuilder.and(DirectMessageDao.Properties.Recipient_id.eq(selfUid),
                DirectMessageDao.Properties.Sender_id.eq(toUid)),
                queryBuilder.and(DirectMessageDao.Properties.Sender_id.eq(selfUid),
                        DirectMessageDao.Properties.Recipient_id.eq(toUid))));

        List<DirectMessage> messages = queryBuilder.limit(count).offset(page - 1)
                .orderDesc(DirectMessageDao.Properties.Created_at).list();

        for (DirectMessage message : messages) {
            selectMessageForOther(message);
        }
        return messages;
    }

    private void selectMessageForOther(DirectMessage message) {
        if (!TextUtils.isEmpty(message.getAtt_ids_json())) {
            List<Long> att_ids  = GsonUtils.fromJson(message.getAtt_ids_json(),
                    new TypeToken<List<Long>>(){}.getType());
            message.setAtt_ids(att_ids);
        }

        User sender = mUserDao.load(message.getSender_id());
        User recipien = mUserDao.load(message.getRecipient_id());

        message.setSender(sender);
        message.setRecipient(recipien);

//                        Geo geo = mGeoDao.load(String.valueOf(message.getId()));
//                        message.setGeo(geo);

        if (!TextUtils.isEmpty(message.getAtt_infos_json())) {
            List<MessageAttachInfo> attachInfos  = GsonUtils.fromJson(message.getAtt_infos_json(),
                    new TypeToken<List<MessageAttachInfo>>(){}.getType());
            message.setAttachinfo(attachInfos);
        }
    }

    @Override
    public void saveMessage(DirectMessage message) {
        if (message.getAtt_ids() != null) {
            message.setAtt_ids_json(GsonUtils.toJson(message.getAtt_ids()));
        }
        if (message.getRecipient() != null) {
            mUserDao.insertOrReplace(message.getRecipient());
        }
        if (message.getSender() != null) {
            mUserDao.insertOrReplace(message.getSender());
        }
//        Geo geo = message.getGeo();
//        if (geo != null) {
//            geo.setId(String.valueOf(message.getId()));
//            mGeoDao.insertOrReplace(geo);
//        }

        if (message.getAttachinfo() != null) {
            message.setAtt_infos_json(GsonUtils.toJson(message.getAttachinfo()));
        }
        mDirectMessageDao.insertOrReplace(message);
    }

    @Override
    public void deleteMessageById(long id) {
        mDirectMessageDao.deleteByKey(id);
    }

    @Override
    public DirectMessage getMessageById(long id) {
        DirectMessage message = mDirectMessageDao.load(id);
        if (message != null) {
            selectMessageForOther(message);
            return message;
        }
        return null;
    }
}
