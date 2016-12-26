package com.caij.emore.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.ui.adapter.delegate.MessageDelegateProvider;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;
import com.caij.rvadapter.adapter.MultiItemTypeAdapter;

import java.util.Map;


/**
 * Created by Caij on 2016/7/10.
 */
public class MessageAdapter extends MultiItemTypeAdapter<DirectMessage> {

    private Map<String, DirectMessage> mShowTimeMessageMap;


    public MessageAdapter(Context context, OnItemPartViewClickListener onItemPartViewClickListener) {
        super(context);
        addItemViewDelegate(new MessageDelegateProvider.OutImageMessageDelegate(onItemPartViewClickListener));
        addItemViewDelegate(new MessageDelegateProvider.OutTextMessageDelegate(onItemPartViewClickListener));
        addItemViewDelegate(new MessageDelegateProvider.ReceiveImageMessageDelegate(onItemPartViewClickListener));
        addItemViewDelegate(new MessageDelegateProvider.ReceiveTextMessageDelegate(onItemPartViewClickListener));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        DirectMessage directMessage = getItem(position);

        TextView tvTime = holder.getView(R.id.tv_send_time);
        if (mShowTimeMessageMap != null && mShowTimeMessageMap.get(directMessage.getIdstr()) != null) {
            tvTime.setText(DateUtil.convMessageDate(mContext, directMessage.getCreated_at().getTime()));
            tvTime.setVisibility(View.VISIBLE);
        }else {
            tvTime.setVisibility(View.GONE);
        }
    }

    public void setShowTimeMessageMap(Map<String, DirectMessage> showTimeMessageMap) {
        mShowTimeMessageMap = showTimeMessageMap;
    }
}
