package com.caij.emore.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.caij.emore.R;
import com.caij.emore.widget.DoubleClickToolBar;

/**
 * Created by Caij on 2016/5/28.
 */
public abstract class BaseToolBarActivity extends BaseActivity {

    protected DoubleClickToolBar mToolbar;
    protected FrameLayout mAttachContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayoutId());
        mToolbar = (DoubleClickToolBar) findViewById(R.id.toolbar);
        mAttachContainer = (FrameLayout) findViewById(R.id.attach_container);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getAttachLayoutId() > 0) {
            getLayoutInflater().inflate(getAttachLayoutId(), mAttachContainer, true);
        }
    }

    protected int getContentLayoutId() {
        return R.layout.activity_base_toolbar;
    }

    public abstract int getAttachLayoutId();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
