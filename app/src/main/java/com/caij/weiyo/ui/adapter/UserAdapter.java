package com.caij.weiyo.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.weiyo.R;
import com.caij.weiyo.bean.User;
import com.caij.weiyo.utils.ImageLoader;
import com.caij.weiyo.view.recyclerview.BaseAdapter;
import com.caij.weiyo.view.recyclerview.BaseViewHolder;
import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/7/3.
 */
public class UserAdapter extends BaseAdapter<User, UserAdapter.UserViewHolder> {


    private ImageLoader.ImageConfig mImageConfig;

    public UserAdapter(Context context) {
        super(context);
        mImageConfig = new ImageLoader.ImageConfigBuild().
                setScaleType(ImageLoader.ScaleType.CENTER_CROP)
                .setCircle(true)
                .build();
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_friendship, parent, false);
        return new UserViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = getItem(position);
        holder.txtName.setText(user.getScreen_name());
        holder.txtDesc.setText(user.getDescription());
        ImageLoader.load(mContext, holder.imgPhoto, user.getAvatar_large(),
                R.drawable.circle_image_placeholder, mImageConfig);
    }

    public static class UserViewHolder extends BaseViewHolder {

        @BindView(R.id.imgPhoto)
        ImageView imgPhoto;
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtDesc)
        TextView txtDesc;

        public UserViewHolder(View itemView, RecyclerViewOnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            ButterKnife.bind(this, itemView);
        }
    }
}
