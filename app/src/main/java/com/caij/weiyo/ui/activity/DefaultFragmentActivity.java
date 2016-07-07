package com.caij.weiyo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;

/**
 * Created by Caij on 2016/7/7.
 */
public class DefaultFragmentActivity extends BaseToolBarActivity {

    public static Intent starFragmentV4(Context context, Class<? extends android.support.v4.app.Fragment> fragmentClazz, Bundle bundle) {
        Intent intent = new Intent(context, DefaultFragmentActivity.class);
        intent.putExtra(Key.DATE, bundle);
        intent.putExtra(Key.OBJ, fragmentClazz);
        intent.putExtra(Key.TYPE, 1);
        return intent;
    }

    public static Intent starFragment(Context context, Class<? extends android.app.Fragment> fragmentClazz, Bundle bundle) {
        Intent intent = new Intent(context, DefaultFragmentActivity.class);
        intent.putExtra(Key.DATE, bundle);
        intent.putExtra(Key.OBJ, fragmentClazz);
        intent.putExtra(Key.TYPE, 2);
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

        int type = intent.getIntExtra(Key.TYPE, -1);

        if (type == 1) {
            attachV4Fragment(intent);
        }else if (type == 2) {
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
}
