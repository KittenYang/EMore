package com.caij.emore.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.caij.emore.Key;
import com.caij.emore.present.ListPresent;
import com.caij.emore.view.recyclerview.BaseAdapter;

/**
 * Created by Caij on 2016/6/14.
 */
public class WeiboLikerListFragment extends RecyclerViewFragment {


    public static WeiboLikerListFragment newInstance(long weiboId) {
        Bundle args = new Bundle();
        args.putLong(Key.ID, weiboId);
        WeiboLikerListFragment fragment = new WeiboLikerListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected BaseAdapter createRecyclerViewAdapter() {
        return null;
    }

    @Override
    protected ListPresent createPresent() {
        return null;
    }

    @Override
    protected void onUserFirstVisible() {
        
    }


    @Override
    public void onEmpty() {

    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
