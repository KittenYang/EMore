package com.caij.emore.bean;

import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Caij on 2016/8/25.
 */
public class ShortUrl {

    public static final int TYPE_WEB = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_IMAGE = 39;
    public static final int TYPE_MUSIC = 2;
    public static final int TYPE_FULL_TEXT = 10;
    /**
     * short_url : http://t.cn/RtgcYZQ
     * ori_url : sinaweibo://browser?url=http%3A%2F%2Fm.weibo.cn%2Fclient%2Fversion&sinainternalbrowser=topnav&url_type=39&object_type=collection&pos=1
     * url_title : 查看图片
     * url_type_pic : http://h5.sinaimg.cn/upload/2015/01/21/20/timeline_card_small_photo.png
     * pic_infos : {"7116d0edgw1f75rcs6yg1j20p00dw3zt":{"thumbnail":{"url":"http://ww4.sinaimg.cn/thumbnail/7116d0edgw1f75rcs6yg1j20p00dw3zt.jpg","cut_type":1,"type":"JPEG","width":120,"height":66,"croped":false},"bmiddle":{"url":"http://ww4.sinaimg.cn/bmiddle/7116d0edgw1f75rcs6yg1j20p00dw3zt.jpg","cut_type":1,"type":"JPEG","width":440,"height":244,"croped":false},"large":{"url":"http://ww4.sinaimg.cn/large/7116d0edgw1f75rcs6yg1j20p00dw3zt.jpg","cut_type":1,"type":"JPEG","width":"900","height":"500","croped":false},"woriginal":{"url":"http://ww4.sinaimg.cn/woriginal/7116d0edgw1f75rcs6yg1j20p00dw3zt.jpg","cut_type":1,"type":"JPEG","width":"900","height":"500","croped":false}}}
     * pic_ids : ["7116d0edgw1f75rcs6yg1j20p00dw3zt"]
     * position : 2
     * url_type : 39
     * result : false
     * need_save_obj : 1
     */

    private String short_url;
    private String ori_url;
    private String url_title;
    private String url_type_pic;
    /**
     * 7116d0edgw1f75rcs6yg1j20p00dw3zt : {"thumbnail":{"url":"http://ww4.sinaimg.cn/thumbnail/7116d0edgw1f75rcs6yg1j20p00dw3zt.jpg","cut_type":1,"type":"JPEG","width":120,"height":66,"croped":false},"bmiddle":{"url":"http://ww4.sinaimg.cn/bmiddle/7116d0edgw1f75rcs6yg1j20p00dw3zt.jpg","cut_type":1,"type":"JPEG","width":440,"height":244,"croped":false},"large":{"url":"http://ww4.sinaimg.cn/large/7116d0edgw1f75rcs6yg1j20p00dw3zt.jpg","cut_type":1,"type":"JPEG","width":"900","height":"500","croped":false},"woriginal":{"url":"http://ww4.sinaimg.cn/woriginal/7116d0edgw1f75rcs6yg1j20p00dw3zt.jpg","cut_type":1,"type":"JPEG","width":"900","height":"500","croped":false}}
     */

    /**
     * pic_ids : ["7116d0edgw1f75rcs6yg1j20p00dw3zt"]
     * position : 2
     * url_type : 39
     * result : false
     * need_save_obj : 1
     */

    private int position;
    private int url_type;
    private boolean result;
    private int need_save_obj;
    private List<String> pic_ids;
    private HashMap<String, ImageInfo> pic_infos;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getUrl_type() {
        return url_type;
    }

    public int getObj_type() {
//        sinaweibo://article?object_id=1022:2309351000014012378592312697&url_type=39&object_type=article&pos=1 文章
//        sinaweibo://infopage?pageid=230442cd92b5f058137384ef938e6db0f82f78&showurl=http%3A%2F%2Fmiaopai.com
                // %2Fshow%2FJ53GkPTmaNlrsDB4hAww2A__.htm&url_open_direct=1&toolbar_hidden=1&url_type=39&object_type=video&pos=1  视频
//        sinaweibo://browser?url=http%3A%2F%2Fm.weibo.cn%2Fclient%2Fversion&sinainternalbrowser=topnav&share_menu=1 全文
//        sinaweibo://browser?url=http%3A%2F%2Fm.weibo.cn%2Fclient%2Fversion&sinainternalbrowser=topnav&url_type=39&object_type=collection&pos=1 //图片
        if (TextUtils.isEmpty(ori_url) || !ori_url.startsWith("sinaweibo://")) {
            return TYPE_WEB;
        }else {
            Uri uri = Uri.parse(ori_url);
            String type = uri.getQueryParameter("object_type");
            if ("article".equals(type)) {
                return TYPE_WEB;
            }else if ("video".equals(type)) {
                return TYPE_VIDEO;
            }else if ("collection".equals(type)) {
                return TYPE_IMAGE;
            }else if ("browser".equals(uri.getHost())) {
                return TYPE_FULL_TEXT;
            }else {
                return TYPE_WEB;
            }
        }
    }

    public void setUrl_type(int url_type) {
        this.url_type = url_type;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getNeed_save_obj() {
        return need_save_obj;
    }

    public void setNeed_save_obj(int need_save_obj) {
        this.need_save_obj = need_save_obj;
    }

    public List<String> getPic_ids() {
        return pic_ids;
    }

    public void setPic_ids(List<String> pic_ids) {
        this.pic_ids = pic_ids;
    }

    public String getShort_url() {
        return short_url;
    }

    public void setShort_url(String short_url) {
        this.short_url = short_url;
    }

    public String getOri_url() {
        return ori_url;
    }

    public void setOri_url(String ori_url) {
        this.ori_url = ori_url;
    }

    public String getUrl_title() {
        return url_title;
    }

    public void setUrl_title(String url_title) {
        this.url_title = url_title;
    }

    public String getUrl_type_pic() {
        return url_type_pic;
    }

    public void setUrl_type_pic(String url_type_pic) {
        this.url_type_pic = url_type_pic;
    }

    public HashMap<String, ImageInfo> getPic_infos() {
        return pic_infos;
    }

    public void setPic_infos(HashMap<String, ImageInfo> pic_infos) {
        this.pic_infos = pic_infos;
    }
}
