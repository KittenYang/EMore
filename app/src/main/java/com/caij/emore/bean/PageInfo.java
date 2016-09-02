package com.caij.emore.bean;

import android.net.Uri;
import android.text.TextUtils;

import com.caij.emore.utils.LogUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Caij on 2016/9/1.
 */
public class PageInfo implements Serializable {


    /**
     * type : 11
     * oid : 5970127807
     * page_pic : http://ww3.sinaimg.cn/orj480/736f0c7ejw1f72fso97o5j20bu06kdfx.jpg
     * page_id : 230444df82c76ad6e02b026d0d0ac51ec9f605
     * page_title : 秒拍视频
     * type_icon : null
     * content1 : 秒拍视频
     * content2 : #没什么中国人不能吃#系列之鲱鱼罐头算个屁！[奥运金牌]这世界可能没有什么能够难倒@张一山 了[鼓掌][doge]大声告诉我你们要接受一山的#鲱鱼罐头挑战#吗？[来]凡是成功开启鲱鱼罐头发布视频艾特一山和本秘的朋友，前五位送出张一山的签名照！我们说到做到哦~[哈哈]带上你的勇气跟
     * pic_info : {"pic_big":{"height":"236","url":"http://ww3.sinaimg.cn/orj480/736f0c7ejw1f72fso97o5j20bu06kdfx.jpg","width":"426"},"pic_small":{"height":"236","url":"http://ww3.sinaimg.cn/orj480/736f0c7ejw1f72fso97o5j20bu06kdfx.jpg","width":"426"},"pic_middle":{"url":"http://ww3.sinaimg.cn/orj480/736f0c7ejw1f72fso97o5j20bu06kdfx.jpg","height":"236","width":"426"}}
     * warn :
     * author_id : 5970127807
     * page_url : sinaweibo://infopage?containerid=230444df82c76ad6e02b026d0d0ac51ec9f605&pageid=230444df82c76ad6e02b026d0d0ac51ec9f605&url_type=39&object_type=video&pos=2
     * object_type : video
     * authorid : 5970127807
     * object_id : 1034:df82c76ad6e02b026d0d0ac51ec9f605
     * media_info : {"name":"秒拍视频","duration":346,"stream_url":"http://us.sinaimg.cn/000bkGVcjx074fl74XK705040100vqtC0k01.mp4?KID=unistore,video&Expires=1472640839&ssig=7vFgxrpO8v","stream_url_hd":"http://us.sinaimg.cn/004hEPszjx074fl74USI0504010105io0k01.mp4?KID=unistore,video&Expires=1472640839&ssig=v8Owtraw3J","h5_url":"http://video.weibo.com/show?fid=1034:df82c76ad6e02b026d0d0ac51ec9f605","mp4_sd_url":"http://us.sinaimg.cn/000bkGVcjx074fl74XK705040100vqtC0k01.mp4?KID=unistore,video&Expires=1472640839&ssig=7vFgxrpO8v","mp4_hd_url":"http://us.sinaimg.cn/004hEPszjx074fl74USI0504010105io0k01.mp4?KID=unistore,video&Expires=1472640839&ssig=v8Owtraw3J","h265_mp4_hd":"http://us.sinaimg.cn/004xDdJBjx074fl7l5iL0504010000300k01.m3u8?KID=unistore,video&Expires=1472640839&ssig=qzR2VT%2BKbm","h265_mp4_ld":"http://us.sinaimg.cn/000YhsXtjx074fl79FfV0504010000300k01.m3u8?KID=unistore,video&Expires=1472640839&ssig=DCoYibhhCo","inch_4_mp4_hd":"http://us.sinaimg.cn/0043Rjl4jx074fl71PMH05040100DMbp0k01.mp4?KID=unistore,video&Expires=1472640839&ssig=f6korEQ%2FJb","inch_5_mp4_hd":"http://us.sinaimg.cn/003FW2aBjx074fl71Ou405040100PyKB0k01.mp4?KID=unistore,video&Expires=1472640839&ssig=k%2Fbj3SKnDJ","inch_5_5_mp4_hd":"http://us.sinaimg.cn/0007A2NIjx074fl72MzR05040100WGWS0k01.mp4?KID=unistore,video&Expires=1472640839&ssig=4Pa4Y3cVs5","video_feed_show_custom_bg":0,"play_completion_actions":[{"type":1,"icon":"http://img.t.sinajs.cn/t6/style/images/face/feed_c_r.png","text":"重播","link":"","btn_code":1000,"show_position":1,"actionlog":{"oid":"230444df82c76ad6e02b026d0d0ac51ec9f605","act_code":1221,"act_type":0,"source":"video"}},{"type":2,"icon":"http://img.t.sinajs.cn/t6/style/images/face/feed_c_s.png","text":"打赏","btn_code":1001,"show_position":5,"link":"http://e.weibo.com/v1/public/paid/h5/rewardaccess?seller=5970127807&buyer=2300562462&bid=1000281261&oid=1034:df82c76ad6e02b026d0d0ac51ec9f605&share=1&sign=e601adf1ca61a60f5321bc5d589ee69f&miduid=2625481001","actionlog":{"oid":"230444df82c76ad6e02b026d0d0ac51ec9f605","act_code":1222,"act_type":0,"source":"video"}}],"encode_mode":"crf","prefetch_type":1,"prefetch_size":524288,"online_users_number":25340593,"online_users":"2534万次播放","ttl":3600,"storage_type":"unistore","has_recommend_video":1}
     * act_status : 1
     * actionlog : {"act_type":1,"uid":2300562462,"mid":"4012017150764989","uuid":4011258352292021,"source":"video","act_code":799,"ext":"uid:2300562462|mid:4012017150764989|objectid:1034%3Adf82c76ad6e02b026d0d0ac51ec9f605|from:1|object_duration:346|miduid:2625481001|rootuid:2625481001|rootmid:4012017150764989|detail:native","fid":null}
     */

