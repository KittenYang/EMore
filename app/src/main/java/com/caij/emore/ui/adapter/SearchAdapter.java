package com.caij.emore.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.bean.SinaSearchRecommend;
import com.caij.emore.utils.CountUtil;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.BaseViewHolder;
import com.caij.emore.view.recyclerview.RecyclerViewOnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/7/26.
 */
public class SearchAdapter extends BaseAdapter<SinaSearchRecommend.RecommendData, SearchAdapter.SearchViewHolder> {

    public SearchAdapter(Context context) {
        super(context);
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_search, parent, false);
        return new SearchViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        SinaSearchRecommend.RecommendData data = getItem(position);
        holder.textViewItemText.setText(data.getKey());
        holder.tvCount.setText(CountUtil.getCounter(mContext, Integer.parseInt(data.getCount())));
    }

    public static class SearchViewHolder extends BaseViewHolder {

        @BindView(R.id.textView_item_text)
        TextView textViewItemText;
        @BindView(R.id.tv_count)
        TextView tvCount;
        @BindView(R.id.imageView_item_icon_left)
        ImageView leftImage;

        public SearchViewHolder(View itemView, RecyclerViewOnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            ButterKnife.bind(this, itemView);
            leftImage.setImageResource(com.lapism.searchview.R.drawable.search_ic_search_black_24dp);
        }
    }

}
