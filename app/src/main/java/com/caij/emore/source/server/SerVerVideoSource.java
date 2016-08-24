package com.caij.emore.source.server;

import com.caij.emore.AppApplication;
import com.caij.emore.Key;
import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.VideoInfo;
import com.caij.emore.source.VideoSource;
import com.caij.emore.utils.SystemUtil;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by Caij on 2016/7/28.
 */
public class SerVerVideoSource implements VideoSource {
    @Override
    public Observable<VideoInfo> getVideoInfo(long weiboId) {
        Map<String, Object> map = new HashMap<>();
        map.put("weibo_c", "weicoandroid");
        map.put("weibo_id", weiboId);
        map.put("weibo_s", "3f9afac0");
        if (SystemUtil.isNetworkWifi(AppApplication.getInstance())) {
            map.put("ua", "wifi");
        }else {
            map.put("ua", "null-wifi");
        }

        map.put("weibo_gsid", "_2A256ndUIDeTxGeRN61IU9izIzT6IHXVXC2_ArDV6PUJbjdANLRjykWplVR9HICNdgODbL8GcFgsJj3cg2g..");
        return WeiCoService.WeiCoFactory.create().getVideoInfo(Key.WEICO_API_URL, "get_video", "default", map);
    }
}
