package com.caij.emore.ui.adapter.delegate;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.bean.Comment;
import com.caij.emore.database.bean.Status;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;

/**
 * Created by Ca1j on 2016/12/20.
 */

public class RepostDelegate extends BaseItemViewDelegate<Status> {

    private final ImageLoader.ImageConfig mAvatarImageConfig;

    public RepostDelegate(OnItemPartViewClickListener onClickListener) {
        super(onClickListener);
        mAvatarImageConfig = new ImageLoader.ImageConfigBuild().setCircle(true).build();
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_weibo_comment;
    }

    @Override
    public void convert(BaseViewHolder baseViewHolder, Status status, int i) {

        TextView textView = baseViewHolder.getView(R.id.txtContent);
        textView.setText(status.getContentSpannableString());

        String createAt = DateUtil.convWeiboDate(textView.getContext(), status.getCreated_at().getTime());
        String from = String.format("%s", Html.fromHtml(status.getSource()));
        String desc = String.format("%s %s", createAt, from);

        baseViewHolder.setText(R.id.txtDesc, desc);

        baseViewHolder.setText(R.id.txtName, status.getUser().getName());

        ImageView imageView = baseViewHolder.getView(R.id.imgPhoto);

        ImageLoader.loadUrl(baseViewHolder.getConvertView().getContext(), imageView, status.getUser().getAvatar_large(),
                R.drawable.circle_image_placeholder, mAvatarImageConfig);
    }

    @Override
    public boolean isForViewType(Status status, int i) {
        return true;
    }

    @Override
    public void onCreateViewHolder(final BaseViewHolder baseViewHolder) {
        baseViewHolder.setOnClickListener(R.id.imgPhoto, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemPartViewClickListener.onClick(v, baseViewHolder.getAdapterPosition());
            }
        });
    }
}
