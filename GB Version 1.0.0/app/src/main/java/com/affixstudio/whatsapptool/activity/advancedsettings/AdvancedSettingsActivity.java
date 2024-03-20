package com.affixstudio.whatsapptool.activity.advancedsettings;

import android.os.Bundle;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activity.BaseActivity;

public class AdvancedSettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_settings);

        setTitle(R.string.advanced_settings);
    }
}
