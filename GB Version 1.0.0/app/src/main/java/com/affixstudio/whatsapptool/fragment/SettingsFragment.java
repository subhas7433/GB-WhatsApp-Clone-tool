package com.affixstudio.whatsapptool.fragment;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.model.utils.AutoStartHelper;
import com.affixstudio.whatsapptool.model.utils.ServieUtils;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);

        SwitchPreference showNotificationPref = findPreference(getString(R.string.pref_show_notification_replied_msg));
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M && showNotificationPref != null) {
            showNotificationPref.setTitle(getString(R.string.show_notification_label) + "(Beta)");
        }

        Preference autoStartPref = findPreference(getString(R.string.pref_auto_start_permission));
        if (autoStartPref != null) {
            autoStartPref.setOnPreferenceClickListener(preference -> {
                checkAutoStartPermission();
                return true;
            });
        }

        SwitchPreference foregroundServiceNotifPref = findPreference(getString(R.string.pref_show_foreground_service_notification));
        if (foregroundServiceNotifPref != null) {
            foregroundServiceNotifPref.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue.equals(true)) {
                    ServieUtils.getInstance(getActivity()).startNotificationService();
                } else {
                    ServieUtils.getInstance(getActivity()).stopNotificationService();
                }
                return true;
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null)
            getActivity().setTitle(R.string.settings);
    }

    private void checkAutoStartPermission() {
        if (getActivity() != null) {
            AutoStartHelper.getInstance().getAutoStartPermission(getActivity());
        }
    }

}
