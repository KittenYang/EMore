package com.caij.emore.manager;

import com.caij.emore.database.bean.DirectMessage;

import java.util.List;

/**
 * Created by Caij on 2016/10/9.
 */

public interface MessageManager {

    public List<DirectMessage> getUserMessage(final long toUid, final long selfUid,
                                             final long since_id, final long max_id,
                                             final int count, final int page);

    public void saveMessage(DirectMessage message);

    public void deleteMessageById(long id);

}
