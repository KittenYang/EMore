package com.caij.emore.present.view;

import java.util.List;

/**
 * Created by Caij on 2016/6/16.
 */
public interface BaseListView<E> extends BaseView{

    void onLoadComplete(boolean isHaveMore);

    void setEntities(List<E> entities);

    void showErrorView();

    void updatePositionDate(int index);
}
