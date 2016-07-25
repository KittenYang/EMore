package com.caij.emore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.caij.emore.Key;
import com.caij.emore.R;

import java.util.List;

/**
 * Created by Caij on 2016/7/7.
 */
public class DefaultFragmentActivity extends BaseToolBarActivity {

    private int mType;

    public static Intent starFragmentV4(Context context,
                                        String title,
                                        Class<? extends android.support.v4.app.Fragment> fragmentClazz,
                                        Bundle bundle) {
        Intent intent = new Intent(context, DefaultFragmentActivity.class);
        intent.putExtra(Key.DATE, bundle);
        intent.putExtra(Key.OBJ, fragmentClazz);
        intent.putExtra(Key.TYPE, 1);
        intent.putExtra(Key.TITLE, title);
        return intent;
    }

    public static Intent starFragment(Context context,
                                      String title,
                                      Class<? extends android.app.Fragment> fragmentClazz,
                                      Bundle bundle) {
        Intent intent = new Intent(context, DefaultFragmentActivity.class);
        intent.putExtra(Key.DATE, bundle);
        intent.putExtra(Key.OBJ, fragmentClazz);
        intent.putExtra(Key.TYPE, 2);
        intent.putExtra(Key.TITLE, title);
        return intent;
    }

    @Override
    public int getAttachLayoutId() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        mType = intent.getIntExtra(Key.TYPE, -1);

        setTitle(intent.getStringExtra(Key.TITLE));

        if (mType == 1) {
            attachV4Fragment(intent);
        }else if (mType == 2) {
            attachFragment(intent);
        }

    }

    private void attachFragment(Intent intent) {
        Bundle bundle = intent.getBundleExtra(Key.DATE);
        Class<android.app.Fragment> fragmentClazz = (Class<android.app.Fragment>) getIntent().getSerializableExtra(Key.OBJ);
        android.app.FragmentTransaction fragmentTransaction =  getFragmentManager().beginTransaction();
        try {
            android.app.Fragment fragment = fragmentClazz.newInstance();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.attach_container, fragment).commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void attachV4Fragment(Intent intent) {
        Bundle bundle = intent.getBundleExtra(Key.DATE);
        Class<android.support.v4.app.Fragment> fragmentClazz = (Class<android.support.v4.app.Fragment>) getIntent().getSerializableExtra(Key.OBJ);
        FragmentTransaction fragmentTransaction =  getSupportFragmentManager().beginTransaction();
        try {
            android.support.v4.app.Fragment fragment = fragmentClazz.newInstance();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.attach_container, fragment).commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (mType == 1) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                if (fragment instanceof OnBackPressedListener) {
                    if (((OnBackPressedListener) fragment).onBackPressed()) {
                        return;
                    }
                }
            }
        }
        super.onBackPressed();
    }

    public static interface OnBackPressedListener {
        boolean onBackPressed();
    }
}
