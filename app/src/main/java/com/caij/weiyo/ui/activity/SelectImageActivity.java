package com.caij.weiyo.ui.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import com.caij.weiyo.R;
import com.caij.weiyo.bean.ImageFolder;
import com.caij.weiyo.bean.Image;
import com.caij.weiyo.ui.fragment.SelectImageFragment;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Caij on 2016/6/22.
 */
public class SelectImageActivity extends BaseToolBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.attach_container, new SelectImageFragment()).commit();
    }

    @Override
    public int getAttachLayoutId() {
        return 0;
    }


}