    private int type;
    private String oid;
    private String page_pic;
    private String page_id;
    private String page_title;
    private Object type_icon;
    private String content1;
    private String content2;
    /**
     * pic_big : {"height":"236","url":"http://ww3.sinaimg.cn/orj480/736f0c7ejw1f72fso97o5j20bu06kdfx.jpg","width":"426"}
     * pic_small : {"height":"236","url":"http://ww3.sinaimg.cn/orj480/736f0c7ejw1f72fso97o5j20bu06kdfx.jpg","width":"426"}
     * pic_middle : {"url":"http://ww3.sinaimg.cn/orj480/736f0c7ejw1f72fso97o5j20bu06kdfx.jpg","height":"236","width":"426"}
     */

    private PicInfo pic_info;
    private String warn;
    private String author_id;
    private String page_url;
    private String object_type;
    private String authorid;
    private String object_id;
    /**
     * name : 秒拍视频
     * duration : 346
     * stream_url : http://us.sinaimg.cn/000bkGVcjx074fl74XK705040100vqtC0k01.mp4?KID=unistore,video&Expires=1472640839&ssig=7vFgxrpO8v
     * stream_url_hd : http://us.sinaimg.cn/004hEPszjx074fl74USI0504010105io0k01.mp4?KID=unistore,video&Expires=1472640839&ssig=v8Owtraw3J
     * h5_url : http://video.weibo.com/show?fid=1034:df82c76ad6e02b026d0d0ac51ec9f605
     * mp4_sd_url : http://us.sinaimg.cn/000bkGVcjx074fl74XK705040100vqtC0k01.mp4?KID=unistore,video&Expires=1472640839&ssig=7vFgxrpO8v
     * mp4_hd_url : http://us.sinaimg.cn/004hEPszjx074fl74USI0504010105io0k01.mp4?KID=unistore,video&Expires=1472640839&ssig=v8Owtraw3J
     * h265_mp4_hd : http://us.sinaimg.cn/004xDdJBjx074fl7l5iL0504010000300k01.m3u8?KID=unistore,video&Expires=1472640839&ssig=qzR2VT%2BKbm
     * h265_mp4_ld : http://us.sinaimg.cn/000YhsXtjx074fl79FfV0504010000300k01.m3u8?KID=unistore,video&Expires=1472640839&ssig=DCoYibhhCo
     * inch_4_mp4_hd : http://us.sinaimg.cn/0043Rjl4jx074fl71PMH05040100DMbp0k01.mp4?KID=unistore,video&Expires=1472640839&ssig=f6korEQ%2FJb
     * inch_5_mp4_hd : http://us.sinaimg.cn/003FW2aBjx074fl71Ou405040100PyKB0k01.mp4?KID=unistore,video&Expires=1472640839&ssig=k%2Fbj3SKnDJ
     * inch_5_5_mp4_hd : http://us.sinaimg.cn/0007A2NIjx074fl72MzR05040100WGWS0k01.mp4?KID=unistore,video&Expires=1472640839&ssig=4Pa4Y3cVs5
     * video_feed_show_custom_bg : 0
     * play_completion_actions : [{"type":1,"icon":"http://img.t.sinajs.cn/t6/style/images/face/feed_c_r.png","text":"重播","link":"","btn_code":1000,"show_position":1,"actionlog":{"oid":"230444df82c76ad6e02b026d0d0ac51ec9f605","act_code":1221,"act_type":0,"source":"video"}},{"type":2,"icon":"http://img.t.sinajs.cn/t6/style/images/face/feed_c_s.png","text":"打赏","btn_code":1001,"show_position":5,"link":"http://e.weibo.com/v1/public/paid/h5/rewardaccess?seller=5970127807&buyer=2300562462&bid=1000281261&oid=1034:df82c76ad6e02b026d0d0ac51ec9f605&share=1&sign=e601adf1ca61a60f5321bc5d589ee69f&miduid=2625481001","actionlog":{"oid":"230444df82c76ad6e02b026d0d0ac51ec9f605","act_code":1222,"act_type":0,"source":"video"}}]
     * encode_mode : crf
     * prefetch_type : 1
     * prefetch_size : 524288
     * online_users_number : 25340593
     * online_users : 2534万次播放
     * ttl : 3600
     * storage_type : unistore
     * has_recommend_video : 1
     */

