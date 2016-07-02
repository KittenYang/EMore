package com.caij.weiyo.bean.response;

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
}
