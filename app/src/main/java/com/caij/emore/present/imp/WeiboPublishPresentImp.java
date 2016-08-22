package com.caij.emore.present.imp;


import android.os.AsyncTask;

import com.caij.emore.Event;
import com.caij.emore.account.Account;
import com.caij.emore.bean.PublishBean;
import com.caij.emore.database.bean.Draft;
import com.caij.emore.present.WeiboPublishPresent;
import com.caij.emore.ui.view.WeiboPublishView;
import com.caij.emore.source.DraftSource;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.rxbus.RxBus;

import java.util.ArrayList;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/24.
 */
public class WeiboPublishPresentImp extends AbsBasePresent implements WeiboPublishPresent {

    private WeiboPublishView mWeiboPublishView;
    private Account mAccount;
    DraftSource mDraftSource;

    public WeiboPublishPresentImp(Account account, WeiboPublishView weiboPublishView, DraftSource draftSource) {
        mAccount = account;
        mWeiboPublishView = weiboPublishView;
        mDraftSource = draftSource;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void publishWeibo(long id, String content, ArrayList<String> imagePaths) {
        if (imagePaths != null && imagePaths.size() > 1) {
            if (mAccount.getWeiCoToken() == null || mAccount.getWeiCoToken().isExpired()) {
                mWeiboPublishView.toAuthWeico();
                return;
            }
        }

        PublishBean publishBean = new PublishBean();
        publishBean.setId(id);
        publishBean.setText(content);
        publishBean.setPics(imagePaths);
        RxBus.getDefault().post(Event.PUBLISH_WEIBO, publishBean);

        saveToDraft(id, content, imagePaths);

        mWeiboPublishView.finish();
    }

    @Override
    public void saveToDraft(final long id, final String content, final ArrayList<String> images) {
        ExecutorServiceUtil.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                Draft draft = new Draft();
                draft.setImages(images);
                draft.setCreate_at(System.currentTimeMillis());
                draft.setId(id);
                draft.setContent(content);
                draft.setType(Draft.TYPE_WEIBO);
                draft.setStatus(Draft.STATUS_SAVE);
                draft.setImage_paths(GsonUtils.toJson(images));
                mDraftSource.saveDraft(draft);
                RxBus.getDefault().post(Event.EVENT_DRAFT_UPDATE, draft);
                return null;
            }
        });
    }

}
