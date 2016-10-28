package com.caij.emore.present;

import com.caij.emore.database.bean.Status;

/**
 * Created by Caij on 2016/7/14.
 */
public interface StatusActionPresent extends BasePresent{


    void deleteStatus(Status weibo, int position);

    void collectStatus(Status weibo);

    void unCollectStatus(Status weibo);

    void attitudeStatus(Status weibo);

    void destroyAttitudeStatus(Status weibo);
}
