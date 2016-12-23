package com.caij.emore.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.caij.emore.R;
import com.caij.emore.account.Account;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.AccountInfo;
import com.caij.emore.present.AccountPresent;
import com.caij.emore.present.imp.AccountPresentImp;
import com.caij.emore.ui.activity.MainActivity;
import com.caij.emore.ui.activity.login.WeiCoLoginActivity;
import com.caij.emore.ui.adapter.delegate.AccountDelegate;
import com.caij.emore.ui.view.AccountView;
import com.caij.emore.utils.DensityUtil;
import com.caij.emore.Init;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;
import com.caij.rvadapter.adapter.BaseAdapter;
import com.caij.rvadapter.adapter.MultiItemTypeAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

/**
 * Created by Caij on 2016/8/22.
 */
public class AccountsFragment extends RecyclerViewFragment<AccountInfo, AccountPresent> implements AccountView, OnItemPartViewClickListener {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        xRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(getResources().getColor(R.color.divider_timeline_item)).
                size(DensityUtil.dip2px(getActivity(), 1f)).
                build());
    }

    @Override
    protected BaseAdapter<AccountInfo, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        MultiItemTypeAdapter<AccountInfo> multiItemTypeAdapter = new MultiItemTypeAdapter<AccountInfo>(getActivity());
        multiItemTypeAdapter.addItemViewDelegate(new AccountDelegate(this));
        return multiItemTypeAdapter;
    }

    @Override
    protected AccountPresent createPresent() {
        return new AccountPresentImp(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        Account account = mRecyclerViewAdapter.getItem(position).getAccount();
        changeAccount(account);
    }

    private void deleteAccount(Account account, int position) {
        if (!account.equals(UserPrefs.get(getActivity()).getAccount())) {
            UserPrefs.get(getActivity()).deleteAccount(account);
            mRecyclerViewAdapter.removeEntity(mRecyclerViewAdapter.getItem(position));
            mRecyclerViewAdapter.notifyItemRemoved(position);
        }else {
            UserPrefs.get(getActivity()).deleteAccount(account);
            mRecyclerViewAdapter.removeEntity(mRecyclerViewAdapter.getItem(position));

            if (mRecyclerViewAdapter.getEntities().size() > 0) {
                Account otherAccount = mRecyclerViewAdapter.getEntities().get(0).getAccount();
                toMain(otherAccount);
            }else {
                Init.getInstance().stop(getActivity());

                Intent toIntent = WeiCoLoginActivity.newWeiCoLoginIntent(getActivity(),
                        null, null);
                toIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toIntent);
            }
        }
    }

    private void changeAccount(Account account) {
        if (account.getToken() != null && !account.getToken().isExpired()) {
            if (!account.equals(UserPrefs.get(getActivity()).getAccount())) {
                toMain(account);
            }else {
                showHint(R.string.is_current_account);
            }
        }else {
            UserPrefs.get(getActivity()).changeAccount(account);
            Init.getInstance().stop(getActivity());

            Intent toIntent = WeiCoLoginActivity.newWeiCoLoginIntent(getActivity(),
                        account.getUsername(), account.getPwd());
            toIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(toIntent);
        }
    }

    private void toMain(Account account) {
        UserPrefs.get(getActivity()).changeAccount(account);
        Init.getInstance().reset(getActivity(), account.getUid());

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_account, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 新增授权
        if (item.getItemId() == R.id.add) {
            Intent intent = WeiCoLoginActivity.newWeiCoLoginIntent(getActivity(), null, null);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view, int position) {
        Account account = mRecyclerViewAdapter.getItem(position).getAccount();
        if (view.getId() == R.id.iv_delete) {
            deleteAccount(account, position);
        }
    }
}
