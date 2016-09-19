package com.caij.emore.ui.activity;

import android.os.Bundle;

import com.caij.emore.R;
import com.caij.emore.present.BasePresent;
import com.caij.emore.ui.fragment.setting.AdvancedSettingFragment;

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
        getApplication().setTheme(R.style.AppTheme_Pink);
        getFragmentManager().beginTransaction().
                replace(R.id.attach_container, new AdvancedSettingFragment()).commit();
    }

    @Override
    protected BasePresent createPresent() {
        return null;
    }
}
