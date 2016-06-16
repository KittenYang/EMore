package com.fingerall.daogenerator;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DbDaoGenerator {

    private static final int VERSION = 3;

    public static void main(String[] args) throws Exception {
        Schema sch = new Schema(VERSION, "com.caij.weiyo.database.bean");
        sch.setDefaultJavaPackageDao("com.caij.weiyo.database.dao");
        sch.enableKeepSectionsByDefault();
        createUser(sch);
        createFriendWeibo(sch);
        createUserWeibo(sch);
        createImage(sch);
        createFile(sch);
        new de.greenrobot.daogenerator.DaoGenerator().generateAll(sch, "./app/src/main/java");
    }

    static void createUser(Schema sch) {
        Entity entityUser = sch.addEntity("LocalUser");
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
        entityUser.addStringProperty("weibo_json");
        entityUser.addBooleanProperty("allow_all_comment");
        entityUser.addStringProperty("avatar_large");
        entityUser.addStringProperty("avatar_hd");
        entityUser.addStringProperty("verified_reason");
        entityUser.addBooleanProperty("follow_me");
        entityUser.addIntProperty("online_status");
        entityUser.addIntProperty("bi_followers_count");
        entityUser.addStringProperty("lang");

    }

    private static void createFriendWeibo(Schema sch) {
        Entity entityUser = sch.addEntity("FriendWeibo");
        entityUser.addLongProperty("user_id");
        createWeibo(entityUser);
    }

    private static void createUserWeibo(Schema sch) {
        Entity entityUser = sch.addEntity("UserWeibo");
        entityUser.addLongProperty("user_id");
        createWeibo(entityUser);
    }

    private static void createWeibo(Entity entity) {
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
        entity.addStringProperty("geo_json");
        entity.addStringProperty("user_json");
        entity.addStringProperty("retweeted_status_json");
        entity.addIntProperty("reposts_count");
        entity.addIntProperty("comments_count");
        entity.addIntProperty("attitudes_count");
        entity.addIntProperty("mlevel");
        entity.addStringProperty("visible_json");
        entity.addStringProperty("pic_ids_json");
        entity.addLongProperty("create_time");
    }

    static void createImage(Schema sch) {
        Entity image = sch.addEntity("LocakImage");
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


}
