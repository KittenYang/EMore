package com.caij.weiyo.present;

import com.caij.weiyo.bean.Weibo;

import java.util.ArrayList;

/**
 * Created by Caij on 2016/6/24.
 */
public interface WeiboPublishPresent extends BasePresent {

    public void publishWeibo( final String content, ArrayList<String> imagePaths);
}
