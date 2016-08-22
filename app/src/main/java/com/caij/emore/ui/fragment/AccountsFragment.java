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
import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.AccountInfo;
import com.caij.emore.present.AccountPresent;
import com.caij.emore.present.imp.AccountPresentImp;
import com.caij.emore.ui.activity.MainActivity;
import com.caij.emore.ui.activity.login.EMoreLoginActivity;
import com.caij.emore.ui.activity.login.WeiCoLoginActivity;
import com.caij.emore.ui.adapter.AccountAdapter;
import com.caij.emore.ui.view.AccountView;
import com.caij.emore.utils.ActivityStack;
import com.caij.emore.utils.DensityUtil;
import com.caij.emore.utils.Init;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.BaseViewHolder;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

/**
 * Created by Caij on 2016/8/22.
 */
public class AccountsFragment extends RecyclerViewFragment<AccountInfo, AccountPresent> implements AccountView {

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
        return new AccountAdapter(getActivity());
    }

    @Override
    protected AccountPresent createPresent() {
        return new AccountPresentImp(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        Account account = mRecyclerViewAdapter.getItem(position).getAccount();
        if (view.getId() == R.id.iv_delete) {
            if (!account.equals(UserPrefs.get(getActivity()).getAccount())) {
                UserPrefs.get(getActivity()).deleteAccount(account);
                mRecyclerViewAdapter.removeEntity(mRecyclerViewAdapter.getItem(position));
                mRecyclerViewAdapter.notifyItemRemoved(position);
            }else {
                UserPrefs.get(getActivity()).deleteAccount(account);
                mRecyclerViewAdapter.removeEntity(mRecyclerViewAdapter.getItem(position));

                if (mRecyclerViewAdapter.getEntities().size() > 0) {
                    Account otherAccount = mRecyclerViewAdapter.getEntities().get(0).getAccount();
                    changeAccount(otherAccount);
                }else {
                    Init.getInstance().stop(getActivity());

                    ActivityStack.getInstance().remove(getActivity());
                    ActivityStack.getInstance().finishAllActivity();
                    Intent toIntent = EMoreLoginActivity.newEMoreLoginIntent(getActivity(),
                            null, null);
                    startActivity(toIntent);
                    getActivity().finish();
                }
            }
        }else {
            if (account.getEmoreToken() != null && !account.getEmoreToken().isExpired()
                    && account.getWeiCoToken() != null && !account.getWeiCoToken().isExpired()) {
                if (!account.equals(UserPrefs.get(getActivity()).getAccount())) {
                    changeAccount(account);
                }else {
                    showHint(R.string.is_current_account);
                }
            }else {
                Token eMoreAccessToken = UserPrefs.get(getActivity()).getEMoreToken();
                Token weicoAccessToken = UserPrefs.get(getActivity()).getWeiCoToken();
                Intent toIntent = null;
                if (eMoreAccessToken.isExpired()) {
                    toIntent = EMoreLoginActivity.newEMoreLoginIntent(getActivity(),
                            UserPrefs.get(getActivity()).getAccount().getUsername(),
                            UserPrefs.get(getActivity()).getAccount().getPwd());
                }else if (weicoAccessToken == null || weicoAccessToken.isExpired()){
                    toIntent = WeiCoLoginActivity.newWeiCoLoginIntent(getActivity(),
                            UserPrefs.get(getActivity()).getAccount().getUsername(),
                            UserPrefs.get(getActivity()).getAccount().getPwd());
                }
                startActivity(toIntent);
            }
        }
    }

    private void changeAccount(Account account) {
        UserPrefs.get(getActivity()).changeAccount(account);
        Init.getInstance().reset(getActivity(), account.getUid());

        Intent intent = new Intent(getActivity(), MainActivity.class);
        ActivityStack.getInstance().remove(getActivity());
        ActivityStack.getInstance().finishAllActivity();
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_account, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 新增授权
        if (item.getItemId() == R.id.add) {
            ActivityStack.getInstance().remove(getActivity());
            ActivityStack.getInstance().finishAllActivity();
            Intent intent = EMoreLoginActivity.newEMoreLoginIntent(getActivity(), null, null);
            startActivity(intent);
            getActivity().finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
