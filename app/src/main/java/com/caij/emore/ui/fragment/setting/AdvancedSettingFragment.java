package com.caij.emore.ui.fragment.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.service.EMoreService;
import com.caij.emore.ui.activity.DefaultFragmentActivity;
import com.caij.emore.ui.activity.login.EMoreLoginActivity;
import com.caij.emore.utils.ActivityStack;
import com.caij.emore.utils.CacheUtils;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.EventUtil;
import com.caij.emore.utils.ExecutorServiceUtil;


/**
 * 更多高级设置
 * 
 * @author Jeff.Wang
 *
 * @date 2014年10月21日
 */
public class AdvancedSettingFragment extends PreferenceFragment
									implements  OnPreferenceClickListener {

	private Preference cachePreference;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		addPreferencesFromResource(R.xml.fragment_advanced_item);
		getActivity().setTitle("设置");
		Preference pNotification = (Preference) findPreference("pNotification");
		pNotification.setOnPreferenceClickListener(this);
		Preference pFlow = (Preference) findPreference("pFlow");
		pFlow.setOnPreferenceClickListener(this);
		CheckBoxPreference pInnerBrowser = (CheckBoxPreference) findPreference(getString(R.string.key_setting_browser));
		cachePreference = findPreference("clear_cache");
		cachePreference.setOnPreferenceClickListener(this);
		Preference changeAccount = findPreference("key_change_account");
		changeAccount.setOnPreferenceClickListener(this);
		Preference exitPreference = findPreference("key_exit");
		exitPreference.setOnPreferenceClickListener(this);
		lodCacheSize();
	}

	private void lodCacheSize() {
		ExecutorServiceUtil.executeAsyncTask(new AsyncTask<Object, Object, String>() {
			@Override
			protected String doInBackground(Object... params) {
				return CacheUtils.getCacheFileSizeString(getActivity());
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);
				cachePreference.setSummary(s);
			}
		});
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if ("pNotification".equals(preference.getKey())) {
			Intent intent = DefaultFragmentActivity.starFragment(getActivity(), NotificationSettingsFragment.class, null);
			startActivity(intent);
		}
		else if ("pFlow".equals(preference.getKey())) {
		}
		else if ("pAccount".equals(preference.getKey())) {
		}
        else if ("pOffline".equals(preference.getKey())) {

        }else if ("key_change_account".equals(preference.getKey())) {
			DialogUtil.showHintDialog(getActivity(), "提示", "是否切换账号", "确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ActivityStack.getInstance().remove(getActivity());
					ActivityStack.getInstance().finishAllActivity();
					Intent intent = EMoreLoginActivity.newEMoreLoginIntent(getActivity(), null, null);
					startActivity(intent);
					getActivity().finish();
					UserPrefs.get().clear();
				}
			}, "取消", null);
		}else if ("key_exit".equals(preference.getKey())) {
			DialogUtil.showHintDialog(getActivity(), "提示", "是否退出", "确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					EMoreService.stop(getActivity());
					ActivityStack.getInstance().finishAllActivity();
				}
			}, "取消", null);
		}else if ("clear_cache".equals(preference.getKey())) {
			DialogUtil.showHintDialog(getActivity(), "提示", "是否清楚缓存", "是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					clearCache();
				}
			}, "取消", null);
		}
		return true;
	}

	private void clearCache() {
		ExecutorServiceUtil.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
			@Override
			protected Object doInBackground(Object... params) {
				CacheUtils.clearCache(getActivity());
				return null;
			}

			@Override
			protected void onPostExecute(Object o) {
				super.onPostExecute(o);
				lodCacheSize();
			}
		});
	}

	
}
