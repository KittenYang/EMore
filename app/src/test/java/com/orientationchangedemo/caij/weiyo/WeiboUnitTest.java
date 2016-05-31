package com.orientationchangedemo.caij.weiyo;

import com.caij.weiyo.bean.User;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.source.UserSource;
import com.caij.weiyo.source.WeiboSource;
import com.caij.weiyo.source.imp.UserSourceImp;
import com.caij.weiyo.source.imp.WeiboSourceImp;
import com.caij.weiyo.utils.GsonUtils;

import org.junit.Test;

import java.util.List;

import rx.Subscriber;

/**
 * Created by Caij on 2016/5/30.
 */
public class WeiboUnitTest {

    @Test
    public void loadUserInfo() throws Exception {
        UserSource userSource = new UserSourceImp();
        userSource.getWeiUserInfo(Key.token, "Ca1j").subscribe(new Subscriber<User>() {
            @Override
            public void onCompleted() {
                System.out.print("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                System.out.print("onError");
            }

            @Override
            public void onNext(User weiBoUser) {
                System.out.print(GsonUtils.toJson(weiBoUser));
            }
        });
    }

    @Test
    public void loadFriendWeibo() throws Exception {
        WeiboSource weiboSource = new WeiboSourceImp();
        weiboSource.getFriendWeibo(Key.token, 0, 0, 20, 1, 0).subscribe(new Subscriber<List<Weibo>>() {
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
