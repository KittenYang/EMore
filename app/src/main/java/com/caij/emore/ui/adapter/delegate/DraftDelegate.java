package com.caij.emore.ui.adapter.delegate;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.caij.emore.R;
import com.caij.emore.database.bean.Draft;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;

/**
 * Created by Ca1j on 2016/12/20.
 */

public class DraftDelegate extends BaseItemViewDelegate<Draft> {

    public DraftDelegate(OnItemPartViewClickListener onClickListener) {
        super(onClickListener);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_draft;
    }

    @Override
    public void convert(BaseViewHolder baseViewHolder, Draft draft, int i) {
        Context context = baseViewHolder.getConvertView().getContext();

        if (draft.getType() == Draft.TYPE_WEIBO) {
            baseViewHolder.setText(R.id.txtType, context.getString(R.string.weibo));
        }
        baseViewHolder.setText(R.id.txtContent, draft.getContent());
        baseViewHolder.setText(R.id.txtTiming, DateUtil.convWeiboDate(context, draft.getCreate_at()));

        if (draft.getImages() != null && draft.getImages().size() > 0) {
            baseViewHolder.setVisible(R.id.iv_image, true);
            ImageView imageView = baseViewHolder.getView(R.id.iv_image);
            ImageLoader.load(context, imageView,
                    "file://" + draft.getImages().get(0), R.drawable.weibo_image_placeholder);
        }else {
            baseViewHolder.setVisible(R.id.iv_image, false);
        }
    }

    @Override
    public boolean isForViewType(Draft draft, int i) {
        return true;
    }

    @Override
    public void onCreateViewHolder(final BaseViewHolder baseViewHolder) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemPartViewClickListener.onClick(v, baseViewHolder.getAdapterPosition());
            }
        };
        baseViewHolder.setOnClickListener(R.id.btnResend, onClickListener);
        baseViewHolder.setOnClickListener(R.id.btnDel, onClickListener);
    }
}
