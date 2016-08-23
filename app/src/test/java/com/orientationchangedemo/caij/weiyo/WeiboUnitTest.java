package com.orientationchangedemo.caij.weiyo;

import com.caij.emore.api.WeiBoService;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.bean.response.UserWeiboResponse;
import com.caij.emore.database.bean.User;
import com.caij.emore.source.UserSource;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.source.server.ServerUserSource;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.utils.GsonUtils;

import org.junit.Test;

import rx.Subscriber;

/**
 * Created by Caij on 2016/5/30.
 */
public class WeiboUnitTest {

    @Test
    public void loadUserInfo() throws Exception {
        UserSource userSource = new ServerUserSource();
        userSource.getWeiboUserInfoByName(Key.token, "Ca1j").subscribe(new Subscriber<User>() {
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
        WeiboSource weiboSource = new ServerWeiboSource();
        weiboSource.getFriendWeibo(Key.token, 0, 0, 0, 20, 1).subscribe(new Subscriber<QueryWeiboResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(QueryWeiboResponse response) {

            }
        });
    }

    @Test
    public void loadUserWeibo() throws Exception {
        WeiBoService weiBoService = WeiBoService.Factory.create();
        weiBoService.getUserWeibos(Key.token, "GitCafe", 0, 0, 0, 20, 1)
                .subscribe(new Subscriber<UserWeiboResponse>() {
                    @Override
                    public void onCompleted() {
                        System.out.print("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.print(e.getMessage());
                    }

                    @Override
                    public void onNext(UserWeiboResponse userWeiboResponse) {
                        System.out.print("onCompleted");
                    }
                });
    }
}
