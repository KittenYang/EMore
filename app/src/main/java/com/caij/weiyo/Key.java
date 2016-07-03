package com.caij.weiyo;

import android.os.Environment;

/**
 * Created by Caij on 2016/5/28.
 */
public interface Key {

    String DB_NAME  = "WeiBo";
    long DB_VERSION  = 1;

    String WEIBO_BASE_URL = "https://api.weibo.com/";
    String WEIBO_APP_ID  = "1210093162";
    String WEIBO_APP_SECRET = "2304b3d6aad36d32cc737cb38607b4a8";
    String WEIBO_CALLBACK_URL =  "https://api.weibo.com/oauth2/default.html";

    String WEICO_BASE_URL = " http://weicoapi.weico.cc/";
    String WEICO_APP_ID = "211160679";
    String WEICO_APP_SECRET = "63b64d531b98c2dbff2443816f274dd3";
    String WEIYO_CALLBACK_URL =  "http://oauth.weico.cc";

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
    String CID = "cid";
    String COMMENT = "comment";
}
