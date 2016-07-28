package com.caij.emore.bean;

/**
 * Created by Caij on 2016/7/28.
 */
public class VideoInfo {


    /**
     * data : http://us.sinaimg.cn/004ikHdGjx072zZ4HkYn050401003wd80k01.mp4?KID=unistore,video&Expires=1469690829&ssig=LY0ywLOtFO
     * info :
     * retcode : 0
     */

    private String data;
    private String info;
    private int retcode;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getRetcode() {
        return retcode;
    }

    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }
}
