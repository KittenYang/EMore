package com.caij.emore.present.imp;


import android.os.AsyncTask;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.bean.Account;
import com.caij.emore.bean.PublishBean;
import com.caij.emore.database.bean.Draft;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboPublishPresent;
import com.caij.emore.present.view.WeiboPublishView;
import com.caij.emore.source.DraftSource;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.EventUtil;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.rxbus.RxBus;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/24.
 */
public class WeiboPublishPresentImp implements WeiboPublishPresent {

    private final CompositeSubscription mLoginCompositeSubscription;

    private WeiboPublishView mWeiboPublishView;
    private Account mAccount;
    DraftSource mDraftSource;

    public WeiboPublishPresentImp(Account account, WeiboPublishView weiboPublishView, DraftSource draftSource) {
        mAccount = account;
        mWeiboPublishView = weiboPublishView;
        mDraftSource = draftSource;
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }

    @Override
    public void publishWeibo(long id, String content, ArrayList<String> imagePaths) {
        if (imagePaths != null && imagePaths.size() > 1) {
            if (mAccount.getWeicoToken() == null || mAccount.getWeicoToken().isExpired()) {
                mWeiboPublishView.toAuthWeico();
                return;
            }
        }

        PublishBean publishBean = new PublishBean();
        publishBean.setId(id);
        publishBean.setText(content);
        publishBean.setPics(imagePaths);
        EventUtil.publishWeibo(publishBean);
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
                RxBus.get().post(Key.EVENT_DRAFT_UPDATE, draft);
                return null;
            }
        });
    }

}
