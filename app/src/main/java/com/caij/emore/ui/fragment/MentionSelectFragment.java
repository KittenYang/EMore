package com.caij.emore.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.caij.emore.Key;
import com.caij.emore.ui.fragment.friendship.FriendFragment;

/**
 * Created by Caij on 2016/8/1.
 */
public class MentionSelectFragment extends FriendFragment {
    @Override
    public void onItemClick(View view, int position) {
//        super.onItemClick(view, position);
        Intent intent = new Intent();
        intent.putExtra(Key.USERNAME, mRecyclerViewAdapter.getItem(position).getScreen_name());
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}
