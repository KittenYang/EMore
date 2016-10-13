package com.caij.emore.manager.imp;

import android.database.Cursor;
import android.text.TextUtils;

import com.caij.emore.bean.PageInfo;
import com.caij.emore.bean.ShortUrl;
import com.caij.emore.bean.StatusImageInfo;
import com.caij.emore.bean.weibo.Button;
import com.caij.emore.bean.weibo.Title;
import com.caij.emore.manager.StatusManager;
import com.caij.emore.database.bean.Geo;
import com.caij.emore.database.bean.LongText;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.bean.Visible;
import com.caij.emore.database.bean.Status;
import com.caij.emore.database.dao.StatusDao;
import com.caij.emore.database.dao.UserDao;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.db.DBManager;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Caij on 2016/10/9.
 */

public class StatusManagerImp implements StatusManager {

    private final UserDao userDao;
    private final StatusDao mStatusDao;

    public StatusManagerImp() {
        userDao = DBManager.getDaoSession().getUserDao();
        mStatusDao = DBManager.getDaoSession().getStatusDao();
    }


    @Override
    public List<Status> getFriendStatuses(long uid, long since_id, long max_id, int count, int page) {
        Cursor cursor = userDao.queryBuilder().where(UserDao.Properties.Following.eq(true))
                .buildCursor().query();
        List<Long> followerUserIds = new ArrayList<Long>();
        try {
            int idColumn = cursor.getColumnIndex(UserDao.Properties.Id.columnName);
            while (cursor.moveToNext()) {
                long followerUid = cursor.getLong(idColumn);
                followerUserIds.add(followerUid);
            }
        }finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        //添加自己
        followerUserIds.add(uid);

        Status sinceStatus = mStatusDao.load(since_id);
        Date sinceCreateTime = null;
        if (sinceStatus != null) {
            sinceCreateTime = sinceStatus.getCreated_at();
        }

        Status maxStatus = mStatusDao.load(max_id);
        Date maxCreateTime = null;
        if (maxStatus != null) {
            maxCreateTime = maxStatus.getCreated_at();
        }

        QueryBuilder<Status> queryBuilder = mStatusDao.queryBuilder();
        if(sinceCreateTime != null && maxCreateTime != null) {
            queryBuilder.where(queryBuilder.and(StatusDao.Properties.Created_at.lt(maxCreateTime),
                    StatusDao.Properties.Created_at.gt(sinceCreateTime)));
        }else if (maxCreateTime != null) {
            queryBuilder.where(StatusDao.Properties.Created_at.lt(maxCreateTime));
        }else if (sinceCreateTime != null) {
            queryBuilder.where(StatusDao.Properties.Created_at.ge(sinceCreateTime));
        }

        if (followerUserIds.size() == 0) {
            followerUserIds.add(0L);
        }

        queryBuilder.where(StatusDao.Properties.User_id.in(followerUserIds));
        List<Status> friendStatuses = queryBuilder.limit(count).offset(page - 1)
                .orderDesc(StatusDao.Properties.Created_at).list();
        for (Status status : friendStatuses) {
            selectStatus(status);
        }
        return friendStatuses;
    }

    @Override
    public void saveStatuses(List<Status> statuses) {
        for (Status status : statuses) {
            insertStatus(status);
        }
    }

    @Override
    public void saveStatus(Status status) {
        insertStatus(status);
    }

    @Override
    public void deleteStatus(long id) {
        mStatusDao.deleteByKey(id);
    }

    @Override
    public Status getStatusById(long id) {
        Status status = mStatusDao.load(id);
        if (status != null) {
            selectStatus(status);
        }
        return status;
    }

