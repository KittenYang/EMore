package com.caij.emore;

/**
 * Created by Caij on 2016/8/8.
 */
public interface Event {

    String EVENT_PUBLISH_WEIBO_SUCCESS = "event_publish_weibo_success";
    String PUBLISH_WEIBO = "publish_weibo";
    String INTERVAL_MILLIS_UPDATE = "interval_millis_update";
    String SEND_MESSAGE_EVENT = "send_message_event";
    String EVENT_SEND_MESSAGE_RESULT = "event_send_message_result";
    String ON_EMOTION_CLICK = "on_emotion_click";
    String ON_EMOTION_DELETE_CLICK = "on_emotion_delete_click";
    String EVENT_TOOL_BAR_DOUBLE_CLICK = "event_tool_bar_double_click";
    String EVENT_COMMENT_WEIBO_SUCCESS = "event_comment_weibo_success";
    String EVENT_REPOST_WEIBO_SUCCESS = "event_repost_weibo_success";
    String EVENT_ATTITUDE_WEIBO_SUCCESS = "event_attitude_weibo_success";
    String EVENT_DRAFT_UPDATE = "event_draft_update";

    String EVENT_REPOST_WEIBO_REFRESH_COMPLETE = "event_repost_weibo_refresh_complete";
    String EVENT_WEIBO_COMMENTS_REFRESH_COMPLETE = "event_weibo_comments_refresh_complete";
    String EVENT_WEIBO_ATTITUDE_REFRESH_COMPLETE = "event_weibo_attitude_refresh_complete";
    String EVENT_UNREAD_MESSAGE_COMPLETE = "event_unread_message_complete";
    String EVENT_HAS_NEW_DM = "event_has_new_dm";

    String EVENT_WEIBO_UPDATE = "event_weibo_update";

    String EVENT_USER_UPDATE = "event_user_update";
}
