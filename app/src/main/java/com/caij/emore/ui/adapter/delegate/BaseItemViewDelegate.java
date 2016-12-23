package com.caij.emore.ui.adapter.delegate;

import android.view.View;

import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.delegate.ItemViewDelegate;

/**
 * Created by Ca1j on 2016/12/19.
 */

public abstract class BaseItemViewDelegate<E> implements ItemViewDelegate<E> {

    protected OnItemPartViewClickListener mOnItemPartViewClickListener;

    public BaseItemViewDelegate(OnItemPartViewClickListener onClickListener) {
        mOnItemPartViewClickListener = onClickListener;
    }
}
