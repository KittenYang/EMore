package com.caij.emore.ui.fragment.setting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.caij.emore.EMApplication;
import com.caij.emore.EventTag;
import com.caij.emore.R;
import com.caij.emore.bean.ThemeItem;
import com.caij.emore.ui.activity.DefaultFragmentActivity;
import com.caij.emore.ui.adapter.ThemeAdapter;
import com.caij.emore.ui.fragment.AccountsFragment;
import com.caij.emore.ui.fragment.AppAboutFragment;
import com.caij.emore.utils.ActivityStack;
import com.caij.emore.Init;
import com.caij.emore.utils.CacheUtils;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.ToastUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.weibo.ThemeUtils;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;

import java.util.ArrayList;
import java.util.List;


/**
 * 更多高级设置
 * 
 * @date 2014年10月21日
 */
public class AdvancedSettingFragment extends PreferenceFragment
									implements  OnPreferenceClickListener, Preference.OnPreferenceChangeListener, RecyclerViewOnItemClickListener {

	private Preference cachePreference;

	private Dialog mThemeDialog;
	private ThemeAdapter mThemeAdapter;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		addPreferencesFromResource(R.xml.fragment_advanced_item);
		Preference pNotification =  findPreference(getString(R.string.key_setting_notify));
		pNotification.setOnPreferenceClickListener(this);
		findPreference(getString(R.string.key_settings_theme)).setOnPreferenceClickListener(this);
		Preference pFlow = findPreference("pFlow");
		pFlow.setOnPreferenceClickListener(this);
		cachePreference = findPreference(getString(R.string.setting_key_clear_cache));
		cachePreference.setOnPreferenceClickListener(this);
		Preference accountPf = findPreference(getString(R.string.key_setting_account));
		accountPf.setOnPreferenceClickListener(this);
		Preference exitPreference = findPreference(getString(R.string.setting_key_exit));
		exitPreference.setOnPreferenceClickListener(this);
		findPreference(getString(R.string.key_setting_about)).setOnPreferenceClickListener(this);
		CheckBoxPreference theme = (CheckBoxPreference) findPreference(getString(R.string.key_setting_theme));
		theme.setOnPreferenceChangeListener(this);
		lodCacheSize();

		RecyclerView recyclerView = new RecyclerView(getActivity());
		recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
		int padding = getResources().getDimensionPixelOffset(R.dimen.spacing_medium);
		recyclerView.setPadding(padding, 0, padding, 0);

		final List<ThemeItem> themeItems = new ArrayList<>();
		themeItems.add(new ThemeItem(getResources().getColor(R.color.colorPrimary), false));
		themeItems.add(new ThemeItem(getResources().getColor(R.color.pink), false));
		themeItems.add(new ThemeItem(getResources().getColor(R.color.blue), false));
		themeItems.add(new ThemeItem(getResources().getColor(R.color.green), false));
		themeItems.add(new ThemeItem(getResources().getColor(R.color.yellow), false));
		themeItems.add(new ThemeItem(getResources().getColor(R.color.red), false));
		themeItems.add(new ThemeItem(getResources().getColor(R.color.grey), false));

		mThemeAdapter = new ThemeAdapter(getActivity(), themeItems);
		mThemeAdapter.setOnItemClickListener(this);
		recyclerView.setAdapter(mThemeAdapter);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		mThemeDialog = builder.setView(recyclerView)
				.setTitle("主题")
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						for (int i= 0; i < mThemeAdapter.getEntities().size(); i ++) {
							if (mThemeAdapter.getItem(i).isSelect()) {
								ThemeUtils.changeTheme(getActivity(), i);
								if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
									getActivity().recreate();
									RxBus.getDefault().post(EventTag.EVENT_MODE_NIGHT_UPDATE, null);
								}
								return;
							}
						}
					}
				})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.create();

		mThemeDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				for (ThemeItem item : mThemeAdapter.getEntities()) {
					item.setSelect(false);
				}
				themeItems.get(ThemeUtils.getThemePosition(getActivity())).setSelect(true);
				mThemeAdapter.notifyDataSetChanged();
			}
		});
	}

	private void lodCacheSize() {
		ExecutorServiceUtil.executeAsyncTask(new AsyncTask<Object, Object, String>() {
			@Override
			protected String doInBackground(Object... params) {
				return CacheUtils.getCacheFileSizeString(EMApplication.getInstance());
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

		} else if (getString(R.string.setting_key_exit).equals(preference.getKey())) {
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
		}else if (getString(R.string.key_settings_theme).equals(preference.getKey())) {
			if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
				ToastUtil.show(getActivity(), getString(R.string.night_mode_change_theme_hint));
			}else {
				mThemeDialog.show();
			}
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


	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (getString(R.string.key_setting_theme).equals(preference.getKey())) {
			boolean isNight = (boolean) newValue;
			if (isNight) {
				AppCompatDelegate.setDefaultNightMode(
						AppCompatDelegate.MODE_NIGHT_YES);
			} else {
				AppCompatDelegate.setDefaultNightMode(
						AppCompatDelegate.MODE_NIGHT_NO);
			}
			getActivity().recreate();
			RxBus.getDefault().post(EventTag.EVENT_MODE_NIGHT_UPDATE, newValue);
		}
		return true;
	}

	@Override
	public void onItemClick(View view, int position) {
		ThemeItem themeItem = mThemeAdapter.getItem(position);
		for (ThemeItem item : mThemeAdapter.getEntities()) {
			item.setSelect(false);
		}
		themeItem.setSelect(true);
		mThemeAdapter.notifyDataSetChanged();
	}
}
