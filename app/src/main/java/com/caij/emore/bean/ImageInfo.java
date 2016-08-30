package com.caij.emore.bean;

/**
 * Created by Caij on 2016/8/25.
 */
public class ImageInfo {

    /**
     * url : http://ww4.sinaimg.cn/wap180/7116d0edgw1f72hg7h6u4j20go09edgd.jpg
     * width : 180
     * height : 101
     * cut_type : 1
     * type : JPEG
     */

    private Image thumbnail;
    /**
     * url : http://ww4.sinaimg.cn/wap360/7116d0edgw1f72hg7h6u4j20go09edgd.jpg
     * width : 360
     * height : 202
     * cut_type : 1
     * type : JPEG
     */

    private Image bmiddle;
    /**
     * url : http://ww4.sinaimg.cn/wap720/7116d0edgw1f72hg7h6u4j20go09edgd.jpg
     * width : 600
     * height : 338
     * cut_type : 1
     * type : JPEG
     */

    private Image large;
    /**
     * url : http://ww4.sinaimg.cn/woriginal/7116d0edgw1f72hg7h6u4j20go09edgd.jpg
     * width : 600
     * height : 338
     * cut_type : 1
     * type : JPEG
     */

    private Image original;
    /**
     * url : http://ww4.sinaimg.cn/large/7116d0edgw1f72hg7h6u4j20go09edgd.jpg
     * width : 600
     * height : 338
     * cut_type : 1
     * type : JPEG
     */

    private Image largest;
    /**
     * thumbnail : {"url":"http://ww4.sinaimg.cn/wap180/7116d0edgw1f72hg7h6u4j20go09edgd.jpg","width":180,"height":101,"cut_type":1,"type":"JPEG"}
     * bmiddle : {"url":"http://ww4.sinaimg.cn/wap360/7116d0edgw1f72hg7h6u4j20go09edgd.jpg","width":360,"height":202,"cut_type":1,"type":"JPEG"}
     * large : {"url":"http://ww4.sinaimg.cn/wap720/7116d0edgw1f72hg7h6u4j20go09edgd.jpg","width":"600","height":"338","cut_type":1,"type":"JPEG"}
     * original : {"url":"http://ww4.sinaimg.cn/woriginal/7116d0edgw1f72hg7h6u4j20go09edgd.jpg","width":"600","height":"338","cut_type":1,"type":"JPEG"}
     * largest : {"url":"http://ww4.sinaimg.cn/large/7116d0edgw1f72hg7h6u4j20go09edgd.jpg","width":"600","height":"338","cut_type":1,"type":"JPEG"}
     * object_id : 1042018:e053d6abe47b6bd031b787b7fd3245ab
     * pic_id : 7116d0edgw1f72hg7h6u4j20go09edgd
     * photo_tag : 0
     * type : pic
     * pic_status : 0
     */

    private String object_id;
    private String pic_id;
    private int photo_tag;
    private String type;
    private int pic_status;

    public Image getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Image getBmiddle() {
        return bmiddle;
    }

    public void setBmiddle(Image bmiddle) {
        this.bmiddle = bmiddle;
    }

    public Image getLarge() {
        return large;
    }

    public void setLarge(Image large) {
        this.large = large;
    }

    public Image getOriginal() {
        return original;
    }

    public void setOriginal(Image original) {
        this.original = original;
    }

    public Image getLargest() {
        return largest;
    }

    public void setLargest(Image largest) {
        this.largest = largest;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public String getPic_id() {
        return pic_id;
    }

    public void setPic_id(String pic_id) {
        this.pic_id = pic_id;
    }

    public int getPhoto_tag() {
        return photo_tag;
    }

    public void setPhoto_tag(int photo_tag) {
        this.photo_tag = photo_tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPic_status() {
        return pic_status;
    }

    public void setPic_status(int pic_status) {
        this.pic_status = pic_status;
    }

    public static class Image {

        private String url;
        private int width;
        private int height;
        private int cut_type;
        private String type;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getCut_type() {
            return cut_type;
        }

        public void setCut_type(int cut_type) {
            this.cut_type = cut_type;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}