    private MediaInfo media_info;
    private int act_status;
    /**
     * act_type : 1
     * uid : 2300562462
     * mid : 4012017150764989
     * uuid : 4011258352292021
     * source : video
     * act_code : 799
     * ext : uid:2300562462|mid:4012017150764989|objectid:1034%3Adf82c76ad6e02b026d0d0ac51ec9f605|from:1|object_duration:346|miduid:2625481001|rootuid:2625481001|rootmid:4012017150764989|detail:native
     * fid : null
     */

    private Actionlog actionlog;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getPage_pic() {
        return page_pic;
    }

    public void setPage_pic(String page_pic) {
        this.page_pic = page_pic;
    }

    public String getPage_id() {
        return page_id;
    }

    public void setPage_id(String page_id) {
        this.page_id = page_id;
    }

    public String getPage_title() {
        return page_title;
    }

    public void setPage_title(String page_title) {
        this.page_title = page_title;
    }

    public Object getType_icon() {
        return type_icon;
    }

    public void setType_icon(Object type_icon) {
        this.type_icon = type_icon;
    }

    public String getContent1() {
        return content1;
    }

    public void setContent1(String content1) {
        this.content1 = content1;
    }

    public String getContent2() {
        return content2;
    }

    public void setContent2(String content2) {
        this.content2 = content2;
    }

    public PicInfo getPic_info() {
        return pic_info;
    }

    public void setPic_info(PicInfo pic_info) {
        this.pic_info = pic_info;
    }

    public String getWarn() {
        return warn;
    }

