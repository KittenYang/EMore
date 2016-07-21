package com.caij.emore.view.recyclerview;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.caij.emore.database.bean.Weibo;
import com.caij.emore.ui.adapter.IAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Caij on 2015/9/22.
 */
public abstract class BaseAdapter<E, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> implements IAdapter<E> {

    private static final String TAG = "BaseAdapter";

    private List<E> mEntities;

    protected Context mContext;

    protected LayoutInflater mInflater;

    protected RecyclerViewOnItemClickListener mOnItemClickListener;

    public BaseAdapter(Context context) {
        this(context, null);
    }

    public BaseAdapter(Context context, List<E> entities) {
        this.mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mEntities = entities;
    }

    public void setOnItemClickListener(RecyclerViewOnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public E getItem(int i) {
        return mEntities.get(i);
    }

    public void setEntities(List<E> entities) {
        mEntities = entities;
    }

    public void addEntities(List<E> entities) {
        if (mEntities == null) {
            mEntities = entities;
        } else {
            mEntities.addAll(entities);
        }
    }

    public List<E> getEntities() {
        return mEntities;
    }

    public void addEntity(E entity) {
        if (mEntities == null) {
            mEntities = new ArrayList<>();
        }
        mEntities.add(entity);
    }

    public void removeEntity(E entiry) {
        if (mEntities != null) {
            mEntities.remove(entiry);
        }
    }

    public void removeEntities(List<E> entities) {
        if (mEntities != null) {
            mEntities.removeAll(entities);
        }
    }

    public void clearEntities() {
        if (mEntities != null) {
            mEntities.clear();
        }
    }

    @Override
    public int getItemCount() {
        return mEntities == null ? 0 : mEntities.size();
    }

    public void addEntity(int index, E entity) {
        mEntities.add(index, entity);
    }

    public void addEntities(int index, List<E> entities) {
        if (mEntities == null) {
            mEntities = entities;
        } else {
            mEntities.addAll(index, entities);
        }
    }
}
