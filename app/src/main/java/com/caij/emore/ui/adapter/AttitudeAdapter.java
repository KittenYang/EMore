package com.caij.emore.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.bean.Attitude;
import com.caij.emore.database.bean.User;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.BaseViewHolder;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/7/21.
 */
public class AttitudeAdapter extends BaseAdapter<User, AttitudeAdapter.AttitudeViewHolder> {

    private final ImageLoader.ImageConfig mImageConfig;

    public AttitudeAdapter(Context context) {
        super(context);
        mImageConfig = new ImageLoader.ImageConfigBuild().
                setCircle(true).
                setScaleType(ImageLoader.ScaleType.CENTER_CROP).
                build();
    }

    @Override
    public AttitudeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_weibo_attitude, parent, false);
        return new AttitudeViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(AttitudeViewHolder holder, int position) {
        User user = getItem(position);
        holder.tvName.setText(user.getScreen_name());
        ImageLoader.loadUrl(mContext, holder.ivAvatar,user.getAvatar_large(),
                R.drawable.circle_image_placeholder, mImageConfig);
    }

    public static class AttitudeViewHolder extends BaseViewHolder {

        @BindView(R.id.iv_avatar)
        ImageView ivAvatar;
        @BindView(R.id.tv_name)
        TextView tvName;

        public AttitudeViewHolder(View itemView, RecyclerViewOnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            ButterKnife.bind(this, itemView);
        }
    }

}
