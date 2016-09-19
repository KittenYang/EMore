package com.caij.emore.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.account.Account;
import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.present.BasePresent;
import com.caij.emore.ui.activity.login.WeiCoLoginActivity;
import com.caij.emore.ui.adapter.WeiboFragmentPagerAdapter;
import com.caij.emore.ui.fragment.BaseFragment;
import com.caij.emore.ui.fragment.mention.CommentMentionFragment;
import com.caij.emore.ui.fragment.mention.WeiboMentionFragment;
import com.caij.emore.utils.DialogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/7/3.
 */
public class MentionActivity extends BaseToolBarActivity {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setTitle(R.string.mention);
        Token weicoToken = UserPrefs.get(this).getWeiCoToken();
        if (weicoToken == null || weicoToken.isExpired()) {
            DialogUtil.showHintDialog(this, getString(R.string.hint), getString(R.string.aouth_high_hint), getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Account account = UserPrefs.get(getApplicationContext()).getAccount();
                    Intent intent = WeiCoLoginActivity.newWeiCoLoginIntent(MentionActivity.this,
                            account.getUsername(), account.getPwd());
                    startActivityForResult(intent, Key.AUTH);
                }
            }, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        } else {
            doNext();
        }
    }

    @Override
    protected BasePresent createPresent() {
        return null;
    }

    private void doNext() {
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new WeiboMentionFragment());
        fragments.add(new CommentMentionFragment());

        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.weibo));
        titles.add(getString(R.string.comment));
        FriendshipPageAdapter friendshipPageAdapter = new FriendshipPageAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(friendshipPageAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public int getAttachLayoutId() {
        return 0;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_friendship;
    }

    private class FriendshipPageAdapter extends WeiboFragmentPagerAdapter {

        public FriendshipPageAdapter(FragmentManager fm, List<BaseFragment> fragments, List<String> titles) {
            super(fm, fragments, titles);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Key.AUTH && resultCode == Activity.RESULT_OK) {
            doNext();
        }
    }
}
