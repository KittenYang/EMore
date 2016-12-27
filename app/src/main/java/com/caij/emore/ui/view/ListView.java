package com.caij.emore.ui.view;

import java.util.List;

/**
 * Created by Caij on 2016/6/16.
 */
public interface ListView<E> extends BaseView{

    void onLoadComplete(boolean isHaveMore);

    void setEntities(List<E> entities);

    void showErrorView();

    void notifyItemChanged(List<E> entities, int index);

    void notifyItemRemoved(List<E> entities, int index);

    /**
     * @param position
     * @param count
     * @param entities 这个是全部的数据，而不是添加的数据
     */
    void notifyItemRangeInserted(List<E> entities, int position, int count);

}
