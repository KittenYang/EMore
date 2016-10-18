package com.caij.emore.manager;

import com.caij.emore.database.bean.Status;

import java.util.List;

/**
 * Created by Caij on 2016/10/9.
 */

public interface StatusManager {

    /**

     * @param uid
     * @param since_id
     * @param max_id
     * @param count
     * @param page
     * @return
     */
    public List<Status> getFriendStatuses(final long uid, final long since_id, final long max_id,
                                          final int count, final int page);

    public void saveStatuses(List<Status> statuses);

    public void saveStatus(Status status);

    public void deleteStatus(final long id);

    public Status getStatusById(long id);

}
