package com.caij.emore.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.caij.emore.R;
import com.caij.emore.bean.ThemeItem;
import com.caij.emore.widget.CircleView;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.BaseViewHolder;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/9/18.
 */
public class ThemeAdapter extends BaseAdapter<ThemeItem, ThemeAdapter.ViewHolder> {

    public ThemeAdapter(Context context) {
        super(context);
    }

    public ThemeAdapter(Context context, List<ThemeItem> entities) {
        super(context, entities);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_theme, parent, false), mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ThemeItem themeItem = getItem(position);
        holder.circleView.setColor(themeItem.getColor());
        if (themeItem.isSelect()) {
            holder.tvSelect.setVisibility(View.VISIBLE);
        }else {
            holder.tvSelect.setVisibility(View.GONE);
        }
    }

    public static class ViewHolder extends BaseViewHolder {

        @BindView(R.id.circle_view)
        CircleView circleView;
        @BindView(R.id.tv_select)
        ImageView tvSelect;

        public ViewHolder(View itemView, RecyclerViewOnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            ButterKnife.bind(this, itemView);
        }
    }

}
