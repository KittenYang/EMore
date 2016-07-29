package com.caij.emore.bean;

import java.util.List;

/**
 * Created by Caij on 2016/7/28.
 */
public class ShortUrlInfo {


    /**
     * result : true
     * last_modified : 1467980738
     * title : 301 Moved Permanently
     * description :
     * url_short : http://t.cn/R5rWKTV
     * annotations : [{"timestamp":1468657709402,"object_type":"video","activate_status":"0","last_modified":"Sat Jul 16 16:28:29 CST 2016","safe_status":1,"containerid":"2304426662b518cf7ea2d771a7315955d41685","object_id":"2017607:6662b518cf7ea2d771a7315955d41685","act_status":"12","object":{"object_type":"video","summary":"林俊杰16岁时，有位女友人向他告白，虽没成为情侣却变成好朋友，然而在1997年，这位女友人搭上了死亡航班，飞机在空中粉碎性解体，机组人员和乘客无一生还。林俊决定正视这段难以忘怀的过去，于是写下了这首《修炼爱情》 via@音乐大湿肖棒","display_name":"秒拍视频","author":{"id":2566570841,"object_type":"person","display_name":"甩古_","url":"http://weibo.com/u/2566570841"},"stream":{"duration":354,"height":360,"width":640,"format":"mp4","url":"http://gslb.miaopai.com/stream/4pW3K4Pj2hZ1A1p5rvX-3Q__.mp4?yx=&refer=weibo_app"},"object_type_detail":"minivideo","post_links":{"native_link":"miaopai://profile?suid=z7plVPOgb0iHbdIn","android_native_link":"miaopai://square.app/start?type=1&suid=z7plVPOgb0iHbdIn","link":"http://www.miaopai.com/download"},"image":{"height":360,"width":640,"url":"http://qncdn.miaopai.com/imgs/4pW3K4Pj2hZ1A1p5rvX-3Q___000002.jpg"},"links":{"url":"http://mobile.yixia.com/show/4pW3K4Pj2hZ1A1p5rvX-3Q__.htm"},"embed_code":"http://www.miaopai.com/show/4pW3K4Pj2hZ1A1p5rvX-3Q__.swf","custom_data":{"source":"秒拍视频"},"target_url":"http://miaopai.com/show/4pW3K4Pj2hZ1A1p5rvX-3Q__.htm","url":"http://miaopai.com/show/4pW3K4Pj2hZ1A1p5rvX-3Q__.htm","biz":{"biz_id":"230442","containerid":"2304426662b518cf7ea2d771a7315955d41685"},"scid":"4pW3K4Pj2hZ1A1p5rvX-3Q__"},"uuid":3995059207817535,"show_status":"11","object_domain_id":"2017607"}]
     * url_long : http://miaopai.com/show/4pW3K4Pj2hZ1A1p5rvX-3Q__.htm
     * type : 39
     * transcode : 0
     */

    private List<UrlsBean> urls;

    public List<UrlsBean> getUrls() {
        return urls;
    }

    public void setUrls(List<UrlsBean> urls) {
        this.urls = urls;
    }

