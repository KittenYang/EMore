package com.caij.weiyo.ui.fragment;

import android.os.Bundle;

import com.caij.weiyo.Key;
import com.caij.weiyo.bean.Weibo;

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
    protected void onUserFirstVisible() {
        
    }


}
