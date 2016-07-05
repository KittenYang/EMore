package com.caij.weiyo.present.view;

import com.caij.weiyo.bean.PicUrl;
import com.caij.weiyo.bean.Weibo;

import java.util.List;

/**
 * Created by Caij on 2016/5/31.
 */
public interface TimeLineWeiboImageView extends RefreshListView {

    public void setImages(List<PicUrl> picUrls);

}
