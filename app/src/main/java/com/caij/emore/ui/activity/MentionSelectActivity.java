package com.caij.emore.ui.activity;

import android.os.Bundle;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.ui.fragment.MentionSelectFragment;

/**
 * Created by Caij on 2016/8/1.
 */
public class MentionSelectActivity extends BaseToolBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MentionSelectFragment fragment = new MentionSelectFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(Key.ID, Long.parseLong(UserPrefs.get().getEMoreToken().getUid()));
        fragment.setArguments(bundle);
        fragment.setUserVisibleHint(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.attach_container, fragment).commit();
    }

    @Override
    public int getAttachLayoutId() {
        return 0;
    }
}
