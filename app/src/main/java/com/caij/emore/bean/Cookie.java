package com.caij.emore.bean;

import java.util.Map;

/**
 * Created by Caij on 2016/7/8.
 */
public class Cookie {

    /**
     * uid : 2300562462
     * cookie : {".sina.com.cn":"SUB=_2A256fxelDeThGeRN61IU9izIzT6IHXVZ00PtrDV_PUJbitAKLWPHkWtOuv67rwLhFYqqIN_Ftp4F7tt5Gw..; expires=Thursday, 04-Aug-16 07:55:33 GMT; path=/; domain=.sina.com.cn; httponly\nSUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WFhKaGN3rxRT92FaZImql_z5NHD95QEe057SKqEShqEWs4DqcjsC.DkdsLV; expires=Saturday, 08-Jul-17 10:20:50 GMT; path=/; domain=.sina.com.cn",".sina.cn":"SUB=_2A256fxelDeThGeRN61IU9izIzT6IHXVZ00PtrDV9PUJbitAKLUT3kWsM52xD18DWiMq6QDrRru-pudo3Vw..; expires=Thursday, 04-Aug-16 07:55:33 GMT; path=/; domain=.sina.cn; httponly\nSUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WFhKaGN3rxRT92FaZImql_z5NHD95QEe057SKqEShqEWs4DqcjsC.DkdsLV; expires=Saturday, 08-Jul-17 10:20:50 GMT; path=/; domain=.sina.cn",".weibo.com":"SUB=_2A256fxelDeThGeRN61IU9izIzT6IHXVZ00PtrDV8PUJbitAKLVP5kWsuZqss6yR5zmYWaR9GdjXS9LBo4g..; expires=Thursday, 04-Aug-16 07:55:33 GMT; path=/; domain=.weibo.com; httponly\nSUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WFhKaGN3rxRT92FaZImql_z5NHD95QEe057SKqEShqEWs4DqcjsC.DkdsLV; expires=Saturday, 08-Jul-17 10:20:50 GMT; path=/; domain=.weibo.com\nSUHB=0JvxT_zwIQ_-tA; expires=Saturday, 08-Jul-17 10:20:50 GMT; path=/; domain=.weibo.com",".weibo.cn":"SUB=_2A256fxelDeThGeRN61IU9izIzT6IHXVZ00PtrDV6PUJbitAKLVatkWsGcp7g2xKai3SWfe1Vek6l0y3ORg..; expires=Thursday, 04-Aug-16 07:55:33 GMT; path=/; domain=.weibo.cn; httponly\nSUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WFhKaGN3rxRT92FaZImql_z5NHD95QEe057SKqEShqEWs4DqcjsC.DkdsLV; expires=Saturday, 08-Jul-17 10:20:50 GMT; path=/; domain=.weibo.cn\nSUHB=0btt7rnGeLnBp9; expires=Saturday, 08-Jul-17 10:20:50 GMT; path=/; domain=.weibo.cn"}
     * expire : 1468045250
     */

    private long uid;
    private long expire;

    private Map<String, String> cookie;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public Map<String, String> getCookie() {
        return cookie;
    }

    public void setCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }
}
