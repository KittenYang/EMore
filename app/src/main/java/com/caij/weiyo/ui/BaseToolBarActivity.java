package com.caij.weiyo.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.caij.weiyo.R;

/**
 * Created by Caij on 2016/5/28.
 */
public abstract class BaseToolBarActivity extends BaseActivity {

    protected Toolbar mToolbar;
    protected FrameLayout mAttachContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_toolbar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mAttachContainer = (FrameLayout) findViewById(R.id.attach_container);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getAttachLayoutId() > 0) {
            getLayoutInflater().inflate(getAttachLayoutId(), mAttachContainer, true);
        }
    }

    public abstract int getAttachLayoutId();
}
