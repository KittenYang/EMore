package com.caij.emore;

/**
 * Created by Caij on 2016/5/28.
 */
public interface Key {

    String DB_NAME  = "WeiBo";
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
    String WEICO_API_URL = "http://weicoapi.weico.cc/portal.php";

    String SEARCH_URL = "http://s.weibo.com//ajax/suggestion";

    String BUGLY_KEY = "900037371";

    String SP_CONFIG = "sp_config";
    String URL = "url";
    String OBJ = "obj";
    String TITLE = "title";

    String ID = "id";
    String TYPE = "type";
    String IMAGE_PATHS = "image_paths";
    String IMAGE_PATH = "image_path";
    String POSITION = "position";
    String MAX = "max";

    String PATH = "path";

    String USERNAME = "username";
    String PWD = "pwd";
    int AUTH = 1;

    String CID = "cid";
    String COMMENT = "comment";
    String DATE = "date";
    String FRIEND_WEIBO_UPDATE_TIME = "friend_weibo_update_time";
    String TRANSIT_PIC = "transit_pic";

    int REQUEST_CODE_SELECT_IMAGE = 1000;

    String WIDTH = "width";
    String HEIGHT = "height";
    String TEXT = "text";
    String PROCESS = "process";
}