    private void selectStatus(Status status) {
        if (status.getUser_id() != null && status.getUser_id() > 0) { //如果user 为null  表示微博被删除了
            status.setUser(userDao.load(status.getUser_id()));
            if (!TextUtils.isEmpty(status.getGeo_json_string())) {
                status.setGeo(GsonUtils.fromJson(status.getGeo_json_string(), Geo.class));
            }

            if (!TextUtils.isEmpty(status.getVisible_json_string())) {
                status.setVisible(GsonUtils.fromJson(status.getVisible_json_string(), Visible.class));
            }

            if (!TextUtils.isEmpty(status.getPic_ids_json_string())) {
                List<String> picIds = GsonUtils.fromJson(status.getPic_ids_json_string(),
                        new TypeToken<List<String>>() {
                        }.getType());
                status.setPic_ids(picIds);
            }

            if (!TextUtils.isEmpty(status.getPic_infos_json_string())) {
                LinkedHashMap<String, StatusImageInfo> pic_infos = GsonUtils.fromJson(status.getPic_infos_json_string(),
                        new TypeToken<LinkedHashMap<String, StatusImageInfo>>(){}.getType());
                status.setPic_infos(pic_infos);
            }

            if (status.getIsLongText() != null && status.getIsLongText()
                    && !TextUtils.isEmpty(status.getLong_text_json_string())) {
                LongText longText = GsonUtils.fromJson(status.getLong_text_json_string(), LongText.class);
                status.setLongText(longText);
            }

            if (!TextUtils.isEmpty(status.getUrl_struct_json_string())) {
                List<ShortUrl> shortUrls = GsonUtils.fromJson(status.getUrl_struct_json_string(),
                        new TypeToken<List<ShortUrl>>() {
                        }.getType());
                status.setUrl_struct(shortUrls);
            }

            if (!TextUtils.isEmpty(status.getPage_info_json_string())) {
                PageInfo pageInfo = GsonUtils.fromJson(status.getPage_info_json_string(), PageInfo.class);
                status.setPage_info(pageInfo);
            }

            if (status.getRetweeted_status_id() != null && status.getRetweeted_status_id() > 0) {
                Status forwardStatus = mStatusDao.load(status.getRetweeted_status_id());
                selectStatus(forwardStatus);
                status.setRetweeted_status(forwardStatus);
            }

            if (!TextUtils.isEmpty(status.getTitle_json_string())) {
                Title title = GsonUtils.fromJson(status.getTitle_json_string(), Title.class);
                status.setTitle(title);
            }

            if (!TextUtils.isEmpty(status.getButtons_json_string())) {
                List<Button> buttons = GsonUtils.fromJson(status.getButtons_json_string(),
                        new TypeToken<List<Button>>() {
                        }.getType());
                status.setButtons(buttons);
            }
        }
    }

    private void insertStatus(Status status) {
        Geo geo = status.getGeo();
        if (geo != null) {
            status.setGeo_json_string(GsonUtils.toJson(geo));
        }

        Visible visible = status.getVisible();
        if (visible != null) {
            status.setVisible_json_string(GsonUtils.toJson(visible));
        }

        User user = status.getUser();
        if (user != null) {
            status.setUser_id(user.getId());
            userDao.insertOrReplace(user);
        }else {
            status.setUser_id(-1L);
        }

        List<String> picIds = status.getPic_ids();
        if (picIds != null && picIds.size() > 0) {
            status.setPic_ids_json_string(GsonUtils.toJson(picIds));
        }

        LinkedHashMap<String, StatusImageInfo> pic_infos = status.getPic_infos();
        if (pic_infos != null && pic_infos.size() > 0) {
            status.setPic_infos_json_string(GsonUtils.toJson(pic_infos));
        }

        LongText longText =  status.getLongText();
        if (status.getIsLongText() != null && status.getIsLongText() && longText != null
                && !TextUtils.isEmpty(longText.getContent())) {
            status.setLong_text_json_string(GsonUtils.toJson(longText));
        }

        List<ShortUrl> shortUrls = status.getUrl_struct();
        if (shortUrls != null && shortUrls.size() > 0) {
            status.setUrl_struct_json_string(GsonUtils.toJson(shortUrls));
        }

        List<Button> buttons = status.getButtons();
        if (buttons != null && buttons.size() > 0) {
            status.setUrl_struct_json_string(GsonUtils.toJson(buttons));
        }


        Title title = status.getTitle();
        if (title != null) {
            status.setTitle_json_string(GsonUtils.toJson(title));
        }


        PageInfo pageInfo = status.getPage_info();
        if (pageInfo != null) {
            status.setPage_info_json_string(GsonUtils.toJson(pageInfo));
        }

        if (status.getRetweeted_status() != null) {
            status.setRetweeted_status_id(status.getRetweeted_status().getId());
            insertStatus(status.getRetweeted_status());
        }else {
            status.setRetweeted_status_id(-1L);
        }
        mStatusDao.insertOrReplace(status);
    }
}
