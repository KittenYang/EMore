package com.caij.weiyo.present.view;

import java.util.List;

/**
 * Created by Caij on 2016/7/4.
 */
public interface MentionView<E> extends RefreshListView {

    public void setEntities(List<E> entities);

    void toRefresh();
}
