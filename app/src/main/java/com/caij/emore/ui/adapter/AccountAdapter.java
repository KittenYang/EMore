package com.caij.emore.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.account.Account;
import com.caij.emore.bean.AccountInfo;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.BaseViewHolder;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/8/22.
 */
public class AccountAdapter extends BaseAdapter<AccountInfo, AccountAdapter.AccountViewHolder> {

    private ImageLoader.ImageConfig mAvatarImageConfig;

    public AccountAdapter(Context context) {
        super(context);
        mAvatarImageConfig = new ImageLoader.ImageConfigBuild().setCircle(true).build();
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AccountViewHolder(mInflater.inflate(R.layout.item_account, parent, false),
                mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(AccountViewHolder holder, int position) {
        AccountInfo accountInfo = getItem(position);
        ImageLoader.loadUrl(mContext, holder.ivAvatar, accountInfo.getUser().getAvatar_large(),
                R.drawable.circle_image_placeholder, mAvatarImageConfig);
        holder.tvName.setText(accountInfo.getUser().getName());
        holder.tvDesc.setText(accountInfo.getUser().getDescription());
        if (accountInfo.getAccount().getStatus() == Account.STATUS_USING) {
            holder.ivSelect.setVisibility(View.VISIBLE);
        }else {
            holder.ivSelect.setVisibility(View.GONE);
        }

        if (accountInfo.getAccount().getToken().isExpired()) {
            holder.txtTokenInfo.setVisibility(View.VISIBLE);
        }else {
            holder.txtTokenInfo.setVisibility(View.GONE);
        }
    }

    public static class AccountViewHolder extends BaseViewHolder {

        @BindView(R.id.iv_avatar)
        ImageView ivAvatar;
        @BindView(R.id.iv_select)
        ImageView ivSelect;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_desc)
        TextView tvDesc;
        @BindView(R.id.txtTokenInfo)
        TextView txtTokenInfo;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;

        public AccountViewHolder(View itemView, final RecyclerViewOnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            ButterKnife.bind(this, itemView);
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, getLayoutPosition());
                }
            });
        }

    }

}
