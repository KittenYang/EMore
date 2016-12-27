package com.caij.emore.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.caij.emore.R;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.present.StrangerMessagePresent;
import com.caij.emore.present.imp.ChatPresentImp;
import com.caij.emore.present.imp.StrangerMessagePresentImp;
import com.caij.emore.remote.imp.MessageApiImp;
import com.caij.emore.ui.activity.DefaultFragmentActivity;
import com.caij.emore.ui.activity.ImagePrewActivity;
import com.caij.emore.ui.activity.UserInfoActivity;
import com.caij.emore.ui.adapter.delegate.MessageDelegateProvider;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;
import com.caij.rvadapter.adapter.BaseAdapter;
import com.caij.rvadapter.adapter.MultiItemTypeAdapter;

import java.util.ArrayList;

/**
 * Created by Ca1j on 2016/12/26.
 */

public class StrangerMessageFragment extends SwipeRefreshRecyclerViewFragment<DirectMessage, StrangerMessagePresent> implements OnItemPartViewClickListener {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                mPresent.refresh();
            }
        });
    }

    @Override
    protected BaseAdapter<DirectMessage, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        MultiItemTypeAdapter<DirectMessage> messageMultiItemTypeAdapter = new MultiItemTypeAdapter<DirectMessage>(getActivity());
        messageMultiItemTypeAdapter.addItemViewDelegate(new MessageDelegateProvider.ReceiveImageMessageDelegate(this));
        messageMultiItemTypeAdapter.addItemViewDelegate(new MessageDelegateProvider.ReceiveTextMessageDelegate(this));
        return messageMultiItemTypeAdapter;
    }

    @Override
    protected StrangerMessagePresent createPresent() {
        return new StrangerMessagePresentImp(new MessageApiImp(), this);
    }

    @Override
    public void onItemClick(View view, int position) {
        final DirectMessage directMessage = mRecyclerViewAdapter.getItem(position);
        DialogUtil.showItemDialog(getActivity(), null, new String[]{getString(R.string.reply)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = DefaultFragmentActivity.starFragmentV4(getActivity(), directMessage.getSender_screen_name(), ChatFragment.class,
                        ChatFragment.newInstance(directMessage.getSender_id()).getArguments());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        final DirectMessage directMessage = mRecyclerViewAdapter.getItem(position);
        if (view.getId() == R.id.iv_avatar) {
            Intent intent = UserInfoActivity.newIntent(getActivity(), directMessage.getSender_screen_name());
            startActivity(intent);
        }else if (view.getId() == R.id.iv_image) {
            int type = MessageDelegateProvider.Type.getType(directMessage);
            if (type == MessageDelegateProvider.TYPE_OTHER_IMAGE || type == MessageDelegateProvider.TYPE_SELT_IMAGE) {
                ArrayList<ImageInfo> images = new ArrayList<>(1);
                images.add(directMessage.getImageInfo());

                ArrayList<ImageInfo> hdPaths = new ArrayList<>(1);
                ImageInfo hdImageInfo = new ImageInfo(ChatPresentImp.getMessageImageHdUrl(directMessage), directMessage.getImageInfo().getWidth(),
                        directMessage.getImageInfo().getWidth(), directMessage.getImageInfo().getImageType());
                hdPaths.add(hdImageInfo);

                Intent intent = ImagePrewActivity.newIntent(getActivity(), images, hdPaths, 0);
                startActivity(intent);
            }
        }
    }
}
