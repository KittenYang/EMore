package com.caij.emore.ui.fragment.mention;

import com.caij.emore.account.Account;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.manager.imp.NotifyManagerImp;
import com.caij.emore.present.RefreshListPresent;
import com.caij.emore.present.imp.CommentMentionPresentImp;
import com.caij.emore.remote.imp.CommentApiImp;
import com.caij.emore.remote.imp.NotifyApiImp;

/**
 * Created by Caij on 2016/7/4.
 */
public class CommentMentionFragment extends AcceptCommentsFragment  {

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    protected RefreshListPresent createPresent() {
        Account account = UserPrefs.get(getActivity()).getAccount();
        return new CommentMentionPresentImp(account.getUid(), new CommentApiImp(),
                new NotifyApiImp(), new NotifyManagerImp(), this);
    }

}
