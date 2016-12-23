package com.caij.emore.ui.adapter.delegate;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.caij.emore.R;
import com.caij.emore.bean.Attitude;
import com.caij.emore.database.bean.Status;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;

/**
 * Created by Ca1j on 2016/12/22.
 */

public class ToMeAttitudeDelegate extends BaseItemViewDelegate<Attitude> {

    private final ImageLoader.ImageConfig mImageConfig;

    public ToMeAttitudeDelegate(OnItemPartViewClickListener onClickListener) {
        super(onClickListener);
        mImageConfig = new ImageLoader.ImageConfigBuild().setScaleType(ImageLoader.ScaleType.CENTER_CROP)
                .setCircle(true).build();
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_comment_mention;
    }

    @Override
    public void convert(BaseViewHolder baseViewHolder, Attitude attitude, int i) {
        Context context = baseViewHolder.getConvertView().getContext();
        ImageView avatarImageView = baseViewHolder.getView(R.id.sdv_avatar);
        ImageLoader.loadUrl(context, avatarImageView, attitude.getUser().getAvatar_large(),
                R.drawable.circle_image_placeholder, mImageConfig);

        baseViewHolder.setText(R.id.tv_name, attitude.getUser().getName());

        if (attitude.getAttitude_type() == 0) {
            baseViewHolder.setText(R.id.tv_comment, "赞了这条微博");
        }else {
            baseViewHolder.setText(R.id.tv_comment, "");
        }

        Status status = attitude.getStatus();

        baseViewHolder.setText(R.id.tv_status_name, "@" + status.getUser().getName());
        baseViewHolder.setText(R.id.tv_status, status.getText());

        ImageView statusImageView = baseViewHolder.getView(R.id.image_view);
        ImageLoader.load(context, statusImageView,
                status.getBmiddle_pic() != null ?  status.getBmiddle_pic() : status.getUser().getAvatar_large(),
                R.drawable.weibo_image_placeholder);

        String createAt = DateUtil.convWeiboDate(context, status.getCreated_at().getTime());
        String from = "";
        if (!TextUtils.isEmpty(status.getSource()))
            from = String.format("%s", Html.fromHtml(status.getSource()));
        String desc = String.format("%s %s", createAt, from);
        baseViewHolder.setText(R.id.tv_source, desc);
    }

    @Override
    public boolean isForViewType(Attitude attitude, int i) {
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
        baseViewHolder.setOnClickListener(R.id.item_bottom, onClickListener);
        baseViewHolder.setOnClickListener(R.id.tv_reply, onClickListener);
        baseViewHolder.setOnClickListener(R.id.sdv_avatar, onClickListener);

        baseViewHolder.setVisible(R.id.tv_reply, false);
    }
}
