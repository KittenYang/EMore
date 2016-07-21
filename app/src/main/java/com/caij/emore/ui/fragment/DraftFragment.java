package com.caij.emore.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.caij.emore.R;
import com.caij.emore.database.bean.Draft;
import com.caij.emore.present.DraftPresent;
import com.caij.emore.present.imp.DraftPresentImp;
import com.caij.emore.source.local.LocalDraftSource;
import com.caij.emore.ui.activity.publish.PublishWeiboActivity;
import com.caij.emore.ui.adapter.DraftAdapter;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.BaseViewHolder;

/**
 * Created by Caij on 2016/7/20.
 */
public class DraftFragment extends RecyclerViewFragment<Draft, DraftPresent> {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("草稿箱");
    }

    @Override
    protected BaseAdapter<Draft, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        return new DraftAdapter(getActivity());
    }

    @Override
    protected DraftPresent createPresent() {
        return new DraftPresentImp(new LocalDraftSource(), this);
    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void onItemClick(View view, int position) {
        Draft draft = mRecyclerViewAdapter.getItem(position);
        if (view.getId() == R.id.btnResend) {
            mPresent.publishDraft(draft);
            mRecyclerViewAdapter.removeEntity(draft);
            mRecyclerViewAdapter.notifyItemRemoved(position);
        }else if (view.getId() == R.id.btnDel) {
            mPresent.deleteDraft(draft);
            mRecyclerViewAdapter.removeEntity(draft);
            mRecyclerViewAdapter.notifyItemRemoved(position);
        }else {
            switch (draft.getType()) {
                case Draft.TYPE_WEIBO:
                    Intent intent = PublishWeiboActivity.newIntent(getActivity(), draft);
                    startActivity(intent);
                    break;
            }
        }
    }
}
