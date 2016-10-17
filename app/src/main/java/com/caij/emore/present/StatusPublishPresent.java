package com.caij.emore.present;

import java.util.ArrayList;

/**
 * Created by Caij on 2016/6/24.
 */
public interface StatusPublishPresent extends BasePresent {

    public void publishStatus(long id, final String content, ArrayList<String> imagePaths);

    void saveToDraft(final long id, String content, ArrayList<String> images);
}