    public static class UrlsBean {
        private boolean result;
        private int last_modified;
        private String title;
        private String description;
        private String url_short;
        private String url_long;
        private int type;
        private int transcode;
        /**
         * timestamp : 1468657709402
         * object_type : video
         * activate_status : 0
         * last_modified : Sat Jul 16 16:28:29 CST 2016
         * safe_status : 1
         * containerid : 2304426662b518cf7ea2d771a7315955d41685
         * object_id : 2017607:6662b518cf7ea2d771a7315955d41685
         * act_status : 12
         * object : {"object_type":"video","summary":"林俊杰16岁时，有位女友人向他告白，虽没成为情侣却变成好朋友，然而在1997年，这位女友人搭上了死亡航班，飞机在空中粉碎性解体，机组人员和乘客无一生还。林俊决定正视这段难以忘怀的过去，于是写下了这首《修炼爱情》 via@音乐大湿肖棒","display_name":"秒拍视频","author":{"id":2566570841,"object_type":"person","display_name":"甩古_","url":"http://weibo.com/u/2566570841"},"stream":{"duration":354,"height":360,"width":640,"format":"mp4","url":"http://gslb.miaopai.com/stream/4pW3K4Pj2hZ1A1p5rvX-3Q__.mp4?yx=&refer=weibo_app"},"object_type_detail":"minivideo","post_links":{"native_link":"miaopai://profile?suid=z7plVPOgb0iHbdIn","android_native_link":"miaopai://square.app/start?type=1&suid=z7plVPOgb0iHbdIn","link":"http://www.miaopai.com/download"},"image":{"height":360,"width":640,"url":"http://qncdn.miaopai.com/imgs/4pW3K4Pj2hZ1A1p5rvX-3Q___000002.jpg"},"links":{"url":"http://mobile.yixia.com/show/4pW3K4Pj2hZ1A1p5rvX-3Q__.htm"},"embed_code":"http://www.miaopai.com/show/4pW3K4Pj2hZ1A1p5rvX-3Q__.swf","custom_data":{"source":"秒拍视频"},"target_url":"http://miaopai.com/show/4pW3K4Pj2hZ1A1p5rvX-3Q__.htm","url":"http://miaopai.com/show/4pW3K4Pj2hZ1A1p5rvX-3Q__.htm","biz":{"biz_id":"230442","containerid":"2304426662b518cf7ea2d771a7315955d41685"},"scid":"4pW3K4Pj2hZ1A1p5rvX-3Q__"}
         * uuid : 3995059207817535
         * show_status : 11
         * object_domain_id : 2017607
         */

        private List<AnnotationsBean> annotations;

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        public int getLast_modified() {
            return last_modified;
        }

