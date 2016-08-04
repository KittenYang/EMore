package com.caij.emore.utils.rxjava;

import com.caij.emore.bean.response.Response;

/**
 * Created by Caij on 2016/8/3.
 */
public class ErrorResponseException extends RuntimeException {

    public Response mResponse;

    public  ErrorResponseException(Response response) {
        mResponse = response;
    }
}
