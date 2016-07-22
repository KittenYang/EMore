package com.caij.emore.present.view;

import com.caij.emore.bean.Attitude;

import java.util.List;

/**
 * Created by Caij on 2016/7/22.
 */
public interface WeiboAttitudesView extends BaseListView<Attitude> {
    void onAttitudeSuccess(List<Attitude> attitudes);
}
