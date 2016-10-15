package com.caij.emore.present.imp;


import android.os.AsyncTask;

import com.caij.emore.EventTag;
import com.caij.emore.account.Account;
import com.caij.emore.bean.PublishBean;
import com.caij.emore.manager.DraftManager;
import com.caij.emore.database.bean.Draft;
import com.caij.emore.present.WeiboPublishPresent;
import com.caij.emore.ui.view.StatusPublishView;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.rxbus.RxBus;

import java.util.ArrayList;


/**
 * Created by Caij on 2016/6/24.
 */
public class StatusPublishPresentImp extends AbsBasePresent implements WeiboPublishPresent {

    private StatusPublishView mStatusPublishView;
    private Account mAccount;
    private DraftManager mDraftManager;

    public StatusPublishPresentImp(Account account, StatusPublishView statusPublishView, DraftManager draftManager) {
        mAccount = account;
        mStatusPublishView = statusPublishView;
        mDraftManager = draftManager;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void publishStatus(long id, String content, ArrayList<String> imagePaths) {
        if (imagePaths != null && imagePaths.size() > 1) {
            if (mAccount.getToken() == null || mAccount.getToken().isExpired()) {
                mStatusPublishView.toAuthWeico();
                return;
            }
        }

        PublishBean publishBean = new PublishBean();
        publishBean.setId(id);
        publishBean.setText(content);
        publishBean.setPics(imagePaths);
        RxBus.getDefault().post(EventTag.PUBLISH_WEIBO, publishBean);

        saveToDraft(id, content, imagePaths);

        mStatusPublishView.finish();
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
                mDraftManager.insertDraft(draft);
                RxBus.getDefault().post(EventTag.EVENT_DRAFT_UPDATE, draft);
                return null;
            }
        });
    }

}
