package com.caij.emore.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.bean.User;
import com.caij.emore.utils.DateUtil;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/30.
 */
public class UserInfoFragment extends LazyFragment {

    @BindView(R.id.item_name)
    View itemName;
    @BindView(R.id.item_rename)
    View itemRename;
    @BindView(R.id.item_sex)
    View itemSex;
    @BindView(R.id.item_address)
    View itemAddress;
    @BindView(R.id.item_description)
    View itemDesc;
    @BindView(R.id.item_join_time)
    View itemJoinTime;

    public static UserInfoFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putSerializable(Key.OBJ, user);
        UserInfoFragment fragment = new UserInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onUserFirstVisible() {
        User user = (User) getArguments().getSerializable(Key.OBJ);
        setItemTitle(itemName, "昵称");
        setItemTitle(itemRename, "备注");
        setItemTitle(itemSex, "性别");
        setItemTitle(itemAddress, "所在地");
        setItemTitle(itemDesc, "简介");
        setItemTitle(itemJoinTime, "注册时间");

        setItemBody(itemName, user.getScreen_name());
        setItemBody(itemRename, user.getName());
        String sex;
        if ("m".equals(user.getGender())) {
            sex = "男";
        }else if ("f".equals(user.getGender())) {
            sex = "女";
        }else {
            sex = "未知";
        }
        setItemBody(itemSex, sex);
        setItemBody(itemAddress, user.getLocation());
        setItemBody(itemDesc, user.getDescription());
        setItemBody(itemJoinTime, DateUtil.formatDate(Date.parse(user.getCreated_at()), "yyyy.MM.dd"));
    }

    private void setItemTitle(View view, String text) {
        ((TextView) view.findViewById(R.id.txtTitle)).setText(text);
    }

    private void setItemBody(View view, String text) {
        ((TextView) view.findViewById(R.id.txtBody)).setText(text);
    }
}
