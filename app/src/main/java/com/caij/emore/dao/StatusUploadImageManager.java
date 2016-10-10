package com.caij.emore.dao;

import com.caij.emore.database.bean.UploadImageResponse;

/**
 * Created by Caij on 2016/10/10.
 */

public interface StatusUploadImageManager {

    public void insert(UploadImageResponse uploadImageResponse);

    public UploadImageResponse getByPath(String path);
}
