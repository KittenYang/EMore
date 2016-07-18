//package com.caij.emore.bean;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.Serializable;
//
//public class ShortLongLink
//        implements Serializable {
//    private static final long serialVersionUID = 2774937201090145642L;
//
//    public static final int TYPE_WEB = 0;
//    public static final int TYPE_VIDEO = 1;
//    public static final int TYPE_MUSIC = 2;
//    public static final int TYPE_IMAGE = 39;
//
//    public String description;
//    public String longUrl;
//    public String object_type;
//    public String shortUrl;
//    public boolean shortUrlAvailable;
//    public String title;
//    public int transcode;
//    public int type = -1;
//    public String display_name;
//
//    public ShortLongLink(String jsonString) throws JSONException {
//        this(new JSONObject(jsonString));
//    }
//
//    public ShortLongLink(JSONObject paramJSONObject) {
//        if (paramJSONObject != null) {
//            this.title = paramJSONObject.optString("title");
//            this.description = paramJSONObject.optString("description");
//            this.shortUrl = paramJSONObject.optString("url_short");
//            this.longUrl = paramJSONObject.optString("url_long");
//            this.type = paramJSONObject.optInt("type", -1);
//            this.shortUrlAvailable = paramJSONObject.optBoolean("result");
//            this.transcode = paramJSONObject.optInt("transcode");
//            JSONArray localJSONArray = paramJSONObject.optJSONArray("annotations");
//            if ((localJSONArray != null) && (localJSONArray.length() > 0)) {
//                JSONObject localJSONObject = localJSONArray.optJSONObject(0);
//                if (localJSONObject != null) {
//                    this.object_type = localJSONObject.optString("object_type");
//                    JSONObject jsonObject = localJSONObject.optJSONObject("object");
//                    if (jsonObject != null) {
//                        this.display_name = jsonObject.optString("display_name");
//                    }
//                }
//            }
//            getUrlType();
//        }
//    }
//
////  private boolean isVideoType(String paramString)
////  {
////    return PattenUtil.matchVideoUrl(paramString);
////  }
////
////  private boolean matchMusicType(String paramString)
////  {
////    return PattenUtil.matchMusicUrl(paramString);
////  }
//
//    public int getUrlType() {
//        if ("video".equalsIgnoreCase(this.object_type)) {
//            return TYPE_VIDEO;
//        }
//        return type;
//    }
//
//    public String toString() {
//        return "[shortUrl = " + this.shortUrl + ", longUrl = " + this.longUrl + ", type = " + this.type + ", shortUrlAvailable = " + this.shortUrlAvailable + "]";
//    }
//
//}
