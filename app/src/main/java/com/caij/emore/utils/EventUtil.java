package com.caij.emore.utils;

import com.caij.emore.Event;
import com.caij.emore.Key;
import com.caij.emore.bean.PublishBean;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.utils.rxbus.RxBus;

import rx.Observable;

/**
 * Created by Caij on 2016/7/2.
 */
public class EventUtil {

    //  发布微博事件
    public static void publishWeibo(PublishBean publishBean) {
        RxBus.get().post(Event.PUBLISH_WEIBO, publishBean);
    }

    public static Observable<PublishBean> registPublishEvent() {
        return RxBus.get().register(Event.PUBLISH_WEIBO);
    }

    public static void  unregistPublishEvent(Observable observable) {
        RxBus.get().unregister(Event.PUBLISH_WEIBO, observable);
    }


    //修改定时拉取消息时间
    public static void postIntervalMillisUpdateEvent() {
        RxBus.get().post(Event.INTERVAL_MILLIS_UPDATE, null);
    }

    public static Observable<Object> registIntervalMillisUpdateEvent() {
        return RxBus.get().register(Event.INTERVAL_MILLIS_UPDATE);
    }

    public static void unregistIntervalMillisUpdateEvent(Observable observable) {
        RxBus.get().unregister(Event.INTERVAL_MILLIS_UPDATE, observable);
    }


    /**
     * @param isLogin true 登录成功  false 注销成功
     */
    //登录状态变化
//    public static void postLoginEvent(boolean isLogin) {
//        RxBus.get().post(Key.LOGIN_STATUE_EVENT, isLogin);
//    }
//
//    public static Observable<Boolean> registLoginEvent() {
//        return RxBus.get().register(Key.LOGIN_STATUE_EVENT);
//    }
//
//    public static void unregistLoginEvent(Observable observable) {
//        RxBus.get().unregister(Key.LOGIN_STATUE_EVENT, observable);
//    }

    /**
     */
    public static void sendMessage(DirectMessage bean) {
        RxBus.get().post(Event.SEND_MESSAGE_EVENT, bean);
    }

    public static Observable<DirectMessage> registSendMessageEvent() {
        return RxBus.get().register(Event.SEND_MESSAGE_EVENT);
    }

    public static void unregistSendMessageEvent(Observable observable) {
        RxBus.get().unregister(Event.SEND_MESSAGE_EVENT, observable);
    }
}
