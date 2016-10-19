package com.caij.emore.present;


import com.caij.emore.database.bean.DirectMessage;

import java.util.ArrayList;

/**
 * Created by Caij on 2016/7/10.
 */
public interface ChatPresent extends ListPresent {

    void sendTextMessage(String message);

    void sendImageMessage(ArrayList<String> paths);

    void sendMessage(DirectMessage directMessage);

    String getMessageImageHdUrl(DirectMessage directMessage);
}
