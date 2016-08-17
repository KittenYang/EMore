package com.caij.emore.present.imp;


import com.caij.emore.bean.Account;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.ui.view.ListView;
import com.caij.emore.ui.view.WeiboActionView;
import com.caij.emore.source.WeiboSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Caij on 2016/8/12.
 */
public abstract class AbsListTimeLinePresent<V extends WeiboActionView & ListView<Weibo>> extends AbsTimeLinePresent<V> {

    protected List<Weibo> mWeibos;

    public AbsListTimeLinePresent(Account account, V view, WeiboSource serverWeiboSource, WeiboSource localWeiboSource) {
        super(account, view, serverWeiboSource, localWeiboSource);
        mWeibos = new ArrayList<>();
    }

    @Override
    protected void onWeiboUpdate(Weibo weibo) {
        for (int index = 0; index < mWeibos.size(); index++) {
            Weibo weiboOnList = mWeibos.get(index);
            if (weiboOnList.equals(weibo)) {
                weiboOnList.setAttitudes_count(weibo.getAttitudes_count());
                weiboOnList.setReposts_count(weibo.getReposts_count());
                weiboOnList.setComments_count(weibo.getComments_count());
                weiboOnList.setAttitudes(weibo.isAttitudes());
                mView.notifyItemChanged(mWeibos, index);
                break;
            }
        }
    }
}
