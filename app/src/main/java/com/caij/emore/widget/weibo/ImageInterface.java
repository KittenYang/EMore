package com.caij.emore.widget.weibo;


import com.caij.emore.bean.StatusImageInfo;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Caij on 2016/6/13.
 */
public interface ImageInterface {
    public void setPics(List<String> pic_ids, LinkedHashMap<String, StatusImageInfo> imageInfoLinkedHashMap);
}
