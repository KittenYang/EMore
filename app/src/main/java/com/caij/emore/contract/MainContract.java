package com.caij.emore.contract;

import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.bean.User;

/**
 * Created by Caij on 2016/10/5.
 */

public interface MainContract {

    interface Present {
        public void getWeiboUserById(long uid);
    }

    interface View extends BaseContract.BaseView{

        public void setUser(User user);

        public void setUnReadMessage(UnReadMessage unReadMessage);

        public void setDraftCount(Integer integer);

        public void updateTheme();
    }
}
