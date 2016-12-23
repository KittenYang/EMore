package com.caij.emore.ui.adapter.delegate;

import android.view.View;
import android.widget.ImageView;

import com.caij.emore.R;
import com.caij.emore.account.Account;
import com.caij.emore.bean.AccountInfo;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;

/**
 * Created by Ca1j on 2016/12/19.
 */

public class AccountDelegate extends BaseItemViewDelegate<AccountInfo> {


    private final ImageLoader.ImageConfig mAvatarImageConfig;

    public AccountDelegate(OnItemPartViewClickListener onClickListener) {
        super(onClickListener);
        mAvatarImageConfig = new ImageLoader.ImageConfigBuild().setCircle(true).build();
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_account;
    }

    @Override
    public void convert(BaseViewHolder baseViewHolder, AccountInfo accountInfo, int i) {
        ImageView ivAvatar = baseViewHolder.getView(R.id.iv_avatar);
        ImageLoader.loadUrl(baseViewHolder.getConvertView().getContext(), ivAvatar, accountInfo.getUser().getAvatar_large(),
                R.drawable.circle_image_placeholder, mAvatarImageConfig);
        baseViewHolder.setText(R.id.tv_name, accountInfo.getUser().getName());
        baseViewHolder.setText(R.id.tv_desc, accountInfo.getUser().getDescription());

        baseViewHolder.setVisible(R.id.iv_select, accountInfo.getAccount().getStatus() == Account.STATUS_USING);
        baseViewHolder.setVisible(R.id.txtTokenInfo, accountInfo.getAccount().getToken().isExpired());
    }

    @Override
    public boolean isForViewType(AccountInfo accountInfo, int i) {
        return true;
    }

    @Override
    public void onCreateViewHolder(final BaseViewHolder baseViewHolder) {
        baseViewHolder.setOnClickListener(R.id.iv_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemPartViewClickListener != null) {
                    mOnItemPartViewClickListener.onClick(v, baseViewHolder.getAdapterPosition());
                }
            }
        });
    }
}
