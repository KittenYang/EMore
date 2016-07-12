package com.caij.emore.utils.weibo;

import com.caij.emore.Key;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.Account;

import java.util.Map;

/**
 * Created by Caij on 2016/7/10.
 */
public class ApiUtil {

    public static void appendAuthSina(Map<String, Object> paramMap)
    {
        Account localAccount = UserPrefs.get().getAccount();
        paramMap.put("c", "weicoandroid");
        paramMap.put("s", localAccount.getWeiCoLoginResponse().getsValue());
        paramMap.put("gsid", localAccount.getWeiCoLoginResponse().getGsid());
        paramMap.put("from", Key.WEICO_APP_FROM);
        paramMap.put("source", Key.WEICO_APP_ID);
    }

    public static void appendAuth(Map<String, Object> paramMap)
    {
        paramMap.put("from", Key.WEICO_APP_FROM);
        paramMap.put("source", Key.WEICO_APP_ID);
    }
}
