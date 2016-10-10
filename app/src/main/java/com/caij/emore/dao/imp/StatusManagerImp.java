package com.caij.emore.dao.imp;

import android.database.Cursor;
import android.text.TextUtils;

import com.caij.emore.bean.PageInfo;
import com.caij.emore.bean.ShortUrl;
import com.caij.emore.bean.WeiboImageInfo;
import com.caij.emore.bean.weibo.Button;
import com.caij.emore.bean.weibo.Title;
import com.caij.emore.dao.StatusManager;
import com.caij.emore.database.bean.Geo;
import com.caij.emore.database.bean.LongText;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.bean.Visible;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.database.dao.UserDao;
import com.caij.emore.database.dao.WeiboDao;
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
    private final WeiboDao weiboDao;

    public StatusManagerImp() {
        userDao = DBManager.getDaoSession().getUserDao();
        weiboDao = DBManager.getDaoSession().getWeiboDao();
    }


    @Override
    public List<Weibo> getFriendWeibo(long uid, long since_id, long max_id, int count, int page) {
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

        Weibo sinceWeibo = weiboDao.load(since_id);
        Date sinceCreateTime = null;
        if (sinceWeibo != null) {
            sinceCreateTime = sinceWeibo.getCreated_at();
        }

        Weibo maxFriendWeibo = weiboDao.load(max_id);
        Date maxCreateTime = null;
        if (maxFriendWeibo != null) {
            maxCreateTime = maxFriendWeibo.getCreated_at();
        }

        QueryBuilder<Weibo> queryBuilder = weiboDao.queryBuilder();
        if(sinceCreateTime != null && maxCreateTime != null) {
            queryBuilder.where(queryBuilder.and(WeiboDao.Properties.Created_at.lt(maxCreateTime),
                    WeiboDao.Properties.Created_at.gt(sinceCreateTime)));
        }else if (maxCreateTime != null) {
            queryBuilder.where(WeiboDao.Properties.Created_at.lt(maxCreateTime));
        }else if (sinceCreateTime != null) {
            queryBuilder.where(WeiboDao.Properties.Created_at.ge(sinceCreateTime));
        }

        if (followerUserIds.size() == 0) {
            followerUserIds.add(0L);
        }

        queryBuilder.where(WeiboDao.Properties.User_id.in(followerUserIds));
        List<Weibo> friendWeibos = queryBuilder.limit(count).offset(page - 1)
                .orderDesc(WeiboDao.Properties.Created_at).list();
        for (Weibo weibo : friendWeibos) {
            selectWeibo(weibo);
        }
        return friendWeibos;
    }

    @Override
    public void saveWeibos(List<Weibo> weibos) {
        for (Weibo weibo : weibos) {
            insertWeibo(weibo);
        }
    }

    @Override
    public void saveWeibo(Weibo weibo) {
        insertWeibo(weibo);
    }

    @Override
    public void deleteWeibo(long id) {
        weiboDao.deleteByKey(id);
    }

    @Override
    public Weibo getWeiboById(long id) {
        Weibo weibo = weiboDao.load(id);
        if (weibo != null) {
            selectWeibo(weibo);
        }
        return weibo;
    }

    private void selectWeibo(Weibo weibo) {
        if (weibo.getUser_id() != null && weibo.getUser_id() > 0) { //如果user 为null  表示微博被删除了
            weibo.setUser(userDao.load(weibo.getUser_id()));
            if (!TextUtils.isEmpty(weibo.getGeo_json_string())) {
                weibo.setGeo(GsonUtils.fromJson(weibo.getGeo_json_string(), Geo.class));
            }

            if (!TextUtils.isEmpty(weibo.getVisible_json_string())) {
                weibo.setVisible(GsonUtils.fromJson(weibo.getVisible_json_string(), Visible.class));
            }

            if (!TextUtils.isEmpty(weibo.getPic_ids_json_string())) {
                List<String> picIds = GsonUtils.fromJson(weibo.getPic_ids_json_string(),
                        new TypeToken<List<String>>() {
                        }.getType());
                weibo.setPic_ids(picIds);
            }

            if (!TextUtils.isEmpty(weibo.getPic_infos_json_string())) {
                LinkedHashMap<String, WeiboImageInfo> pic_infos = GsonUtils.fromJson(weibo.getPic_infos_json_string(),
                        new TypeToken<LinkedHashMap<String, WeiboImageInfo>>(){}.getType());
                weibo.setPic_infos(pic_infos);
            }

            if (weibo.getIsLongText() != null && weibo.getIsLongText()
                    && !TextUtils.isEmpty(weibo.getLong_text_json_string())) {
                LongText longText = GsonUtils.fromJson(weibo.getLong_text_json_string(), LongText.class);
                weibo.setLongText(longText);
            }

            if (!TextUtils.isEmpty(weibo.getUrl_struct_json_string())) {
                List<ShortUrl> shortUrls = GsonUtils.fromJson(weibo.getUrl_struct_json_string(),
                        new TypeToken<List<ShortUrl>>() {
                        }.getType());
                weibo.setUrl_struct(shortUrls);
            }

            if (!TextUtils.isEmpty(weibo.getPage_info_json_string())) {
                PageInfo pageInfo = GsonUtils.fromJson(weibo.getPage_info_json_string(), PageInfo.class);
                weibo.setPage_info(pageInfo);
            }

            if (weibo.getRetweeted_status_id() != null && weibo.getRetweeted_status_id() > 0) {
                Weibo repostWeibo = weiboDao.load(weibo.getRetweeted_status_id());
                selectWeibo(repostWeibo);
                weibo.setRetweeted_status(repostWeibo);
            }

            if (!TextUtils.isEmpty(weibo.getTitle_json_string())) {
                Title title = GsonUtils.fromJson(weibo.getTitle_json_string(), Title.class);
                weibo.setTitle(title);
            }

            if (!TextUtils.isEmpty(weibo.getButtons_json_string())) {
                List<Button> buttons = GsonUtils.fromJson(weibo.getButtons_json_string(),
                        new TypeToken<List<Button>>() {
                        }.getType());
                weibo.setButtons(buttons);
            }
        }
    }

    private void insertWeibo(Weibo weibo) {
        String weiboId  = String.valueOf(weibo.getId());
        Geo geo = weibo.getGeo();
        if (geo != null) {
            weibo.setGeo_json_string(GsonUtils.toJson(geo));
        }

        Visible visible = weibo.getVisible();
        if (visible != null) {
            weibo.setVisible_json_string(GsonUtils.toJson(visible));
        }

        User user = weibo.getUser();
        if (user != null) {
            weibo.setUser_id(user.getId());
            userDao.insertOrReplace(user);
        }else {
            weibo.setUser_id(-1L);
        }

        List<String> picIds = weibo.getPic_ids();
        if (picIds != null && picIds.size() > 0) {
            weibo.setPic_ids_json_string(GsonUtils.toJson(picIds));
        }

        LinkedHashMap<String, WeiboImageInfo> pic_infos = weibo.getPic_infos();
        if (pic_infos != null && pic_infos.size() > 0) {
            weibo.setPic_infos_json_string(GsonUtils.toJson(pic_infos));
        }

        LongText longText =  weibo.getLongText();
        if (weibo.getIsLongText() != null && weibo.getIsLongText() && longText != null
                && !TextUtils.isEmpty(longText.getContent())) {
            weibo.setLong_text_json_string(GsonUtils.toJson(longText));
        }

        List<ShortUrl> urlInfos = weibo.getUrl_struct();
        if (urlInfos != null && urlInfos.size() > 0) {
            weibo.setUrl_struct_json_string(GsonUtils.toJson(urlInfos));
        }

        List<Button> buttons = weibo.getButtons();
        if (buttons != null && buttons.size() > 0) {
            weibo.setUrl_struct_json_string(GsonUtils.toJson(buttons));
        }


        Title title = weibo.getTitle();
        if (title != null) {
            weibo.setTitle_json_string(GsonUtils.toJson(title));
        }


        PageInfo pageInfo = weibo.getPage_info();
        if (pageInfo != null) {
            weibo.setPage_info_json_string(GsonUtils.toJson(pageInfo));
        }

        if (weibo.getRetweeted_status() != null) {
            weibo.setRetweeted_status_id(weibo.getRetweeted_status().getId());
            insertWeibo(weibo.getRetweeted_status());
        }else {
            weibo.setRetweeted_status_id(-1L);
        }
        weiboDao.insertOrReplace(weibo);
    }
}