    public void setWarn(String warn) {
        this.warn = warn;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getPage_url() {
        return page_url;
    }

    public void setPage_url(String page_url) {
        this.page_url = page_url;
    }

    public String getObject_type() {
        return object_type;
    }

    private int pageType = -1;

    public int getPageType() {
        if (pageType > -1) {
            return pageType;
        }
        if (!TextUtils.isEmpty(object_type)) {
            LogUtil.d(this, "string obj type " + object_type);
            pageType = stringTypeToInt(object_type);
            LogUtil.d(this, "int obj type " + pageType);
            return pageType;
        }else {
            return pageType = ShortUrl.getType(page_url);
        }
    }

    public static int getType(String pageUrl) {
        if (TextUtils.isEmpty(pageUrl) || !pageUrl.startsWith("sinaweibo://")) {
            return ShortUrl.TYPE_WEB;
        }else {
            Uri uri = Uri.parse(pageUrl);
            String type = uri.getQueryParameter("object_type");
            return stringTypeToInt(type);
        }
    }

    public static int stringTypeToInt(String type) {
        if ("article".equals(type)) {
            return ShortUrl.TYPE_WEB;
        }else if ("video".equals(type)) {
            return ShortUrl.TYPE_VIDEO;
        }else if ("collection".equals(type)) {
            return ShortUrl.TYPE_IMAGE;
        }else {
            return ShortUrl.TYPE_WEB;
        }
    }


    public void setObject_type(String object_type) {
        this.object_type = object_type;
    }

    public String getAuthorid() {
        return authorid;
    }

    public void setAuthorid(String authorid) {
        this.authorid = authorid;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public MediaInfo getMedia_info() {
        return media_info;
    }

    public void setMedia_info(MediaInfo media_info) {
        this.media_info = media_info;
    }

    public int getAct_status() {
        return act_status;
    }

    public void setAct_status(int act_status) {
        this.act_status = act_status;
    }

    public Actionlog getActionlog() {
        return actionlog;
    }

    public void setActionlog(Actionlog actionlog) {
        this.actionlog = actionlog;
    }

    public static class PicInfo {
        /**
         * height : 236
         * url : http://ww3.sinaimg.cn/orj480/736f0c7ejw1f72fso97o5j20bu06kdfx.jpg
         * width : 426
         */

        private PicBigBean pic_big;
        /**
         * height : 236
         * url : http://ww3.sinaimg.cn/orj480/736f0c7ejw1f72fso97o5j20bu06kdfx.jpg
         * width : 426
         */

        private PicSmallBean pic_small;
        /**
         * url : http://ww3.sinaimg.cn/orj480/736f0c7ejw1f72fso97o5j20bu06kdfx.jpg
         * height : 236
         * width : 426
         */

        private PicMiddleBean pic_middle;

        public PicBigBean getPic_big() {
            return pic_big;
        }

        public void setPic_big(PicBigBean pic_big) {
            this.pic_big = pic_big;
        }

        public PicSmallBean getPic_small() {
            return pic_small;
        }

        public void setPic_small(PicSmallBean pic_small) {
            this.pic_small = pic_small;
        }

        public PicMiddleBean getPic_middle() {
            return pic_middle;
        }

        public void setPic_middle(PicMiddleBean pic_middle) {
            this.pic_middle = pic_middle;
        }

        public static class PicBigBean {
            private String height;
            private String url;
            private String width;

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getWidth() {
                return width;
            }

            public void setWidth(String width) {
                this.width = width;
            }
        }

        public static class PicSmallBean {
            private String height;
            private String url;
            private String width;

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getWidth() {
                return width;
            }

            public void setWidth(String width) {
                this.width = width;
            }
        }

        public static class PicMiddleBean {
            private String url;
            private String height;
            private String width;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }

            public String getWidth() {
                return width;
            }

            public void setWidth(String width) {
                this.width = width;
            }
        }
    }

    public static class MediaInfo implements Serializable {
        private String name;
        private int duration;
        private String stream_url;
        private String stream_url_hd;
        private String h5_url;
        private String mp4_sd_url;
        private String mp4_hd_url;
        private String h265_mp4_hd;
        private String h265_mp4_ld;
        private String inch_4_mp4_hd;
        private String inch_5_mp4_hd;
        private String inch_5_5_mp4_hd;
        private int video_feed_show_custom_bg;
        private String encode_mode;
        private int prefetch_type;
        private int prefetch_size;
        private int online_users_number;
        private String online_users;
        private int ttl;
        private String storage_type;
        private int has_recommend_video;
        /**
         * type : 1
         * icon : http://img.t.sinajs.cn/t6/style/images/face/feed_c_r.png
         * text : 重播
         * link :
         * btn_code : 1000
         * show_position : 1
         * actionlog : {"oid":"230444df82c76ad6e02b026d0d0ac51ec9f605","act_code":1221,"act_type":0,"source":"video"}
         */

        private List<PlayCompletionActionsBean> play_completion_actions;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getStream_url() {
            return stream_url;
        }

        public void setStream_url(String stream_url) {
            this.stream_url = stream_url;
        }

        public String getStream_url_hd() {
            return stream_url_hd;
        }

        public void setStream_url_hd(String stream_url_hd) {
            this.stream_url_hd = stream_url_hd;
        }

        public String getH5_url() {
            return h5_url;
        }

        public void setH5_url(String h5_url) {
            this.h5_url = h5_url;
        }

        public String getMp4_sd_url() {
            return mp4_sd_url;
        }

        public void setMp4_sd_url(String mp4_sd_url) {
            this.mp4_sd_url = mp4_sd_url;
        }

        public String getMp4_hd_url() {
            return mp4_hd_url;
        }

        public void setMp4_hd_url(String mp4_hd_url) {
            this.mp4_hd_url = mp4_hd_url;
        }

        public String getH265_mp4_hd() {
            return h265_mp4_hd;
        }

        public void setH265_mp4_hd(String h265_mp4_hd) {
            this.h265_mp4_hd = h265_mp4_hd;
        }

        public String getH265_mp4_ld() {
            return h265_mp4_ld;
        }

        public void setH265_mp4_ld(String h265_mp4_ld) {
            this.h265_mp4_ld = h265_mp4_ld;
        }

        public String getInch_4_mp4_hd() {
            return inch_4_mp4_hd;
        }

        public void setInch_4_mp4_hd(String inch_4_mp4_hd) {
            this.inch_4_mp4_hd = inch_4_mp4_hd;
        }

        public String getInch_5_mp4_hd() {
            return inch_5_mp4_hd;
        }

        public void setInch_5_mp4_hd(String inch_5_mp4_hd) {
            this.inch_5_mp4_hd = inch_5_mp4_hd;
        }

        public String getInch_5_5_mp4_hd() {
            return inch_5_5_mp4_hd;
        }

        public void setInch_5_5_mp4_hd(String inch_5_5_mp4_hd) {
            this.inch_5_5_mp4_hd = inch_5_5_mp4_hd;
        }

        public int getVideo_feed_show_custom_bg() {
            return video_feed_show_custom_bg;
        }

        public void setVideo_feed_show_custom_bg(int video_feed_show_custom_bg) {
            this.video_feed_show_custom_bg = video_feed_show_custom_bg;
        }

        public String getEncode_mode() {
            return encode_mode;
        }

        public void setEncode_mode(String encode_mode) {
            this.encode_mode = encode_mode;
        }

        public int getPrefetch_type() {
            return prefetch_type;
        }

        public void setPrefetch_type(int prefetch_type) {
            this.prefetch_type = prefetch_type;
        }

        public int getPrefetch_size() {
            return prefetch_size;
        }

        public void setPrefetch_size(int prefetch_size) {
            this.prefetch_size = prefetch_size;
        }

        public int getOnline_users_number() {
            return online_users_number;
        }

        public void setOnline_users_number(int online_users_number) {
            this.online_users_number = online_users_number;
        }

        public String getOnline_users() {
            return online_users;
        }

        public void setOnline_users(String online_users) {
            this.online_users = online_users;
        }

        public int getTtl() {
            return ttl;
        }

        public void setTtl(int ttl) {
            this.ttl = ttl;
        }

        public String getStorage_type() {
            return storage_type;
        }

        public void setStorage_type(String storage_type) {
            this.storage_type = storage_type;
        }

        public int getHas_recommend_video() {
            return has_recommend_video;
        }

        public void setHas_recommend_video(int has_recommend_video) {
            this.has_recommend_video = has_recommend_video;
        }

        public List<PlayCompletionActionsBean> getPlay_completion_actions() {
            return play_completion_actions;
        }

        public void setPlay_completion_actions(List<PlayCompletionActionsBean> play_completion_actions) {
            this.play_completion_actions = play_completion_actions;
        }

        public static class PlayCompletionActionsBean {
            private int type;
            private String icon;
            private String text;
            private String link;
            private int btn_code;
            private int show_position;
            /**
             * oid : 230444df82c76ad6e02b026d0d0ac51ec9f605
             * act_code : 1221
             * act_type : 0
             * source : video
             */

            private ActionlogBean actionlog;

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }

            public int getBtn_code() {
                return btn_code;
            }

            public void setBtn_code(int btn_code) {
                this.btn_code = btn_code;
            }

            public int getShow_position() {
                return show_position;
            }

            public void setShow_position(int show_position) {
                this.show_position = show_position;
            }

            public ActionlogBean getActionlog() {
                return actionlog;
            }

            public void setActionlog(ActionlogBean actionlog) {
                this.actionlog = actionlog;
            }

            public static class ActionlogBean {
                private String oid;
                private int act_code;
                private int act_type;
                private String source;

                public String getOid() {
                    return oid;
                }

                public void setOid(String oid) {
                    this.oid = oid;
                }

                public int getAct_code() {
                    return act_code;
                }

                public void setAct_code(int act_code) {
                    this.act_code = act_code;
                }

                public int getAct_type() {
                    return act_type;
                }

                public void setAct_type(int act_type) {
                    this.act_type = act_type;
                }

                public String getSource() {
                    return source;
                }

                public void setSource(String source) {
                    this.source = source;
                }
            }
        }
    }

    public static class Actionlog {
        private int act_type;
        private long uid;
        private String mid;
        private long uuid;
        private String source;
        private int act_code;
        private String ext;
        private Object fid;

        public int getAct_type() {
            return act_type;
        }

        public void setAct_type(int act_type) {
            this.act_type = act_type;
        }

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public long getUuid() {
            return uuid;
        }

        public void setUuid(long uuid) {
            this.uuid = uuid;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public int getAct_code() {
            return act_code;
        }

        public void setAct_code(int act_code) {
            this.act_code = act_code;
        }

        public String getExt() {
            return ext;
        }

        public void setExt(String ext) {
            this.ext = ext;
        }

        public Object getFid() {
            return fid;
        }

        public void setFid(Object fid) {
            this.fid = fid;
        }
    }
}
