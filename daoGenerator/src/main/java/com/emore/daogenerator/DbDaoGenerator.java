package com.emore.daogenerator;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DbDaoGenerator {

    private static final int VERSION = 1;

    public static void main(String[] args) throws Exception {
        Schema sch = new Schema(VERSION, "com.caij.emore.database.bean");
        sch.setDefaultJavaPackageDao("com.caij.emore.database.dao");
        sch.enableKeepSectionsByDefault();
        createUser(sch);
        createImage(sch);
        createFile(sch);
        createLikeBean(sch);
        createGeo(sch);
        createWeibo(sch);
        createPic(sch);
        createVisible(sch);
        createDirectMessage(sch);
        createDirectMessageImage(sch);
        createUrlInfo(sch);
        createUploadImageResponse(sch);
        createDraft(sch);
        createUnReadMessage(sch);
        createLongText(sch);
        new de.greenrobot.daogenerator.DaoGenerator().generateAll(sch, "./app/src/main/java");
    }

    static void createUser(Schema sch) {
        Entity entityUser = sch.addEntity("User");
        entityUser.addLongProperty("id").primaryKey();
        entityUser.addStringProperty("idstr");
        entityUser.addStringProperty("screen_name");
        entityUser.addStringProperty("name");
        entityUser.addIntProperty("province");
        entityUser.addIntProperty("city");
        entityUser.addStringProperty("location");
        entityUser.addStringProperty("description");
        entityUser.addStringProperty("url");
        entityUser.addStringProperty("profile_image_url");
        entityUser.addStringProperty("profile_url");
        entityUser.addStringProperty("domain");
        entityUser.addStringProperty("weihao");
        entityUser.addStringProperty("gender");
        entityUser.addIntProperty("followers_count");
        entityUser.addIntProperty("friends_count");
        entityUser.addIntProperty("statuses_count");
        entityUser.addIntProperty("favourites_count");
        entityUser.addStringProperty("created_at");
        entityUser.addBooleanProperty("following");
        entityUser.addBooleanProperty("allow_all_act_msg");
        entityUser.addBooleanProperty("geo_enabled");
        entityUser.addBooleanProperty("verified");
        entityUser.addIntProperty("verified_type");
        entityUser.addStringProperty("remark");
        entityUser.addBooleanProperty("allow_all_comment");
        entityUser.addStringProperty("avatar_large");
        entityUser.addStringProperty("avatar_hd");
        entityUser.addStringProperty("verified_reason");
        entityUser.addBooleanProperty("follow_me");
        entityUser.addIntProperty("online_status");
        entityUser.addIntProperty("bi_followers_count");
        entityUser.addStringProperty("lang");
        entityUser.addLongProperty("fiset_weibo_id");
        entityUser.addStringProperty("cover_image_phone");
        entityUser.addLongProperty("update_time");
    }

    private static void createWeibo(Schema sch) {
        Entity entity = sch.addEntity("Weibo");
        entity.addStringProperty("created_at");
        entity.addLongProperty("id").primaryKey();
        entity.addLongProperty("mid");
        entity.addStringProperty("idstr");
        entity.addStringProperty("text");
        entity.addStringProperty("source");
        entity.addBooleanProperty("favorited");
        entity.addBooleanProperty("truncated");
        entity.addStringProperty("in_reply_to_status_id");
        entity.addStringProperty("in_reply_to_user_id");
        entity.addStringProperty("in_reply_to_screen_name");
        entity.addStringProperty("thumbnail_pic");
        entity.addStringProperty("bmiddle_pic");
        entity.addStringProperty("original_pic");
        entity.addIntProperty("reposts_count");
        entity.addIntProperty("comments_count");
        entity.addIntProperty("attitudes_count");
        entity.addIntProperty("mlevel");
        entity.addLongProperty("update_time");
        entity.addLongProperty("create_at_long");
        entity.addBooleanProperty("isLongText");

        entity.addStringProperty("geo_id");
        entity.addLongProperty("user_id");
        entity.addStringProperty("visible_id");
        entity.addLongProperty("retweeted_status_id");
    }

    static void createPic(Schema sch) {
        Entity image = sch.addEntity("PicUrl");
        image.addStringProperty("id").primaryKey();
        image.addStringProperty("thumbnail_pic");
        image.addLongProperty("weibo_id");
    }

    static void createVisible(Schema sch) {
        Entity image = sch.addEntity("Visible");
        image.addStringProperty("id").primaryKey();
        image.addStringProperty("type");
        image.addStringProperty("list_id");
    }

    static void createGeo(Schema sch) {
        Entity image = sch.addEntity("Geo");
        image.addStringProperty("id").primaryKey();
        image.addStringProperty("longitude");
        image.addStringProperty("latitude");
        image.addStringProperty("city");
        image.addStringProperty("province");
        image.addStringProperty("city_name");
        image.addStringProperty("province_name");
        image.addStringProperty("address");
        image.addStringProperty("pinyin");
        image.addStringProperty("more");
        image.addStringProperty("type");
    }

    static void createImage(Schema sch) {
        Entity image = sch.addEntity("ImageInfo");
        image.addStringProperty("url").primaryKey();
        image.addIntProperty("width");
        image.addIntProperty("height");
    }

    static void createFile(Schema sch) {
        Entity image = sch.addEntity("LocalFile");
        image.addStringProperty("url").primaryKey();
        image.addStringProperty("path");
        image.addIntProperty("status");
    }

    static void createLikeBean(Schema sch) {
        Entity image = sch.addEntity("LikeBean");
        image.addLongProperty("id").primaryKey();
        image.addBooleanProperty("isLike");
    }

    static void createDirectMessage(Schema sch) {
        Entity entity = sch.addEntity("DirectMessage");
        entity.addLongProperty("id").primaryKey();
        entity.addStringProperty("idstr");
        entity.addStringProperty("created_at");
        entity.addStringProperty("text");
        entity.addIntProperty("sys_type");
        entity.addIntProperty("msg_status");
        entity.addLongProperty("sender_id");
        entity.addLongProperty("recipient_id");
        entity.addStringProperty("sender_screen_name");
        entity.addStringProperty("recipient_screen_name");
        entity.addStringProperty("mid");
        entity.addBooleanProperty("isLargeDm");
        entity.addStringProperty("source");
        entity.addLongProperty("status_id");
        entity.addIntProperty("dm_type");
        entity.addIntProperty("media_type");
        entity.addLongProperty("ip");
        entity.addLongProperty("burn_time");
        entity.addBooleanProperty("matchKeyword");
        entity.addBooleanProperty("topublic");
        entity.addBooleanProperty("pushToMPS");
        entity.addLongProperty("oriImageId");
        entity.addLongProperty("geo_id");
        entity.addIntProperty("local_status");

        entity.addLongProperty("created_at_long");

        entity.addStringProperty("att_ids_json");
    }

    static void createDirectMessageImage(Schema sch) {
        Entity entity = sch.addEntity("MessageImage");
        entity.addLongProperty("fid").primaryKey();
        entity.addLongProperty("vfid");
        entity.addLongProperty("tovfid");
        entity.addStringProperty("thumbnail_60");
        entity.addStringProperty("thumbnail_100");
        entity.addStringProperty("thumbnail_120");
        entity.addStringProperty("thumbnail_240");
        entity.addStringProperty("thumbnail_600");
        entity.addIntProperty("http_code");
    }

    static void createUrlInfo(Schema sch) {
        Entity entity = sch.addEntity("UrlInfo");
        entity.addStringProperty("shortUrl").primaryKey();
        entity.addStringProperty("url_info_json");
    }

    static void createUploadImageResponse(Schema sch) {
        Entity entity = sch.addEntity("UploadImageResponse");
        entity.addStringProperty("pic_id").primaryKey();
        entity.addStringProperty("thumbnail_pic");
        entity.addStringProperty("imagePath");
    }

    static void createDraft(Schema sch) {
        Entity entity = sch.addEntity("Draft");
        entity.addLongProperty("id").primaryKey();
        entity.addLongProperty("create_at");
        entity.addIntProperty("status");
        entity.addIntProperty("type");
        entity.addStringProperty("content");
        entity.addStringProperty("image_paths");//以逗号分隔
    }

    static void createUnReadMessage(Schema sch) {
        Entity entity = sch.addEntity("UnReadMessage");
        entity.addLongProperty("uid").primaryKey();
        entity.addIntProperty("cmt");
        entity.addIntProperty("dm");
        entity.addIntProperty("chat_group_client");
        entity.addIntProperty("mention_status");
        entity.addIntProperty("mention_cmt");
        entity.addIntProperty("invite");
        entity.addIntProperty("attitude");
        entity.addIntProperty("msgbox");
        entity.addIntProperty("common_attitude");
        entity.addIntProperty("page_follower");
        entity.addIntProperty("all_mention_status");
        entity.addIntProperty("attention_mention_status");
        entity.addIntProperty("all_mention_cmt");
        entity.addIntProperty("attention_mention_cmt");
        entity.addIntProperty("all_cmt");
        entity.addIntProperty("attention_cmt");
        entity.addIntProperty("attention_follower");
        entity.addIntProperty("chat_group_notice");
        entity.addIntProperty("hot_status");
        entity.addIntProperty("status");
        entity.addIntProperty("follower");
        entity.addIntProperty("dm_single");
    }

    static void createLongText(Schema sch) {
        Entity entity = sch.addEntity("LongText");
        entity.addLongProperty("weiboId").primaryKey();
        entity.addStringProperty("longTextContent");
    }

}