package com.caij.emore.api.ex;

import com.caij.emore.bean.response.Response;

import java.io.IOException;

/**
 * Created by Caij on 2016/8/3.
 */
public class ErrorResponseException extends IllegalStateException {

    public Response mResponse;

    public  ErrorResponseException(Response response) {
        mResponse = response;
    }
}
