package com.caij.emore.utils;

import android.view.View;
import android.widget.ImageView;

import com.caij.emore.R;
import com.caij.emore.database.bean.User;

/**
 * Created by Caij on 2016/7/22.
 */
public class WeiboUtil {

    public static void setImageVerified(ImageView imgVerified, User user) {
//        -1普通用户;
//        0名人,
//                1政府,
//                2企业,
//                3媒体,
//                4校园,
//                5网站,
//                6应用,
//                7团体（机构）,
//        8待审企业,
//                200初级达人,
//                220中高级达人,
//                400已故V用户。
        if (user == null || user.getVerified_type() == null) {
            imgVerified.setVisibility(View.GONE);
            return;
        }

        // 黄V
        if (user.getVerified_type() == 0) {
            imgVerified.setImageResource(R.mipmap.avatar_vip);
        }
        // 200:初级达人 220:高级达人
        else if (user.getVerified_type() == 200 || user.getVerified_type() == 220) {
            imgVerified.setImageResource(R.mipmap.avatar_grassroot);
        }
        // 蓝V
        else if (user.getVerified_type() > 0) {
            imgVerified.setImageResource(R.mipmap.avatar_enterprise_vip);
        }

//
        if (user.getVerified_type() >= 0)
            imgVerified.setVisibility(View.VISIBLE);
        else
            imgVerified.setVisibility(View.GONE);
    }

    public static void setGender(User user, ImageView imageView) {
        if ("m".equals(user.getGender())) {
            imageView.setImageResource(R.mipmap.userinfo_icon_male);
        }else if ("f".equals(user.getGender())) {
            imageView.setImageResource(R.mipmap.userinfo_icon_female);
        }else {
            imageView.setImageResource(R.mipmap.userinfo_icon_male);
        }
    }
}
