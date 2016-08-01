package com.caij.emore.ui.fragment.setting;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.caij.emore.AppSettings;
import com.caij.emore.R;
import com.caij.emore.ui.activity.BaseActivity;
import com.caij.emore.utils.DateUtil;

public class NotificationSettingsFragment extends PreferenceFragment
		implements OnPreferenceClickListener, OnPreferenceChangeListener,OnCheckedChangeListener {

    private CheckBoxPreference pNotificationEnable;// 开启通知提醒
	private ListPreference pInterval;// 消息间隔
	private CheckBoxPreference pNightClose;// 夜间勿扰
	private CheckBoxPreference pStatusMention;// 提及微博
	private CheckBoxPreference pCommentMention;// 提及评论
	private CheckBoxPreference pFollower;// 粉丝
	private CheckBoxPreference pComment;// 评论
    private CheckBoxPreference pDm;// 私信
	private CheckBoxPreference pAttitude;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		addPreferencesFromResource(R.xml.fragment_notification_settings);

		BaseActivity activity = (BaseActivity) getActivity();
		activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(false);
//        activity.getSupportActionBar().setTitle(R.string.title_notification);
		
		setHasOptionsMenu(false);

        pNotificationEnable = (CheckBoxPreference) findPreference(getString(R.string.key_notification_enable));
        pNotificationEnable.setOnPreferenceChangeListener(this);

		pInterval = (ListPreference) findPreference(getString(R.string.key_message_interval));
		pInterval.setOnPreferenceChangeListener(this);

		setMessageIntervalSetting(AppSettings.getMessageIntervalValue(getActivity()));
		
		pNightClose = (CheckBoxPreference) findPreference(getString(R.string.key_night_close));

		pStatusMention = (CheckBoxPreference) findPreference(getString(R.string.key_status_mention));

		pCommentMention = (CheckBoxPreference) findPreference(getString(R.string.key_comment_mention));

		pFollower = (CheckBoxPreference) findPreference(getString(R.string.key_setting_follower));

		pComment = (CheckBoxPreference) findPreference(getString(R.string.key_setting_comment));

        pDm = (CheckBoxPreference) findPreference(getString(R.string.key_setting_dm));

		pAttitude = (CheckBoxPreference) findPreference(getString(R.string.key_setting_attitude));

		refreshSettings(AppSettings.isNotifyEnable(getActivity()));
	}
	
	private void refreshSettings(boolean isCheck) {
		pInterval.setEnabled(isCheck);
		pNightClose.setEnabled(isCheck);
		pStatusMention.setEnabled(isCheck);
		pCommentMention.setEnabled(isCheck);
		pFollower.setEnabled(isCheck);
		pComment.setEnabled(isCheck);
		pAttitude.setEnabled(isCheck);
        pDm.setEnabled(isCheck);
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		refreshSettings(isChecked);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		return true;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if ("pInterval".equals(preference.getKey())) {
			setMessageIntervalSetting(Integer.parseInt(newValue.toString()));
		}
        else if ("pNotificationEnable".equalsIgnoreCase(preference.getKey())) {
            onCheckedChanged(null, Boolean.parseBoolean(newValue.toString()));
        }
		return true;
	}

	private void setMessageIntervalSetting(long value) {
		pInterval.setSummary(DateUtil.formatTime(value));
	}

}
