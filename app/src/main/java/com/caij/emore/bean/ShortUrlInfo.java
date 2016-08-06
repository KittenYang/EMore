package com.caij.emore.bean;

import java.util.List;

/**
 * Created by Caij on 2016/7/28.
 */
public class ShortUrlInfo {

    private List<UrlsBean> urls;

    public List<UrlsBean> getUrls() {
        return urls;
    }

    public void setUrls(List<UrlsBean> urls) {
        this.urls = urls;
    }

    public static class UrlsBean {
        private boolean result;
        private String title;
        private String description;
        private String url_short;
        private String url_long;
        private int type;

        private List<AnnotationsBean> annotations;

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
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

        public List<AnnotationsBean> getAnnotations() {
            return annotations;
        }

        public void setAnnotations(List<AnnotationsBean> annotations) {
            this.annotations = annotations;
        }

        public static class AnnotationsBean {
            private String object_type;

            private ObjectBean object;

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

            public String getObject_type() {
                return object_type;
            }

            public void setObject_type(String object_type) {
                this.object_type = object_type;
            }


            public ObjectBean getObject() {
                return object;
            }

            public void setObject(ObjectBean object) {
                this.object = object;
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

                private String target_url;
                private String url;
                /**
                 * biz_id : 230442
                 * containerid : 2304426662b518cf7ea2d771a7315955d41685
                 */

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

                public String getScid() {
                    return scid;
                }

                public void setScid(String scid) {
                    this.scid = scid;
                }

                public static class StreamBean {
                    private long duration;
                    private int height;
                    private int width;
                    private String format;
                    private String url;
                    private String hd_url;

                    public long getDuration() {
                        return duration;
                    }

                    public void setDuration(long duration) {
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
                    private String url;

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }
                }

            }
        }
    }
}
