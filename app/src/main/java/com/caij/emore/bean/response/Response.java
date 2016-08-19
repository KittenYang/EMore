package com.caij.emore.bean.response;

import android.text.TextUtils;

/**
 * Created by Caij on 2016/6/29.
 */
public class Response {

//    error: "request uid's value must be the current user"
//    error_code: 21335
//    request: "/2/statuses/user_timeline.json"

    private String error;
    private int error_code;
    private String request;

    ///---------------下面是cn 域名返回的错误码， 新浪设计的太烂

    private int errno;
    private String errmsg;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public boolean isSuccessful() {
        return error_code == 0 && TextUtils.isEmpty(error)
                && errno == 0 && TextUtils.isEmpty(errmsg);
    }
}
