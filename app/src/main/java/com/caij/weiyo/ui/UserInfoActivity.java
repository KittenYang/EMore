package com.caij.weiyo.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.caij.weiyo.R;
import com.caij.weiyo.present.view.BaseView;

/**
 * Created by Caij on 2016/6/8.
 */
public class UserInfoActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_pager);
//        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
//        getSupportActionBar().setTitle("Caij");
    }
}
