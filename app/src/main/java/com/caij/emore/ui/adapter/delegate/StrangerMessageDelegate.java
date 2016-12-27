package com.caij.emore.ui.adapter.delegate;

import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;

/**
 * Created by Ca1j on 2016/12/27.
 */

public class StrangerMessageDelegate extends BaseItemViewDelegate<DirectMessage> {

    public StrangerMessageDelegate(OnItemPartViewClickListener onClickListener) {
        super(onClickListener);
    }

    @Override
    public int getItemViewLayoutId() {
        return 0;
    }

    @Override
    public void convert(BaseViewHolder baseViewHolder, DirectMessage directMessage, int i) {

    }

    @Override
    public boolean isForViewType(DirectMessage directMessage, int i) {
        return true;
    }

    @Override
    public void onCreateViewHolder(BaseViewHolder baseViewHolder) {

    }
}
