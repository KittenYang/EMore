package com.caij.emore.bean;

import android.text.SpannableString;

import com.caij.emore.database.bean.FriendWeibo;
import com.caij.emore.utils.GsonUtils;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Caij on 2016/5/31.
 */
public class Weibo implements Serializable{

    public static Weibo friendWeibo2Weibo(FriendWeibo friendWeibo) {
        Weibo weibo = new Weibo();
        weibo.setId(friendWeibo.getId());
        weibo.setAttitudes_count(friendWeibo.getAttitudes_count());
        weibo.setBmiddle_pic(friendWeibo.getBmiddle_pic());
        weibo.setCreated_at(friendWeibo.getCreated_at());
        weibo.setFavorited(friendWeibo.getFavorited());
        weibo.setGeo(GsonUtils.fromJson(friendWeibo.getGeo_json(), Geo.class));
        weibo.setIdstr(friendWeibo.getIdstr());
        weibo.setIn_reply_to_screen_name(friendWeibo.getIn_reply_to_screen_name());
        weibo.setIn_reply_to_status_id(friendWeibo.getIn_reply_to_status_id());
        weibo.setIn_reply_to_user_id(friendWeibo.getIn_reply_to_user_id());
        weibo.setMid(friendWeibo.getMid());
        weibo.setCreated_at(friendWeibo.getCreated_at());
        weibo.setMlevel(friendWeibo.getMlevel());
        weibo.setOriginal_pic(friendWeibo.getOriginal_pic());
        weibo.setIn_reply_to_screen_name(friendWeibo.getIn_reply_to_screen_name());
        weibo.setPic_urls((List<PicUrl>) GsonUtils.fromJson(friendWeibo.getPic_ids_json(), new TypeToken<List<PicUrl>>(){}.getType()));
        weibo.setReposts_count(friendWeibo.getReposts_count());
        weibo.setRetweeted_status(GsonUtils.fromJson(friendWeibo.getRetweeted_status_json(), Weibo.class));
        weibo.setSource(friendWeibo.getSource());
        weibo.setText(friendWeibo.getText());
        weibo.setTruncated(friendWeibo.getTruncated());
        weibo.setUser(GsonUtils.fromJson(friendWeibo.getUser_json(),  User.class));
        weibo.setVisible(GsonUtils.fromJson(friendWeibo.getVisible_json(), Visible.class));
        return weibo;
    }

    public static FriendWeibo weibo2FriendWeibo(Weibo weibo) {
        FriendWeibo friendWeibo = new FriendWeibo();
        friendWeibo.setId(weibo.getId());
        friendWeibo.setAttitudes_count(weibo.getAttitudes_count());
        friendWeibo.setBmiddle_pic(weibo.getBmiddle_pic());
        friendWeibo.setFavorited(weibo.isFavorited());
        friendWeibo.setGeo_json(GsonUtils.toJson(weibo.getGeo()));
        friendWeibo.setIdstr(weibo.getIdstr());
        friendWeibo.setIn_reply_to_screen_name(weibo.getIn_reply_to_screen_name());
        friendWeibo.setIn_reply_to_status_id(weibo.getIn_reply_to_status_id());
        friendWeibo.setIn_reply_to_user_id(weibo.getIn_reply_to_user_id());
        friendWeibo.setMid(weibo.getMid());
        friendWeibo.setCreated_at(weibo.getCreated_at());
        friendWeibo.setMlevel(weibo.getMlevel());
        friendWeibo.setOriginal_pic(weibo.getOriginal_pic());
        friendWeibo.setIn_reply_to_screen_name(weibo.getIn_reply_to_screen_name());
        friendWeibo.setPic_ids_json(GsonUtils.toJson(weibo.getPic_urls()));
        friendWeibo.setReposts_count(weibo.getReposts_count());
        friendWeibo.setRetweeted_status_json(GsonUtils.toJson(weibo.getRetweeted_status()));
        friendWeibo.setSource(weibo.getSource());
        friendWeibo.setText(weibo.getText());
        friendWeibo.setTruncated(weibo.isTruncated());
        friendWeibo.setUser_json(GsonUtils.toJson(weibo.getUser()));
        friendWeibo.setVisible_json(GsonUtils.toJson(weibo.getVisible()));
        friendWeibo.setCreate_time(Date.parse(weibo.getCreated_at()));
        return friendWeibo;
    }

