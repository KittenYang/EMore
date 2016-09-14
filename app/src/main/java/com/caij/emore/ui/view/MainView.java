package com.caij.emore.ui.view;


import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.bean.User;

/**
 * Created by Caij on 2016/6/3.
 */
public interface MainView extends BaseView{

    public void setUser(User user);

    void setUnReadMessage(UnReadMessage unReadMessage);

    void setDraftCount(Integer integer);

    public void setNightMode(boolean isNight);
}
