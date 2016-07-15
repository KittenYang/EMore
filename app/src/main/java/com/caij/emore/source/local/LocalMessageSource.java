package com.caij.emore.source.local;

import android.text.TextUtils;

import com.caij.emore.UserPrefs;
import com.caij.emore.bean.MessageUser;
import com.caij.emore.bean.UnreadMessageCount;
import com.caij.emore.bean.response.UserMessageResponse;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.database.bean.Geo;
import com.caij.emore.database.bean.MessageImage;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.dao.DirectMessageDao;
import com.caij.emore.database.dao.GeoDao;
import com.caij.emore.database.dao.MessageImageDao;
import com.caij.emore.database.dao.UserDao;
import com.caij.emore.database.dao.WeiboDao;
import com.caij.emore.source.MessageSource;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.db.DBManager;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Caij on 2016/7/14.
 */
public class LocalMessageSource implements MessageSource {

    private DirectMessageDao mDirectMessageDao;
    private UserDao mUserDao;
    private GeoDao mGeoDao;

    public LocalMessageSource (){
        mDirectMessageDao = DBManager.getDaoSession().getDirectMessageDao();
        mUserDao = DBManager.getDaoSession().getUserDao();
        mGeoDao = DBManager.getDaoSession().getGeoDao();
    }

    @Override
    public Observable<UnreadMessageCount> getUnReadMessage(String accessToken, long uid) {
        return null;
    }

    @Override
    public Observable<MessageUser> getMessageUserList(String accessToken, int count, long cursor) {

        return null;
    }

    @Override
    public Observable<UserMessageResponse> getUserMessage(String accessToken, final long uid,
                                                          final long since_id, final long max_id,
                                                          final int count, final int page) {
        return Observable.create(new Observable.OnSubscribe<UserMessageResponse>() {
            @Override
            public void call(Subscriber<? super UserMessageResponse> subscriber) {
                try {
                    DirectMessage sinceMessage = mDirectMessageDao.load(since_id);
                    long sinceCreateTime = 0;
                    if (sinceMessage != null) {
                        sinceCreateTime = sinceMessage.getCreated_at_long();
                    }

                    DirectMessage maxMessage = mDirectMessageDao.load(max_id);
                    long maxCreateTime = 0;
                    if (maxMessage != null) {
                        maxCreateTime = maxMessage.getCreated_at_long();
                    }
                    QueryBuilder<DirectMessage> queryBuilder =  mDirectMessageDao.queryBuilder();
                    if(sinceCreateTime != 0 && maxCreateTime != 0) {
                        queryBuilder.where(queryBuilder.and(DirectMessageDao.Properties.Created_at_long.lt(maxCreateTime),
                                DirectMessageDao.Properties.Created_at_long.ge(sinceCreateTime)));
                    }else if (maxCreateTime != 0) {
                        queryBuilder.where(DirectMessageDao.Properties.Created_at_long.lt(maxCreateTime));
                    }else if (sinceCreateTime != 0) {
                        queryBuilder.where(DirectMessageDao.Properties.Created_at_long.ge(sinceCreateTime));
                    }
                    long selfUid  = Long.parseLong(UserPrefs.get().getAccount().getWeiyoToken().getUid());
                    queryBuilder.where(queryBuilder.or(queryBuilder.and(DirectMessageDao.Properties.Recipient_id.eq(selfUid),
                            DirectMessageDao.Properties.Sender_id.eq(uid)),
                            queryBuilder.and(DirectMessageDao.Properties.Sender_id.eq(selfUid),
                            DirectMessageDao.Properties.Recipient_id.eq(uid))));

                    List<DirectMessage> messages = queryBuilder.limit(count).offset(page - 1)
                            .orderDesc(DirectMessageDao.Properties.Created_at_long).list();

                    for (DirectMessage message : messages) {
                        if (!TextUtils.isEmpty(message.getAtt_ids_json())) {
                            List<Long> att_ids  = GsonUtils.fromJson(message.getAtt_ids_json(),
                                    new TypeToken<List<Long>>(){}.getType());
                            message.setAtt_ids(att_ids);
                        }

                        User sender = mUserDao.load(message.getSender_id());
                        User recipien = mUserDao.load(message.getRecipient_id());

                        message.setSender(sender);
                        message.setRecipient(recipien);

                        Geo geo = mGeoDao.load(String.valueOf(message.getId()));
                        message.setGeo(geo);
                    }

                    UserMessageResponse response = new UserMessageResponse();
                    response.setDirect_messages(messages);
                    subscriber.onNext(response);
                    subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<DirectMessage> createTextMessage(String accessToken, String text, long uid) {
        return null;
    }

    @Override
    public Observable<DirectMessage> createImageMessage(String accessToken, Map<String, Object> paramMap, String text, String imagePath, long uid, String screenName) {
        return null;
    }

    @Override
    public void saveMessage(DirectMessage message) {
        mDirectMessageDao.getDatabase().beginTransaction();
        if (message.getAtt_ids() != null) {
            message.setAtt_ids_json(GsonUtils.toJson(message.getAtt_ids()));
        }
        message.setCreated_at_long(DateUtil.parseCreateTime(message.getCreated_at()));
        if (message.getRecipient() != null) {
            mUserDao.insertOrReplace(message.getRecipient());
        }
        if (message.getSender() != null) {
            mUserDao.insertOrReplace(message.getSender());
        }
        Geo geo = message.getGeo();
        if (geo != null) {
            geo.setId(String.valueOf(message.getId()));
            mGeoDao.insertOrReplace(geo);
        }
        mDirectMessageDao.insertOrReplace(message);
        mDirectMessageDao.getDatabase().setTransactionSuccessful();
        mDirectMessageDao.getDatabase().endTransaction();
    }

    @Override
    public Observable<MessageImage> getMessageImageInfo(String accessToken, final long fid) {
        return Observable.create(new Observable.OnSubscribe<MessageImage>() {
            @Override
            public void call(Subscriber<? super MessageImage> subscriber) {
                try {
                    MessageImageDao messageImageDao = DBManager.getDaoSession().getMessageImageDao();
                    MessageImage messageImage = messageImageDao.load(fid);
                    subscriber.onNext(messageImage);
                    subscriber.onCompleted();
                }catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public void saveMessageImage(MessageImage messageImage) {
        DBManager.getDaoSession().getMessageImageDao().insertOrReplace(messageImage);
    }

    @Override
    public void removeMessage(DirectMessage bean) {
        mDirectMessageDao.deleteByKey(bean.getId());
    }
}