        public void setLast_modified(int last_modified) {
            this.last_modified = last_modified;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl_short() {
            return url_short;
        }

        public void setUrl_short(String url_short) {
            this.url_short = url_short;
        }

        public String getUrl_long() {
            return url_long;
        }

        public void setUrl_long(String url_long) {
            this.url_long = url_long;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getTranscode() {
            return transcode;
        }

        public void setTranscode(int transcode) {
            this.transcode = transcode;
        }

        public List<AnnotationsBean> getAnnotations() {
            return annotations;
        }

        public void setAnnotations(List<AnnotationsBean> annotations) {
            this.annotations = annotations;
        }

        public static class AnnotationsBean {
            private long timestamp;
            private String object_type;
            private String activate_status;
            private String last_modified;
            private int safe_status;
            private String containerid;
            private String object_id;
            private String act_status;
            /**
             * object_type : video
             * summary : 林俊杰16岁时，有位女友人向他告白，虽没成为情侣却变成好朋友，然而在1997年，这位女友人搭上了死亡航班，飞机在空中粉碎性解体，机组人员和乘客无一生还。林俊决定正视这段难以忘怀的过去，于是写下了这首《修炼爱情》 via@音乐大湿肖棒
             * display_name : 秒拍视频
             * author : {"id":2566570841,"object_type":"person","display_name":"甩古_","url":"http://weibo.com/u/2566570841"}
             * stream : {"duration":354,"height":360,"width":640,"format":"mp4","url":"http://gslb.miaopai.com/stream/4pW3K4Pj2hZ1A1p5rvX-3Q__.mp4?yx=&refer=weibo_app"}
             * object_type_detail : minivideo
             * post_links : {"native_link":"miaopai://profile?suid=z7plVPOgb0iHbdIn","android_native_link":"miaopai://square.app/start?type=1&suid=z7plVPOgb0iHbdIn","link":"http://www.miaopai.com/download"}
             * image : {"height":360,"width":640,"url":"http://qncdn.miaopai.com/imgs/4pW3K4Pj2hZ1A1p5rvX-3Q___000002.jpg"}
             * links : {"url":"http://mobile.yixia.com/show/4pW3K4Pj2hZ1A1p5rvX-3Q__.htm"}
             * embed_code : http://www.miaopai.com/show/4pW3K4Pj2hZ1A1p5rvX-3Q__.swf
             * custom_data : {"source":"秒拍视频"}
             * target_url : http://miaopai.com/show/4pW3K4Pj2hZ1A1p5rvX-3Q__.htm
             * url : http://miaopai.com/show/4pW3K4Pj2hZ1A1p5rvX-3Q__.htm
             * biz : {"biz_id":"230442","containerid":"2304426662b518cf7ea2d771a7315955d41685"}
             * scid : 4pW3K4Pj2hZ1A1p5rvX-3Q__
             */

            private ObjectBean object;
            private long uuid;
            private String show_status;
            private String object_domain_id;


            public static final int TYPE_WEB = 0;
            public static final int TYPE_VIDEO = 1;
            public static final int TYPE_MUSIC = 2;
            public static final int TYPE_IMAGE = Integer.MAX_VALUE;
            public static final int TYPE_ARTICLE = Integer.MAX_VALUE - 1;
            public static final int TYPE_WEB_PAGE = Integer.MAX_VALUE - 2;
            public static final int TYPE_FULL_TEXT = Integer.MAX_VALUE - 3;


            public int getUrlType() {
                if ("video".equalsIgnoreCase(this.object_type)) {
                    return TYPE_VIDEO;
                }else if ("collection".equalsIgnoreCase(object_type)) {
                    return TYPE_IMAGE;
                }else if ("article".equalsIgnoreCase(object_type)) {
                    return TYPE_WEB_PAGE;
                }else if ("webpage".equalsIgnoreCase(object_type)) {
                    return TYPE_WEB_PAGE;
                }
                return TYPE_WEB_PAGE;
            }

            public long getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }

            public String getObject_type() {
                return object_type;
            }

            public void setObject_type(String object_type) {
                this.object_type = object_type;
            }

            public String getActivate_status() {
                return activate_status;
            }

            public void setActivate_status(String activate_status) {
                this.activate_status = activate_status;
            }

            public String getLast_modified() {
                return last_modified;
            }

            public void setLast_modified(String last_modified) {
                this.last_modified = last_modified;
            }

            public int getSafe_status() {
                return safe_status;
            }

            public void setSafe_status(int safe_status) {
                this.safe_status = safe_status;
            }

            public String getContainerid() {
                return containerid;
            }

            public void setContainerid(String containerid) {
                this.containerid = containerid;
            }

            public String getObject_id() {
                return object_id;
            }

            public void setObject_id(String object_id) {
                this.object_id = object_id;
            }

            public String getAct_status() {
                return act_status;
            }

            public void setAct_status(String act_status) {
                this.act_status = act_status;
            }

            public ObjectBean getObject() {
                return object;
            }

            public void setObject(ObjectBean object) {
                this.object = object;
            }

            public long getUuid() {
                return uuid;
            }

            public void setUuid(long uuid) {
                this.uuid = uuid;
            }

            public String getShow_status() {
                return show_status;
            }

            public void setShow_status(String show_status) {
                this.show_status = show_status;
            }

            public String getObject_domain_id() {
                return object_domain_id;
            }

            public void setObject_domain_id(String object_domain_id) {
                this.object_domain_id = object_domain_id;
            }

            public static class ObjectBean {
                private String object_type;
                private String summary;
                private String display_name;

                /**
                 * duration : 354
                 * height : 360
                 * width : 640
                 * format : mp4
                 * url : http://gslb.miaopai.com/stream/4pW3K4Pj2hZ1A1p5rvX-3Q__.mp4?yx=&refer=weibo_app
                 */

                private StreamBean stream;
                private String object_type_detail;
                /**
                 * native_link : miaopai://profile?suid=z7plVPOgb0iHbdIn
                 * android_native_link : miaopai://square.app/start?type=1&suid=z7plVPOgb0iHbdIn
                 * link : http://www.miaopai.com/download
                 */

                private PostLinksBean post_links;
                /**
                 * height : 360
                 * width : 640
                 * url : http://qncdn.miaopai.com/imgs/4pW3K4Pj2hZ1A1p5rvX-3Q___000002.jpg
                 */

                private ImageBean image;
                /**
                 * url : http://mobile.yixia.com/show/4pW3K4Pj2hZ1A1p5rvX-3Q__.htm
                 */

                private String embed_code;
                /**
                 * source : 秒拍视频
                 */

                private CustomDataBean custom_data;
                private String target_url;
                private String url;
                /**
                 * biz_id : 230442
                 * containerid : 2304426662b518cf7ea2d771a7315955d41685
                 */

                private BizBean biz;
                private String scid;

                public String getObject_type() {
                    return object_type;
                }

                public void setObject_type(String object_type) {
                    this.object_type = object_type;
                }

                public String getSummary() {
                    return summary;
                }

                public void setSummary(String summary) {
                    this.summary = summary;
                }

                public String getDisplay_name() {
                    return display_name;
                }

                public void setDisplay_name(String display_name) {
                    this.display_name = display_name;
                }

                public StreamBean getStream() {
                    return stream;
                }

                public void setStream(StreamBean stream) {
                    this.stream = stream;
                }

                public String getObject_type_detail() {
                    return object_type_detail;
                }

                public void setObject_type_detail(String object_type_detail) {
                    this.object_type_detail = object_type_detail;
                }

                public PostLinksBean getPost_links() {
                    return post_links;
                }

                public void setPost_links(PostLinksBean post_links) {
                    this.post_links = post_links;
                }

                public ImageBean getImage() {
                    return image;
                }

                public void setImage(ImageBean image) {
                    this.image = image;
                }

                public String getEmbed_code() {
                    return embed_code;
                }

                public void setEmbed_code(String embed_code) {
                    this.embed_code = embed_code;
                }

                public CustomDataBean getCustom_data() {
                    return custom_data;
                }

                public void setCustom_data(CustomDataBean custom_data) {
                    this.custom_data = custom_data;
                }

                public String getTarget_url() {
                    return target_url;
                }

                public void setTarget_url(String target_url) {
                    this.target_url = target_url;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public BizBean getBiz() {
                    return biz;
                }

                public void setBiz(BizBean biz) {
                    this.biz = biz;
                }

                public String getScid() {
                    return scid;
                }

                public void setScid(String scid) {
                    this.scid = scid;
                }

                public static class StreamBean {
                    private int duration;
                    private int height;
                    private int width;
                    private String format;
                    private String url;
                    private String hd_url;

                    public int getDuration() {
                        return duration;
                    }

                    public void setDuration(int duration) {
                        this.duration = duration;
                    }

                    public int getHeight() {
                        return height;
                    }

                    public void setHeight(int height) {
                        this.height = height;
                    }

                    public int getWidth() {
                        return width;
                    }

                    public void setWidth(int width) {
                        this.width = width;
                    }

                    public String getFormat() {
                        return format;
                    }

                    public void setFormat(String format) {
                        this.format = format;
                    }

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }

                    public String getHd_url() {
                        return hd_url;
                    }

                    public void setHd_url(String hd_url) {
                        this.hd_url = hd_url;
                    }
                }

                public static class PostLinksBean {
                    private String native_link;
                    private String android_native_link;
                    private String link;

                    public String getNative_link() {
                        return native_link;
                    }

                    public void setNative_link(String native_link) {
                        this.native_link = native_link;
                    }

                    public String getAndroid_native_link() {
                        return android_native_link;
                    }

                    public void setAndroid_native_link(String android_native_link) {
                        this.android_native_link = android_native_link;
                    }

                    public String getLink() {
                        return link;
                    }

                    public void setLink(String link) {
                        this.link = link;
                    }
                }

                public static class ImageBean {
                    private int height;
                    private int width;
                    private String url;

                    public int getHeight() {
                        return height;
                    }

                    public void setHeight(int height) {
                        this.height = height;
                    }

                    public int getWidth() {
                        return width;
                    }

                    public void setWidth(int width) {
                        this.width = width;
                    }

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }
                }

                public static class CustomDataBean {
                    private String source;

                    public String getSource() {
                        return source;
                    }

                    public void setSource(String source) {
                        this.source = source;
                    }
                }

                public static class BizBean {
                    private String biz_id;
                    private String containerid;

                    public String getBiz_id() {
                        return biz_id;
                    }

                    public void setBiz_id(String biz_id) {
                        this.biz_id = biz_id;
                    }

                    public String getContainerid() {
                        return containerid;
                    }

                    public void setContainerid(String containerid) {
                        this.containerid = containerid;
                    }
                }
            }
        }
    }
}
