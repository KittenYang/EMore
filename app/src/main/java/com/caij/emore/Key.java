package com.caij.emore;

/**
 * Created by Caij on 2016/5/28.
 */
public interface Key {

    String DB_NAME  = "WeiBo";
    long DB_VERSION  = 1;
    String UPLOAD_FILE_BASE_URL = "http://upload.api.weibo.com";
    String UPLOAD_MESSAGE_IMAGE_URL  = UPLOAD_FILE_BASE_URL + "/2/mss/upload.json";
    String QUERY_MESSAGE_IMAGE_URL  = UPLOAD_FILE_BASE_URL + "/2/mss/meta_query.json";
    String WEIBO_BASE_URL = "https://api.weibo.com/";
    String WEIBO_APP_ID  = "1873593701";
    String WEIBO_APP_SECRET = "f6d2a29b04af69c1fe9cd10302320eeb";
    String WEIBO_CALLBACK_URL =  "https://api.weibo.com/oauth2/default.html";

    String WEICO_BASE_URL = "https://api.weibo.cn/";
    String WEICO_APP_ID = "211160679";
    String WEICO_APP_FROM = "1055095010";
    String WEICO_APP_SECRET = "63b64d531b98c2dbff2443816f274dd3";
    String WEICO_CALLBACK_URL =  "http://oauth.weico.cc";
    String UID_ENCODE_KEY  = "Aet3YvcwIWKQAV0Tz1ap6yeTE/Pk+Vel5YtqqrPBebGWZhouTNpE19LfQ4AHOmaBpTCBOFnKl5+G\ncXPNhh4st3FYeW3SQhFVptAM0Yrhbyv5nLDenbfzEA9htaVdYteBXr49yMPZIkRPslYJmZXOvxjT\nZqVTwD9HFm9uBDseUl8";

    String BUGLY_KEY = "900037371";

    String SP_CONFIG = "sp_config";
    String URL = "url";
    String OBJ = "obj";

    String ID = "id";
    String TYPE = "type";
    String IMAGE_PATHS = "image_paths";
    String IMAGE_PATH = "image_path";
    String POSITION = "position";
    String MAX = "max";
    String ON_EMOTION_CLICK = "on_emotion_click";
    String ON_EMOTION_DELETE_CLICK = "on_emotion_delete_click";
    String USERNAME = "username";
    String PWD = "pwd";
    int AUTH = 1;
    String PUBLISH_WEIBO = "publish_weibo";
    String INTERVAL_MILLIS_UPDATE = "interval_millis_update";
    String LOGIN_STATUE_EVENT = "login_statue_event";
    String SEND_MESSAGE_EVENT = "send_message_event";
    String SEND_MESSAGE_RESULT_EVENT = "send_message_result_event";
    String CID = "cid";
    String COMMENT = "comment";
    String DATE = "date";

    int REQUEST_CODE_SELECT_IMAGE = 1000;
}
