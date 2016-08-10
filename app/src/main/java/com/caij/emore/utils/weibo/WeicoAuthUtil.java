package com.caij.emore.utils.weibo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.Account;
import com.caij.emore.ui.activity.login.WeiCoLoginActivity;
import com.caij.emore.utils.DialogUtil;

import java.util.Map;

/**
 * Created by Caij on 2016/7/10.
 */
public class WeicoAuthUtil {

    public static boolean checkWeicoLogin(Activity activity, boolean isFinish) {
        if (!check()) {
            toAuthWeico(activity, isFinish);
            return false;
        }
        return true;
    }

    public static boolean checkWeicoLogin(Fragment fragment, boolean isFinish) {
        if (!check()) {
            toAuthWeico(fragment, isFinish);
            return false;
        }
        return true;
    }

    private static boolean check() {
        if (UserPrefs.get().getWeiCoToken() == null
                || UserPrefs.get().getWeiCoToken().getAccess_token() == null
/*                || UserPrefs.getDefault().getAccount().getWeiCoLoginResponse() == null
                || UserPrefs.getDefault().getAccount().getWeiCoLoginResponse().getGsid() == null*/) {
            return false;
        }
        return true;
    }

    /**
     * @param activity
     * @return request code  result code RESULT_OK
     */
    public static int toAuthWeico(final Activity activity, final boolean isFinish) {
        DialogUtil.showHintDialog(activity, activity.getString(R.string.hint),
                activity.getString(R.string.aouth_high_hint),
                activity.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Account account = UserPrefs.get().getAccount();
                        Intent intent = WeiCoLoginActivity.newWeiCoLoginIntent(activity,
                                account.getUsername(), account.getPwd());
                        activity.startActivityForResult(intent, Key.AUTH);
                 }
                }, activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isFinish) {
                            activity.finish();
                        }
                    }
                });
        return Key.AUTH;
    }

    public static int toAuthWeico(final Fragment fragment, final boolean isFinish) {
        final Activity activity = fragment.getActivity();
        DialogUtil.showHintDialog(activity, activity.getString(R.string.hint),
                activity.getString(R.string.aouth_high_hint),
                activity.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Account account = UserPrefs.get().getAccount();
                        Intent intent = WeiCoLoginActivity.newWeiCoLoginIntent(activity,
                                account.getUsername(), account.getPwd());
                        fragment.startActivityForResult(intent, Key.AUTH);
                    }
                }, activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isFinish) {
                            activity.finish();
                        }
                    }
                });
        return Key.AUTH;
    }

    public static int toAuthWeico(final Activity activity) {
        return toAuthWeico(activity, true);
    }

    public static int toAuthWeico(final Fragment fragment) {
        return toAuthWeico(fragment, true);
    }

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
