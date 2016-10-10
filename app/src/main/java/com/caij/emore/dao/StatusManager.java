package com.caij.emore.dao;

import com.caij.emore.database.bean.Weibo;

import java.util.List;

/**
 * Created by Caij on 2016/10/9.
 */

public interface StatusManager {

    public List<Weibo> getFriendWeibo(final long uid, final long since_id, final long max_id,
                                      final int count, final int page);

    public void saveWeibos(List<Weibo> weibos);

    public void saveWeibo(Weibo weibo);

    public void deleteWeibo(final long id);

    public Weibo getWeiboById(long id);
}
