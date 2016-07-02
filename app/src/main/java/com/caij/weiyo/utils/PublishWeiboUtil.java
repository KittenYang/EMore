package com.caij.weiyo.utils;

import android.content.Context;
import android.content.Intent;

import com.caij.weiyo.Key;
import com.caij.weiyo.bean.PublishBean;
import com.caij.weiyo.service.WeiyoService;
import com.caij.weiyo.utils.rxbus.RxBus;

import rx.Observable;

/**
 * Created by Caij on 2016/7/2.
 */
public class PublishWeiboUtil {

    public static void publishWeibo(PublishBean publishBean, Context context) {
        if (!SystemUtil.isServiceWork(context, WeiyoService.class.getName())){
            Intent intent = new Intent(context, WeiyoService.class);
            intent.putExtra(Key.OBJ, publishBean);
            context.startService(intent);
        }else {
            RxBus.get().post(Key.PUBLISH_WEIBO, publishBean);
        }
    }

    public static void publishWeibo(PublishBean publishBean) {
        RxBus.get().post(Key.PUBLISH_WEIBO, publishBean);
    }

    public static Observable<PublishBean> registPublishEvent() {
        return RxBus.get().register(Key.PUBLISH_WEIBO);
    }

    public static void  unregistPublishEvent(Observable observable) {
        RxBus.get().unregister(Key.PUBLISH_WEIBO, observable);
    }
}
