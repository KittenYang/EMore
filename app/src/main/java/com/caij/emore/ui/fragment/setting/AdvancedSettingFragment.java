package com.caij.emore.ui.fragment.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import com.caij.emore.R;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.ui.activity.DefaultFragmentActivity;
import com.caij.emore.ui.activity.login.EMoreLoginActivity;
import com.caij.emore.ui.fragment.AccountsFragment;
import com.caij.emore.ui.fragment.AppAboutFragment;
import com.caij.emore.utils.ActivityStack;
import com.caij.emore.utils.Init;
import com.caij.emore.utils.CacheUtils;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.ExecutorServiceUtil;


/**
 * 更多高级设置
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
		Preference pNotification =  findPreference(getString(R.string.key_setting_notify));
		pNotification.setOnPreferenceClickListener(this);
		Preference pFlow = findPreference("pFlow");
		pFlow.setOnPreferenceClickListener(this);
		cachePreference = findPreference(getString(R.string.setting_key_clear_cache));
		cachePreference.setOnPreferenceClickListener(this);
		Preference accountPf = findPreference(getString(R.string.key_setting_account));
		accountPf.setOnPreferenceClickListener(this);
		Preference exitPreference = findPreference(getString(R.string.setting_key_exit));
		exitPreference.setOnPreferenceClickListener(this);
		findPreference(getString(R.string.key_setting_about)).setOnPreferenceClickListener(this);
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
		if (getString(R.string.key_setting_notify).equals(preference.getKey())) {
			Intent intent = DefaultFragmentActivity.starFragment(getActivity(), getString(R.string.title_notification),
					NotificationSettingsFragment.class, null);
			startActivity(intent);
		}
		else if ("pFlow".equals(preference.getKey())) {
		}
		else if ("pAccount".equals(preference.getKey())) {
		}
        else if ("pOffline".equals(preference.getKey())) {

        }else if (getString(R.string.setting_key_exit).equals(preference.getKey())) {
			DialogUtil.showHintDialog(getActivity(), getString(R.string.hint), getString(R.string.exit_hint), getString(R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Init.getInstance().stop(getActivity());
					ActivityStack.getInstance().finishAllActivity();
					Process.killProcess(Process.myPid());
				}
			}, getString(R.string.cancel), null);
		}else if (getString(R.string.setting_key_clear_cache).equals(preference.getKey())) {
			DialogUtil.showHintDialog(getActivity(), getString(R.string.hint), getString(R.string.clear_cache_hint), getString(R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					clearCache();
				}
			}, getString(R.string.cancel), null);
		}else if (getString(R.string.key_setting_about).equals(preference.getKey())) {
			Intent intent = DefaultFragmentActivity.starFragmentV4(getActivity(), "关于", AppAboutFragment.class, null);
			startActivity(intent);
		}else if (getString(R.string.key_setting_account).equals(preference.getKey())) {
			Intent intent = DefaultFragmentActivity.starFragmentV4(getActivity(), getString(R.string.settings_account), AccountsFragment.class, null);
			startActivity(intent);
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
