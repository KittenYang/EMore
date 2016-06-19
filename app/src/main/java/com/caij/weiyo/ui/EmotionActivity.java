package com.caij.weiyo.ui;

import android.os.Bundle;

import com.caij.weiyo.R;
import com.caij.weiyo.ui.fragment.EmotionFragment;

/**
 * Created by Caij on 2016/6/18.
 */
public class EmotionActivity extends BaseToolBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().
                add(R.id.attach_container, new EmotionFragment()).commit();
    }

    @Override
    public int getAttachLayoutId() {
        return 0;
    }
}
