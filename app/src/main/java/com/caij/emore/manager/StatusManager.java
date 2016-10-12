package com.caij.emore.manager;

import com.caij.emore.database.bean.Status;

import java.util.List;

/**
 * Created by Caij on 2016/10/9.
 */

public interface StatusManager {

    public List<Status> getFriendStatuses(final long uid, final long since_id, final long max_id,
                                          final int count, final int page);

    public void saveStatuses(List<Status> weibos);

    public void saveStatus(Status weibo);

    public void deleteStatus(final long id);

    public Status getStatusById(long id);
}