    private String created_at;
    private long id;
    private long mid;
    private String idstr;
    private String text;
    private String source;
    private boolean favorited;
    private boolean truncated;
    private String in_reply_to_status_id;
    private String in_reply_to_user_id;
    private String in_reply_to_screen_name;
    private String thumbnail_pic;
    private String bmiddle_pic;
    private String original_pic;
    private Geo geo;
    private User user;
    private Weibo retweeted_status;
    private int reposts_count;
    private int comments_count;
    private int attitudes_count;
    private int mlevel;
    private Visible visible;
    private List<PicUrl> pic_urls;
    private boolean isAttitudes;

    private transient SpannableString contentSpannableString;


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMid() {
        return mid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    public String getIn_reply_to_status_id() {
        return in_reply_to_status_id;
    }

    public void setIn_reply_to_status_id(String in_reply_to_status_id) {
        this.in_reply_to_status_id = in_reply_to_status_id;
    }

    public String getIn_reply_to_user_id() {
        return in_reply_to_user_id;
    }

    public void setIn_reply_to_user_id(String in_reply_to_user_id) {
        this.in_reply_to_user_id = in_reply_to_user_id;
    }

    public String getIn_reply_to_screen_name() {
        return in_reply_to_screen_name;
    }

    public void setIn_reply_to_screen_name(String in_reply_to_screen_name) {
        this.in_reply_to_screen_name = in_reply_to_screen_name;
    }

    public String getThumbnail_pic() {
        return thumbnail_pic;
    }

    public void setThumbnail_pic(String thumbnail_pic) {
        this.thumbnail_pic = thumbnail_pic;
    }

    public String getBmiddle_pic() {
        return bmiddle_pic;
    }

    public void setBmiddle_pic(String bmiddle_pic) {
        this.bmiddle_pic = bmiddle_pic;
    }

    public String getOriginal_pic() {
        return original_pic;
    }

    public void setOriginal_pic(String original_pic) {
        this.original_pic = original_pic;
    }

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Weibo getRetweeted_status() {
        return retweeted_status;
    }

    public void setRetweeted_status(Weibo retweeted_status) {
        this.retweeted_status = retweeted_status;
    }

    public int getReposts_count() {
        return reposts_count;
    }

    public void setReposts_count(int reposts_count) {
        this.reposts_count = reposts_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public int getAttitudes_count() {
        return attitudes_count;
    }

    public void setAttitudes_count(int attitudes_count) {
        this.attitudes_count = attitudes_count;
    }

    public int getMlevel() {
        return mlevel;
    }

    public void setMlevel(int mlevel) {
        this.mlevel = mlevel;
    }

    public Visible getVisible() {
        return visible;
    }

    public void setVisible(Visible visible) {
        this.visible = visible;
    }

    public List<PicUrl> getPic_urls() {
        return pic_urls;
    }

    public void setPic_urls(List<PicUrl> pic_urls) {
        this.pic_urls = pic_urls;
    }

    public SpannableString getContentSpannableString() {
        return contentSpannableString;
    }

    public void setContentSpannableString(SpannableString contentSpannableString) {
        this.contentSpannableString = contentSpannableString;
    }

    public boolean isAttitudes() {
        return isAttitudes;
    }

    public void setAttitudes(boolean attitudes) {
        isAttitudes = attitudes;
    }

    @Override
    public boolean equals(Object o) {
        Weibo weibo = (Weibo) o;
        return id == weibo.getId();
    }
}
