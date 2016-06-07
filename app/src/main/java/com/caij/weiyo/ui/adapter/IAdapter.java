package com.caij.weiyo.ui.adapter;

import java.util.List;

/**
 * Created by Caij on 2015/9/23.
 */
public interface IAdapter<E> {
    public void addEntity(E entity);
    public void removeEntity(E entiry);
    public void removeEntities(List<E> entities);
    public void addEntities(List<E> entities);
}
