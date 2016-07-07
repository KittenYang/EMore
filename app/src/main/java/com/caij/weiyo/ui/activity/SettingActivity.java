package com.caij.weiyo.ui.activity;

import android.os.Bundle;

import com.caij.weiyo.R;
import com.caij.weiyo.ui.fragment.setting.AdvancedSettingFragment;

/**
 * Created by Caij on 2016/7/6.
 */
public class SettingActivity extends BaseToolBarActivity {
    @Override
    public int getAttachLayoutId() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().
                replace(R.id.attach_container, new AdvancedSettingFragment()).commit();
    }
}
