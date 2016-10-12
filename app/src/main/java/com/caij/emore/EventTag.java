package com.caij.emore;

/**
 * Created by Caij on 2016/8/8.
 */
public interface EventTag {

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
    String EVENT_STATUS_ATTITUDE_COUNT_UPDATE = "event_status_attitude_count_update";
    String EVENT_STATUS_COMMENT_COUNT_UPDATE = "event_status_comment_count_update";
    String EVENT_STATUS_RELAY_COUNT_UPDATE = "event_status_relay_count_update";

    String EVENT_UNREAD_MESSAGE_COMPLETE = "event_unread_message_complete";
    String EVENT_HAS_NEW_DM = "event_has_new_dm";

    String EVENT_USER_UPDATE = "event_user_update";

    String EVENT_MODE_NIGHT_UPDATE = "event_mode_night_update";
    String EVENT_STATUS_REFRESH = "event_status_refresh";
}
