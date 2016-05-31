package com.caij.weiyo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.source.WeiboSource;
import com.caij.weiyo.source.imp.WeiboSourceImp;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WeiboSource weiboSource = new WeiboSourceImp();
        weiboSource.getFriendWeibo(UserPrefs.get().getToken().getAccess_token(), 0, 0, 20, 1, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Weibo>>() {
                    @Override
                    public void onCompleted() {
                        System.out.print("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.print("onError");
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        System.out.print("onNext");
                    }
                });
    }
}
