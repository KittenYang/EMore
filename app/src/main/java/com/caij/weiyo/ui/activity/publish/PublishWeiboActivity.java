package com.caij.weiyo.ui.activity.publish;

import android.os.Bundle;
import android.widget.EditText;

import com.caij.weiyo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/22.
 */
public class PublishWeiboActivity extends PublishActivity {


    @BindView(R.id.et_content)
    EditText etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    public int getWeiboContentLayoutId() {
        return R.layout.include_publish_weibo;
    }

    @Override
    protected void onSendClick() {

    }
}
