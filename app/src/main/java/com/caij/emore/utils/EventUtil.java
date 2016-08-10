package com.caij.emore.utils;

import com.caij.emore.Event;
import com.caij.emore.bean.PublishBean;
import com.caij.emore.utils.rxbus.RxBus;

import rx.Observable;

/**
 * Created by Caij on 2016/7/2.
 */
public class EventUtil {

//    //  发布微博事件
//    public static void publishWeibo(PublishBean publishBean) {
//        RxBus.getDefault().post(Event.PUBLISH_WEIBO, publishBean);
//    }

    public static Observable<PublishBean> registPublishEvent() {
        return RxBus.getDefault().register(Event.PUBLISH_WEIBO);
    }
//
//    public static void  unregistPublishEvent(Observable observable) {
//        RxBus.getDefault().unregister(Event.PUBLISH_WEIBO, observable);
//    }


    //修改定时拉取消息时间
    public static void postIntervalMillisUpdateEvent() {
        RxBus.getDefault().post(Event.INTERVAL_MILLIS_UPDATE, null);
    }

    public static Observable<Object> registIntervalMillisUpdateEvent() {
        return RxBus.getDefault().register(Event.INTERVAL_MILLIS_UPDATE);
    }

    public static void unregistIntervalMillisUpdateEvent(Observable observable) {
        RxBus.getDefault().unregister(Event.INTERVAL_MILLIS_UPDATE, observable);
    }


    /**
     * @param isLogin true 登录成功  false 注销成功
     */
    //登录状态变化
//    public static void postLoginEvent(boolean isLogin) {
//        RxBus.getDefault().post(Key.LOGIN_STATUE_EVENT, isLogin);
//    }
//
//    public static Observable<Boolean> registLoginEvent() {
//        return RxBus.getDefault().register(Key.LOGIN_STATUE_EVENT);
//    }
//
//    public static void unregistLoginEvent(Observable observable) {
//        RxBus.getDefault().unregister(Key.LOGIN_STATUE_EVENT, observable);
//    }

}
