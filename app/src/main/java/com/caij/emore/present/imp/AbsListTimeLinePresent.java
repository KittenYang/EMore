package com.caij.emore.present.imp;


import com.caij.emore.bean.Account;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.view.BaseListView;
import com.caij.emore.present.view.WeiboActionView;
import com.caij.emore.source.WeiboSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Caij on 2016/8/12.
 */
public abstract class AbsListTimeLinePresent<V extends WeiboActionView & BaseListView<Weibo>> extends AbsTimeLinePresent<V> {

    protected List<Weibo> mWeibos;

    public AbsListTimeLinePresent(Account account, V view, WeiboSource serverWeiboSource, WeiboSource localWeiboSource) {
        super(account, view, serverWeiboSource, localWeiboSource);
        mWeibos = new ArrayList<>();
    }

    @Override
    protected void onWeiboUpdate(Weibo weibo) {
        for (int index = 0; index < mWeibos.size(); index++) {
            if (mWeibos.get(index).equals(weibo)) {
                mWeibos.remove(weibo);
                mWeibos.add(index, weibo);
                mView.setEntities(mWeibos);
                break;
            }
        }
    }
}
