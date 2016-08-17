package com.caij.emore.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.database.bean.Draft;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.BaseViewHolder;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/7/20.
 */
public class DraftAdapter extends BaseAdapter<Draft, DraftAdapter.DraftViewHolder> {

    public DraftAdapter(Context context) {
        super(context);
    }

    @Override
    public DraftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_draft, parent, false);
        return new DraftViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(DraftViewHolder holder, int position) {
        Draft draft = getItem(position);
        if (draft.getType() == Draft.TYPE_WEIBO) {
            holder.txtType.setText(R.string.weibo);
        }
        holder.txtContent.setText(draft.getContent());
        holder.txtTiming.setText(DateUtil.convWeiboDate(mContext, draft.getCreate_at()));
        if (draft.getImages() != null && draft.getImages().size() > 0) {
            holder.ivImage.setVisibility(View.VISIBLE);
            ImageLoader.load(mContext, holder.ivImage,
                    "file://" + draft.getImages().get(0), R.drawable.weibo_image_placeholder);
        }else {
            holder.ivImage.setVisibility(View.GONE);
        }
    }

    public static class DraftViewHolder extends BaseViewHolder {

        @BindView(R.id.txtType)
        TextView txtType;
        @BindView(R.id.txtTiming)
        TextView txtTiming;
        @BindView(R.id.iv_image)
        ImageView ivImage;
        @BindView(R.id.txtContent)
        TextView txtContent;
        @BindView(R.id.btnDel)
        LinearLayout btnDel;
        @BindView(R.id.btnResend)
        LinearLayout btnResend;

        public DraftViewHolder(View itemView, RecyclerViewOnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            ButterKnife.bind(this, itemView);
            btnDel.setOnClickListener(this);
            btnResend.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
            if (v.getId() == R.id.btnDel || v.getId() == R.id.btnResend) {
                mOnItemClickListener.onItemClick(v, getLayoutPosition());
            }
        }
    }

}
