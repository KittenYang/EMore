package com.caij.weiyo;

import android.os.Environment;

/**
 * Created by Caij on 2016/5/28.
 */
public interface Key {

    String DB_NAME  = "WeiBo";
    long DB_VERSION  = 1;

    String WEIBO_BASE_URL = "https://api.weibo.com/";
    String WEICO_BASE_URL = " http://weicoapi.weico.cc/";
    String WEIBO_APP_ID  = "1210093162";
    String WEIBO_APP_SECRET = "2304b3d6aad36d32cc737cb38607b4a8";
    String WEIBO_CALLBACK_URL =  "https://api.weibo.com/oauth2/default.html";

    String SP_CONFIG = "sp_config";
    String URL = "url";
    String OBJ = "obj";

    String ID = "id";
    String TYPE = "type";
}
