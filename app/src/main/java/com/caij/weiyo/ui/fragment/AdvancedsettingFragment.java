package com.caij.weiyo.ui.fragment;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import com.caij.weiyo.R;
import com.caij.weiyo.utils.CacheUtils;


/**
 * 更多高级设置
 * 
 * @author Jeff.Wang
 *
 * @date 2014年10月21日
 */
public class AdvancedSettingFragment extends PreferenceFragment
									implements OnPreferenceChangeListener, OnPreferenceClickListener {

	private CheckBoxPreference pInnerBrowser;// 设置默认浏览器
	private Preference pNotification;// 通知中心
	private Preference pFlow;// 流量控制

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		addPreferencesFromResource(R.xml.ui_advanced_item);
		getActivity().setTitle("设置");
		pNotification = (Preference) findPreference("pNotification");
		pNotification.setOnPreferenceClickListener(this);
		pFlow = (Preference) findPreference("pFlow");
		pFlow.setOnPreferenceClickListener(this);
		pInnerBrowser = (CheckBoxPreference) findPreference("pInnerBrowser");
		pInnerBrowser.setOnPreferenceChangeListener(this);
		Preference preference = findPreference("clear_cache");
		preference.setSummary(CacheUtils.getCacheFileSizeString(getActivity()));
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if ("pNotification".equals(preference.getKey())) {
		}
		else if ("pFlow".equals(preference.getKey())) {
		}
		else if ("pAccount".equals(preference.getKey())) {
		}
        else if ("pOffline".equals(preference.getKey())) {
        }
		return true;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if ("pRotatePic".equals(preference.getKey())) {
		}
		else if ("pSendDelay".equals(preference.getKey())) {
		}
		else if ("pInnerBrowser".equals(preference.getKey())) {
			try {
			} catch (Exception e) {
			}
		}

		return true;
	}

	
}
