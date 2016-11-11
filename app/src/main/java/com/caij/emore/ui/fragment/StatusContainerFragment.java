package com.caij.emore.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.database.bean.Group;
import com.caij.emore.manager.imp.GroupManagerImp;
import com.caij.emore.present.StatusContainerPresent;
import com.caij.emore.present.imp.StatusContainerPresentImp;
import com.caij.emore.remote.imp.GroupApiImp;
import com.caij.emore.repository.GroupRepository;
import com.caij.emore.ui.activity.SearchRecommendActivity;
import com.caij.emore.ui.activity.publish.PublishStatusActivity;
import com.caij.emore.ui.brige.ToolbarDoubleClick;
import com.caij.emore.ui.fragment.weibo.FriendStatusFragment;
import com.caij.emore.ui.view.StatusContainerView;
import com.caij.emore.utils.LogUtil;

import java.util.List;

/**
 * Created by Caij on 2016/11/2.
 */

public class StatusContainerFragment extends BaseFragment<StatusContainerPresent> implements StatusContainerView, ToolbarDoubleClick {

    public static final String FRIEND_STATUS_FRAGMENT_TAG = "friend_status_fragment_tag";

    private AlertDialog mGroupDialog;
    private int mCheckPosition;

    @Override
    protected StatusContainerPresent createPresent() {
        return new StatusContainerPresentImp(this, new GroupRepository(new GroupApiImp(), new GroupManagerImp()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_container, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, new FriendStatusFragment(), FRIEND_STATUS_FRAGMENT_TAG)
                    .commit();
        }else {
            mCheckPosition = savedInstanceState.getInt(Key.POSITION);
            LogUtil.d(this, "savedInstanceState != null mCheckPosition : " + mCheckPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Key.POSITION, mCheckPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onGetGroups(final List<Group> groups) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] items = new String[groups.size() + 1];
        items[0] = getString(R.string.home_page);
        for (int i = 0; i <  groups.size(); i ++) {
            items[i + 1] = groups.get(i).getName();
        }
        mGroupDialog =  builder.setTitle(R.string.group).setNegativeButton(getText(R.string.cancel), null)
                .setSingleChoiceItems(items, mCheckPosition, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onGroupClick(which, groups);
                    }
                }).create();
    }

    private void onGroupClick(int which, List<Group> groups) {
        if (which == mCheckPosition) return;

        mGroupDialog.dismiss();
        mCheckPosition = which;

        if (which == 0) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, new FriendStatusFragment(), FRIEND_STATUS_FRAGMENT_TAG)
                    .commit();
        }else {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, GroupStatusFragment.newInstance(groups.get(which - 1).getId()), FRIEND_STATUS_FRAGMENT_TAG)
                    .commit();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.publish:
                Intent intent = new Intent(getActivity(), PublishStatusActivity.class);
                startActivity(intent);
                break;

            case R.id.search:
                Intent intent1 = new Intent(getActivity(), SearchRecommendActivity.class);
                startActivity(intent1);
                getActivity().overridePendingTransition(-1, -1);
                break;

            case R.id.filter:
                if (mGroupDialog != null) {
                    mGroupDialog.show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void acceptToolBarDoubleClick(View view) {
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof ToolbarDoubleClick) {
                ((ToolbarDoubleClick) fragment).acceptToolBarDoubleClick(view);
            }
        }
    }
}
