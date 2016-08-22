package com.caij.emore.ui.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.present.BasePresent;
import com.caij.emore.ui.activity.UserInfoActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/7/29.
 */
public class AppAboutFragment extends BaseFragment {

    @BindView(R.id.tvVersion)
    TextView tvVersion;
    @BindView(R.id.tvHelp)
    TextView tvHelp;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_app_about, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PackageManager manager = getActivity().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            tvVersion.setText(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected BasePresent createPresent() {
        return null;
    }

    @OnClick(R.id.tvHelp)
    public void onClick() {
        Intent intent = UserInfoActivity.newIntent(getActivity(), "Ca1j");
        startActivity(intent);
    }
}
